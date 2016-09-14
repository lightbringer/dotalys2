package de.lighti;

import de.lighti.model.Entity;
import de.lighti.model.GameEvent;
import de.lighti.model.state.ParseState;

public interface GameEventListener {

    void entityCreated( long tickMs, Entity entity );

    void entityRemoved( long tickMs, Entity removed );

    <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue );

    void gameEvent( long timeStamp, GameEvent event );

    void parseComplete( long tickMs, ParseState state );

    void tickEnd( long tick, ParseState state );
}
