package de.lighti.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.lighti.Dotalys2;
import de.lighti.model.Component;
import de.lighti.model.Encounter;
import de.lighti.model.Replay;
import de.lighti.model.Replay.GameState;
import de.lighti.model.game.Hero;
import de.lighti.model.state.ParseState;

public class EncounterTracker extends AbstractGameEventTracker {

    private static final long ENCOUNTER_TIMEOUT = 2000l;

    private static Component createComponent( Collection<Hero> allHeros, Hero current, long tick ) {
        Component component = null;

        final Set<Hero> herosWeveSeen = new HashSet<Hero>();
        final Stack<Hero> seed = new Stack<Hero>();
        seed.add( current );
        Map<Hero, Integer> tempMap = new HashMap<Hero, Integer>();

        while (!seed.isEmpty()) {
            current = seed.pop();
            herosWeveSeen.add( current );

            for (final Hero other : allHeros) {
                if (other == current) {
                    continue;
                }

                final double distance = current.distance( other, tick );

                if (current.isRadiant() != other.isRadiant() && distance <= current.getCombatRange()) {
                    if (component == null) {
                        component = new Component();
                    }
                    final Map<Hero, Integer> currentMap = getAdjacencyMap( component, current, tempMap );
                    currentMap.put( other, 1 );
                    final Map<Hero, Integer> otherMap = getAdjacencyMap( component, current, tempMap );
                    if (!otherMap.containsKey( current )) {
                        otherMap.put( current, 0 );
                    }

                    if (!herosWeveSeen.contains( other )) {
                        seed.add( other );
                    }
                    tempMap = null;
                }
                if (current.isRadiant() == other.isRadiant() && distance < current.getHealRange()) {
                    final Map<Hero, Integer> currentMap = getAdjacencyMap( component, current, tempMap );
                    currentMap.put( other, 2 );
                    final Map<Hero, Integer> otherMap = getAdjacencyMap( component, current, tempMap );
                    if (!otherMap.containsKey( current )) {
                        otherMap.put( current, 0 );
                    }

                    if (!herosWeveSeen.contains( other )) {
                        seed.add( other );
                    }
                }
                if (current.isRadiant() != other.isRadiant() && distance <= other.getCombatRange()) {
                    if (component == null) {
                        component = new Component();
                    }
                    final Map<Hero, Integer> otherMap = getAdjacencyMap( component, current, tempMap );
                    otherMap.put( other, 1 );
                    final Map<Hero, Integer> currentMap = getAdjacencyMap( component, current, tempMap );
                    if (!currentMap.containsKey( other )) {
                        currentMap.put( current, 0 );
                    }
                    if (!herosWeveSeen.contains( other )) {
                        seed.add( other );
                    }
                    tempMap = null;
                }
                if (current.isRadiant() == other.isRadiant() && distance < other.getHealRange()) {
                    final Map<Hero, Integer> otherMap = getAdjacencyMap( component, current, tempMap );
                    otherMap.put( other, 2 );
                    final Map<Hero, Integer> currentMap = getAdjacencyMap( component, current, tempMap );
                    if (!currentMap.containsKey( other )) {
                        currentMap.put( other, 0 );
                    }
                    if (!herosWeveSeen.contains( other )) {
                        seed.add( other );
                    }

                }
            }

        }

        return component;
    }

    private static Map<Hero, Integer> getAdjacencyMap( Component c, Hero h, Map<Hero, Integer> tempMap ) {
        if (c == null && tempMap != null) {
            return tempMap;
        }
        Map<Hero, Integer> m = c.getMembersAdjacency().get( h );
        if (m == null) {
            m = new HashMap<Hero, Integer>();
            c.getMembersAdjacency().put( h, m );

        }
        if (tempMap != null) {
            m.putAll( tempMap );
        }
        return m;
    }

    private static Encounter joinEncounters( List<Encounter> pred ) {
        final Encounter enc = new Encounter( 1000000000000l ); //WTF?
        for (final Encounter e : pred) {
            enc.join( e );
        }
        return enc;
    }

