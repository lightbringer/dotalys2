package de.lighti.parsing;

import javax.swing.JOptionPane;

import de.lighti.Dotalys2;
import de.lighti.Dotalys2App;
import de.lighti.io.DataImporter;
import de.lighti.model.Entity;
import de.lighti.model.Replay.GameState;
import de.lighti.model.Statics;
import de.lighti.model.state.ParseState;

public class GeneralGameStateTracker extends AbstractGameEventTracker {

    private GameState lastSeenGameState;

    public GeneralGameStateTracker( Dotalys2 app ) {
        super( app );
        lastSeenGameState = GameState.DOTA_GAMERULES_STATE_INIT; //See comment in entityUpdated
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        super.entityUpdated( tickMs, e, name, oldValue );

        /*
         * The intended order of GameState the game is supposed to go through is:
         * DOTA_GAMERULES_STATE_INIT
         * DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD
         * DOTA_GAMERULES_STATE_HERO_SELECTION
         * DOTA_GAMERULES_STATE_PRE_GAME
         * DOTA_GAMERULES_STATE_GAME_IN_PROGRESS
         * DOTA_GAMERULES_STATE_POST_GAME
         * (You can see that if you turn up the verbosity in the game's console while playing))
         * For some reason unknown to me, the GameRulesProxy entity is cycling between DOTA_GAMERULES_STATE_INIT/DOTA_GAMERULES_STATE_GAME_IN_PROGRESS
         * while the game runs. To avoid log clutter, I only accept non-DOTA_GAMERULES_STATE_INIT states as new.
         *
         */
        if (tickMs > 0l && e.getEntityClass().getName().equals( "CDOTAGamerulesProxy" )) {
            if (name.contains( "m_nGameState" )) {

                final GameState s = GameState.fromInternal( (int) e.getProperty( name ) );
                if (s != lastSeenGameState && s != GameState.DOTA_GAMERULES_STATE_INIT) {
                    app.getAppState().getReplay().addGameStateChange( s, tickMs );
                    lastSeenGameState = s;
                }
            }
        }
    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        if (state.getProtocolVersion() > Statics.SUPPORTED_PROTOCOL_VERSION) {
            JOptionPane.showMessageDialog( (Dotalys2App) app, DataImporter.getName( "PROTOCOL_WARNING" ), DataImporter.getName( "WARNING" ),
                            JOptionPane.WARNING_MESSAGE );
        }
        app.getAppState().getReplay().setGameVersion( state.getProtocolVersion() );
        app.getAppState().getReplay().setMsPerTick( (int) (state.getTickInterval() * 1000f) );

        //Clean up
        lastSeenGameState = null;

    }
}
