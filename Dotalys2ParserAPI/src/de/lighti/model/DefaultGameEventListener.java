package de.lighti.model;

import de.lighti.GameEventListener;
import de.lighti.model.state.ParseState;

public abstract class DefaultGameEventListener implements GameEventListener {

    @Override
    public void entityCreated( long tickMs, Entity entity ) {

    }

    @Override
    public void entityRemoved( long tickMs, Entity removed ) {

    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {

    }

    @Override
    public void gameEvent( long timeStamp, GameEvent event ) {

    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {

    }

    @Override
    public void tickEnd( long tick, ParseState state ) {

    }

}
