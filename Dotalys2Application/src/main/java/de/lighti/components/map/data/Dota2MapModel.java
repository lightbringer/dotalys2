package de.lighti.components.map.data;

import java.awt.event.ActionListener;

public interface Dota2MapModel extends ActionListener {

    int getPlaybackSpeed();

    void setActive( Object userObject, boolean selected );

    void setTime( int value );

}
