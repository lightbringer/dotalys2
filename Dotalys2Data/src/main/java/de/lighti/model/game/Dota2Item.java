package de.lighti.model.game;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dota2Item {
    @Column( name = "itemId" )
    private String key;
    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    @ElementCollection
    private final List<Long> usage;

    public Dota2Item( String key ) {
        super();

        this.key = key;
        usage = new ArrayList<Long>();
    }

    public void addUsage( long time ) {
        if (time < 0l) {
            throw new IllegalArgumentException( "time out of range" );
        }

        usage.add( time );
    }

    public String getKey() {
        return key;
    }

    public List<Long> getUsage() {
        return usage;
    }

    public void setKey( String key ) {

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException( "key must not be empty" );
        }
        this.key = key;
    }

}
