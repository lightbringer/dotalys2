package de.lighti.model.game;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PositionDouble implements Cloneable {
    public double x;
    public double y;
    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    public PositionDouble( double x2, double y2 ) {
        x = x2;
        y = y2;
    }

    @Override
    public Object clone() {
        try {
            final PositionDouble clone = (PositionDouble) super.clone();
            return clone;
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException( e );
        }
    }

}
