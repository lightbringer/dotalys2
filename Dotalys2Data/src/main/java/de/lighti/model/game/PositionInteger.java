package de.lighti.model.game;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PositionInteger implements Cloneable {
    public int x;
    public int y;
    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    public PositionInteger( int x2, int y2 ) {
        x = x2;
        y = y2;
    }

    @Override
    public Object clone() {
        try {
            final PositionInteger clone = (PositionInteger) super.clone();
            return clone;
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String toString() {
        return "PositionInteger [x=" + x + ", y=" + y + "]";
    }

}
