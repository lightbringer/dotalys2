package de.lighti;

import de.lighti.model.AppState;

/**
 * Main application interface to be passed into DataImporter. Allows us to
 * stuff the desktop app, batchh application, console version, etc. in there
 * @author Tobias Mahlmann
 *
 */
public interface Dotalys2 {

    default void enableSave( boolean maySave ) {
    }

    /**
     * @return the application's internal state
     */
    AppState getAppState();

    void handleError( Throwable t );

}
