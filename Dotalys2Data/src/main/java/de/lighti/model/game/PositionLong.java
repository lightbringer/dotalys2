package de.lighti.model.game;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PositionLong implements Cloneable {
    public long x;
    public long y;
    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    public PositionLong( long x2, long y2 ) {
        x = x2;
        y = y2;
    }

    @Override
    public Object clone() {
        try {
            final PositionLong clone = (PositionLong) super.clone();
            return clone;
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException( e );
        }
    }

}
