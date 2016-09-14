package de.lighti;

import de.lighti.model.AppState;
import de.lighti.model.Replay;
import de.lighti.parsing.EncounterTracker;
import de.lighti.parsing.GeneralGameStateTracker;
import de.lighti.parsing.HeroTracker;
import de.lighti.parsing.PlayersTracker;

public class Dotalys2ConsoleApp implements Dotalys2 {
    public static void main( String[] args ) {

        final Dotalys2ConsoleApp app = new Dotalys2ConsoleApp( args[0] );
        final DotaPlay parser = DotaPlayFactory.getInstance();
        parser.addListener( new PlayersTracker( app ) );
        parser.addListener( new GeneralGameStateTracker( app ) );
        parser.addListener( new EncounterTracker( app ) );
        parser.addListener( new HeroTracker( app ) );
        parser.loadFile( args[0] );

        app.state.getReplay().getPlayers().forEach( p -> System.out.println( p.getHero() ) );
    }

    private final AppState state;

    public Dotalys2ConsoleApp( String file ) {
        state = new AppState( this );
        state.setReplay( new Replay( file ) );
    }

    @Override
    public AppState getAppState() {
        return state;
    }

    @Override
    public void handleError( Throwable t ) {
        t.printStackTrace();

    }

}
