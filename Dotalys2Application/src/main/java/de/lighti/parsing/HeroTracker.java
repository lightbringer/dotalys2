package de.lighti.parsing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.lighti.Dotalys2;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.GameEvent;
import de.lighti.model.game.Creep;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.HeroRole;
import de.lighti.model.game.Player;
import de.lighti.model.game.Unit;
import de.lighti.model.game.Unit.LIFE_STATE;
import de.lighti.model.state.ParseState;

/**
 * This class tracks the movement, ability invocations, and item uses of heroes.
 * Abilities and items are cached in this class until parsing is completed. Then, the buffer
 * is used to update the {@link AppState}. This is necessary as sometimes the related entities (items,
 * abilities, etc.) are created after the owning entity, i.e. the hero, is updated.
 *
 * @author Tobias Mahlmann
 *
 */
public class HeroTracker extends UnitTracker {

    private final static int NULL_ILLUSION_HANDLE = 16777215;

    //caching data structures have no meaning outside the parsing process
    private final Map<Hero, Integer[]> itemCache;

    private final Map<Hero, Set<Integer>> abilityCache;

    public HeroTracker( Dotalys2 app ) {
        super( app );

        itemCache = new HashMap<Hero, Integer[]>();
        abilityCache = new HashMap<Hero, Set<Integer>>();
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().startsWith( "CDOTA_Unit_Hero_" )) {
            if (HeroRole.rolesForHero( e.getEntityClass().getName() ) == 0) {
                Logger.getLogger( HeroTracker.class.getName() ).warning( e.getEntityClass().getName() + " is not a hero" );
                return; //Happens for hero sub units, e.g. beast master's hawk
            }
            if (NULL_ILLUSION_HANDLE != (int) e.getProperty( "m_hReplicatingOtherHeroModel" )) {
                //This is an illusion, we might care about one day
                return;
            }
            Hero h = appState.getReplay().getHeroByEntity( e.getId() );

            if (h == null) {
                h = new Hero( e.getEntityClass().getName(), e.getId() );
                appState.getReplay().addHero( h );
                for (final Player p : appState.getReplay().getPlayers()) {
                    if (p.getHeroID() == e.getId()) {
                        p.setHero( h );
                        break;
                    }
                }

                final int team = (int) e.getProperty( "m_iTeamNum" );
                setTeam( h, team );

                final int x = (int) e.getProperty( Entity.CELL_X );
                final int y = (int) e.getProperty( Entity.CELL_Y );
                h.addX( tickMs, x );
                h.addY( tickMs, y );
            }
            activeUnits.put( e.getId(), h );

            final int team = e.getProperty( "m_iTeamNum" );
            h.setRadiant( team == 2 ); //2 = Radiant, 3 = Dire, 5 = Spectator

        }
        super.entityCreated( tickMs, e );
    }

    @Override
    public void entityRemoved( long tickMs, Entity removed ) {
        if (removed.getEntityClass().getName().startsWith( "CDOTA_Unit_Hero_" )) {
            activeUnits.remove( removed.getId() );
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        super.entityUpdated( tickMs, e, name, oldValue );
        if (e.getEntityClass().getName().startsWith( "CDOTA_Unit_Hero_" )) {
            if (HeroRole.rolesForHero( e.getEntityClass().getName() ) == 0) {
                return; //Happens for hero sub units, e.g. beast master's hawk
            }
            if (NULL_ILLUSION_HANDLE != (int) e.getProperty( "m_hReplicatingOtherHeroModel" )) {
                //This is an illusion, we might care about one day
                return;
            }

            final Hero h = appState.getReplay().getHeroByEntity( e.getId() );
            switch (name) {
                case Entity.CELL_X:
                    h.addX( tickMs, (Integer) e.getProperty( name ) );
                    break;

                case Entity.CELL_Y:
                    h.addY( tickMs, (Integer) e.getProperty( name ) );
                    break;
                case "CBodyComponent.m_vecOrigin":
                    final float[] o = e.getProperty( name );
                    h.addOriginVector( tickMs, o );
                    break;

                case "m_lifeState":
                    final LIFE_STATE oldState = oldValue != null ? LIFE_STATE.fromId( (Integer) oldValue ) : LIFE_STATE.ALIVE; // Only null on first update
                    final LIFE_STATE newValue = LIFE_STATE.fromId( (int) e.getProperty( name ) );
                    if (newValue == LIFE_STATE.ALIVE && oldState != LIFE_STATE.ALIVE) {
                        h.addRespawn( tickMs );
                    }
                    break;
                default:
                    if (name.contains( "m_hItems" )) {
                        final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                        int value = (int) e.getProperty( name );
                        if (value != 0x1FFFFF) {
                            value = Entity.getIndexForReference( value );
                            setItemInCache( h, tickMs, slot, value );
                        }
                        else {
                            setItemInCache( h, tickMs, slot, null );
                        }
                    }
                    else if (name.contains( "m_hAbilities" )) {
                        int value = (int) e.getProperty( name );
                        if (value != 0x1FFFFF) {

//                    final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                            value = Entity.getIndexForReference( value );
                            setAbilityInCache( h, value );
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void gameEvent( long timeStamp, GameEvent event ) {

        final String attackerName = event.getAttackerName() != null ? event.getAttackerName().replace( "npc_dota", "CDOTA_UNIT" ).toUpperCase() : null;
        final String targetName = event.getTargetName() != null ? event.getTargetName().replace( "npc_dota", "CDOTA_UNIT" ).toUpperCase() : null;
        if (event.getType() != null && attackerName != null && targetName != null && targetName.startsWith( "CDOTA_UNIT_HERO_" )) {
            Unit attacker = null;
            for (final Hero h : appState.getReplay().getHeroes()) {
                if (h.getKey().equals( attackerName )) {
                    attacker = h;
                }
            }
            if (attacker == null) {
                attacker = new Creep( attackerName, 0 );
                attacker.addX( timeStamp, -1 );
                attacker.addY( timeStamp, -1 );
            }
            for (final Hero target : appState.getReplay().getHeroes()) {
                if (target.getKey().equals( targetName )) {
                    switch (event.getType()) {
//                            case Buff_applied:
//                                break;
//                            case Buff_removed:
//                                break;
                        case Damage:
                            target.addDamage( timeStamp, attacker, event.getInflictorName(), true, event.getValue() );
                            attacker.addDamage( timeStamp, target, event.getInflictorName(), false, event.getValue() );
                            break;
                        case Death:
                            target.addDeath( timeStamp, attacker );
                            break;
                        case Heal:
                            target.addHealing( timeStamp, attacker, event.getInflictorName(), true, event.getValue() );
                            attacker.addHealing( timeStamp, target, event.getInflictorName(), false, event.getValue() );
                            break;
                        default:
                            break;

                    }

                    return;
                }
            }
        }

    }

    @Override
    public void tickEnd( long tick, ParseState state ) {
        //Abilities
        for (final Map.Entry<Hero, Set<Integer>> e : abilityCache.entrySet()) {
            for (final Integer i : e.getValue()) {
                e.getKey().getAbilities().add( appState.getReplay().getAbility( i ) );
            }
        }

        //Items
        for (final Hero h : itemCache.keySet()) {
            final Integer[] heroItems = itemCache.get( h );

            for (int slot = 0; slot < heroItems.length; slot++) {
                if (heroItems[slot] != null) {
                    final Dota2Item i = appState.getReplay().getItem( heroItems[slot] );

                    h.setItem( tick, slot, i );
                }
                else {
                    h.setItem( tick, slot, null );
                }
            }

        }

        abilityCache.clear();
        itemCache.clear();
    }

    private void setAbilityInCache( Hero h, int value ) {
        Set<Integer> abilities = abilityCache.get( h );
        if (abilities == null) {
            abilities = new HashSet<Integer>();
            abilityCache.put( h, abilities );
        }
        abilities.add( value );
    }

    private void setItemInCache( Hero h, long tickMs, int slot, Integer value ) {
        Integer[] items = itemCache.get( h );
        if (items == null) {
            items = new Integer[Hero.BAG_SIZE];
            itemCache.put( h, items );
        }

        items[slot] = value;

    }

}
