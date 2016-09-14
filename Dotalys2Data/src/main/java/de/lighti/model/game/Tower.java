package de.lighti.model.game;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access( AccessType.FIELD )
public class Tower extends Unit {

    private long lastUpdate;

    public Tower( String name, int id ) {
        super( name, id );

    }

    @Override
    public void addX( long tick, int x ) {
        if (lastUpdate <= tick) {
            super.addX( 0, x );
            lastUpdate = tick;
        }
    }

    @Override
    public void addY( long tick, int y ) {
        if (lastUpdate <= tick) {
            super.addY( 0, y );
            lastUpdate = tick;
        }
    }

    @Override
    public String toString() {
        return "Tower [getName()=" + getKey() + "]";
    }

}