    private final Logger LOGGER = Logger.getLogger( EncounterTracker.class.getName() );

    private final Set<Encounter> openEncounter;
    private final Set<Encounter> closedEncounter;
    private final List<Encounter> predecessors;
    private final Set<Encounter> encounterToClose;

    public EncounterTracker( Dotalys2 app ) {
        super( app );

        openEncounter = new HashSet<Encounter>();
        closedEncounter = new HashSet<Encounter>();
        predecessors = new ArrayList<Encounter>();
        encounterToClose = new HashSet<Encounter>();

    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        final Replay replay = app.getAppState().getReplay();
        openEncounter.forEach( enc -> {
            enc.close();
            closedEncounter.add( enc );
        } );
        closedEncounter.forEach( e -> replay.addEncounter( e ) );

        //XXX ????
//     enc_m = {}
//     for h in replay.hero_names{
//         enc_m[h] = []
//     }

//     for e in replay.closed_encounters:
//     #    print 'start:%s, end : %s , duration : %s'%(e.first_tick, e.last_tick, e.last_tick-e.first_tick)
//     #    for i in e.members:
//     #        enc_m[e.members[i].heroname].append(e.times[i])
//         #print e.roles1
//         #print e.roles2
//     #    print ('1:g:%s , x: %s, d %s - 2:g:%s , x: %s, d %s')%(e.startgold1,e.startxp1, e.deaths1,e.startgold2,e.startxp2, e.deaths2,)
//         #for h in e.members:
//         #   print ('%s %s'%(e.members[h].db_id, e.members[h].heroname))
//         #for ls in e.linkstates:
//         #   print e.linkstates[ls]
//        e.animate()
    }

    @Override
    public void tickEnd( long tick, ParseState state ) {
        if (app.getAppState().getReplay().getGameStateTime( GameState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS ) == null) {
            // We're still in or before pick phase

            openEncounter.clear();
            closedEncounter.clear();
            return;
        }

        final Replay replay = app.getAppState().getReplay();
        final Collection<Hero> allHeros = ConcurrentHashMap.newKeySet();
        allHeros.addAll( replay.getHeroes() );

        for (final Hero current_hero : allHeros) {
            final Component currentComponent = createComponent( allHeros, current_hero, tick );
            if (currentComponent == null) {
                continue;
            }
            predecessors.clear();
            for (final Encounter enc : openEncounter) {
                if (enc.isSuccessor( currentComponent )) {
                    LOGGER.log( Level.FINEST, enc + " continues" );
                    predecessors.add( enc );
                }
            }
            //no predecessor : open new encounter
            if (predecessors.isEmpty()) {
                LOGGER.log( Level.FINEST, "A new encounter is started by " + current_hero );
                addEncounter( currentComponent, tick );
            }
            else if (predecessors.size() == 1) {
                predecessors.get( 0 ).addComponent( currentComponent, tick );
            }
            else {
                for (final Encounter enc : predecessors) {
                    openEncounter.remove( enc );
                    LOGGER.log( Level.FINEST, enc + " will be joined " );
                }
                final Encounter enc = joinEncounters( predecessors );
                enc.addComponent( currentComponent, tick );
                openEncounter.add( enc );
            }
            //TODO Can heroes be in multiple encounters at the same time?
            allHeros.removeAll( currentComponent.getMembersAdjacency().keySet() );
        }

        encounterToClose.clear();
        for (final Encounter enc : openEncounter) {
            if (tick - enc.getLastTick() >= ENCOUNTER_TIMEOUT) {
                enc.close();
                encounterToClose.add( enc );

                LOGGER.log( Level.FINEST, enc + " timed out" );
            }
        }
        for (final Encounter enc : encounterToClose) {
            closeEncounter( enc );
        }

    }

    private void addEncounter( Component cur_component, long tick ) {
        final Encounter enc = new Encounter( tick );
        enc.addComponent( cur_component, tick );
        openEncounter.add( enc );

    }

    private void closeEncounter( Encounter e ) {
        openEncounter.remove( e );
        closedEncounter.add( e );
    }

}
