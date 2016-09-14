package de.lighti;

import java.util.Collection;

public interface DotaPlay {
    public interface ProgressListener {

        void bytesRemaining( int position );

    }

    void addListener( GameEventListener l );

    Collection<GameEventListener> getListeners();

    void loadFile( String absolutePath, ProgressListener... listeners );

}
