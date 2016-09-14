package de.lighti.model.state;

import de.lighti.model.Entity;

public interface ParseState {

    Entity getEntity( int id );

    int getProtocolVersion();

    float getTickInterval();
}
