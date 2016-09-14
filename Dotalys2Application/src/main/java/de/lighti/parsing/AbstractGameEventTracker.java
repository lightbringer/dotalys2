package de.lighti.parsing;

import de.lighti.Dotalys2;
import de.lighti.GameEventListener;
import de.lighti.model.AppState;
import de.lighti.model.DefaultGameEventListener;

/**
 * Base class for all {@link GameEventListener} used by the Dotalys2 aplication. Adds some references
 * that are not necessary for the parsing but for updating the application state in return.
 *
 * @author Tobias Mahlmann
 *
 */
public abstract class AbstractGameEventTracker extends DefaultGameEventListener {
    protected final AppState appState;
    protected final Dotalys2 app;

    protected AbstractGameEventTracker( Dotalys2 app ) {
        super();
        this.app = app;
        appState = app.getAppState();
    }

}
