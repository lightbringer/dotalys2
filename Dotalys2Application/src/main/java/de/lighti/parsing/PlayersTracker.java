package de.lighti.parsing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Player;
import de.lighti.model.game.Player.KillingSpree;
import de.lighti.model.state.ParseState;

/**
 * The game keeps re-creating an entity CDOTA_PlayerResource throughout a match, hence it's entity id is. This volatile
 * more or less meaningless. Instead, this entity holds various vectors whose fields have to be tracked
 * and mapped to our own Player objects.
 *
 * @author Tobias Mahlmann
 *
 */
public class PlayersTracker extends AbstractGameEventTracker {

    private final Pattern playerPattern;

    public PlayersTracker( Dotalys2 app ) {
        super( app );

        playerPattern = Pattern.compile( "\\.([0-9][0-9][0-9][0-9])\\.(\\D+)$" );

    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        boolean dire = false;
        switch (e.getEntityClass().getName()) {
            case "CDOTA_DataDire":
                dire = true;
            case "CDOTA_DataRadiant":
            case "CDOTA_PlayerResource":
                for (final Map.Entry<String, Object> p : e.getProperties().entrySet()) {
                    handleWorldVar( tickMs, p.getKey(), p.getValue(), null, dire );
                }
                break;
            default:
                break;
        }

    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        boolean dire = false;
        switch (e.getEntityClass().getName()) {
            case "CDOTA_DataDire":
                dire = true;
            case "CDOTA_DataRadiant":
            case "CDOTA_PlayerResource":
                for (final Map.Entry<String, Object> p : e.getProperties().entrySet()) {
                    handleWorldVar( tickMs, p.getKey(), p.getValue(), null, dire );
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        for (final Iterator<Player> pI = appState.getReplay().getPlayers().iterator(); pI.hasNext();) {
            final Player p = pI.next();
            if (p.getHero() == null) {
                pI.remove();
            }
        }
    }

    /**
     * @param time
     * @param name
     * @param value
     * @param oldValue
     * @param dire in Dota2 pre-reborn, players were layed out flat in CDOTA_PlayerResource. They're still there, but
     * most data is either in CDOTA_DataRadiant or CDOTA_DataDire, where players are numbered from 0 .. 4 in both objects. Hence we just add
     * 5 if dire is set to true
     */
    private void handleWorldVar( long time, String name, Object value, Object oldValue, boolean dire ) {

        final Matcher m = playerPattern.matcher( name );
        if (m.find()) {

            final String idString = m.group( 1 );
            String valueName = m.group( 2 );
            int id = Integer.valueOf( idString );
            if (dire) {
                id += 5;
            }
            Player p = appState.getReplay().getPlayer( id );
            if (p == null) {
                p = new Player( Integer.valueOf( idString ) );
                appState.getReplay().addPlayer( p );
            }

            if (valueName.indexOf( '.' ) != -1) {
                valueName = valueName.substring( valueName.indexOf( '.' ) + 1 );
            }

            switch (valueName) {
                case "m_iszPlayerName":

                    p.setName( (String) value );
                    break;
                case "m_iTotalEarnedGold":
                    if (p.getHero() != null) {
                        p.getHero().setGold( time, (int) value );
                    }
                    break;
                case "m_iTotalEarnedXP":
                    if (p.getHero() != null) {
                        p.getHero().setXP( time, (int) value );
                    }
                    break;
                case "m_fStuns":
                    if (p.getHero() != null) {
                        final float stunTime = (float) value;
                        if (stunTime > p.getHero().getTotalStunTime()) {
                            p.getHero().addStun( time, stunTime - p.getHero().getTotalStunTime() ); //stunTime is in seconds and an acuumulated value

                        }
                    }
                    break;
                case "m_iStreak":
                    try {
                        p.setStreak( time, KillingSpree.ofValue( (int) value ) );
                    }
                    catch (final ArrayIndexOutOfBoundsException e) {
                        Logger.getLogger( PlayersTracker.class.getName() )
                                        .warning( "Player " + p.getId() + " has an invalid streak value of " + value + ". Bug?" );
                    }
                    break;
                case "m_hSelectedHero":
                    p.setHeroID( Entity.getIndexForReference( (int) value ) );
                    p.setHero( appState.getReplay().getHeroByEntity( p.getHeroID() ) );
                    break;
                case "m_iPlayerTeams":
                    p.setRadiant( (Integer) value == 2 ); //2 = Radiant, 3 = Dire, 5 = Spectator
                    break;

                default:
                    Map<String, Object> tickMap = appState.getReplay().gameEventsPerMs.get( time );
                    if (tickMap == null) {
                        tickMap = new HashMap<String, Object>();
                        appState.getReplay().gameEventsPerMs.put( time, tickMap );
                    }
                    tickMap.put( name, value );
                    appState.getReplay().addPlayerVariable( valueName );
                    break;

            }

        }

    }

}
