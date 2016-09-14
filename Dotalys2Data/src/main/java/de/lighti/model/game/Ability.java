package de.lighti.model.game;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.SortNatural;

@Entity
public class Ability {
    @Column( name = "abilityId", nullable = false )
    private String key;

    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @ElementCollection
    private final SortedMap<Long, Integer> level;
    @ElementCollection
    @SortNatural
    private final SortedSet<Long> invocations;
    private int entityId;

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    public Ability( String key ) {
        super();
        this.key = key.toUpperCase();

        level = new TreeMap<Long, Integer>();
        invocations = new TreeSet<Long>();

    }

    public void addInvocation( long tickMs ) {
        invocations.add( tickMs );
    }

    public int getEntityId() {
        return entityId;
    }

    public SortedSet<Long> getInvocations() {
        return invocations;
    }

    public String getKey() {
        return key;
    }

    public NavigableMap<Long, Integer> getLevel() {
        return (NavigableMap<Long, Integer>) level;
    }

    public void setEntityId( int entityId ) {
        this.entityId = entityId;
    }

    public void setKey( String property ) {
        key = property;

    }

    public void setLevel( long tickMs, int level ) {
        if (this.level.isEmpty() || ((NavigableMap<Long, Integer>) this.level).floorEntry( tickMs ).getValue() < level) {
            this.level.put( tickMs, level );
        }
    }

    @Override
    public String toString() {
        return "Ability [key=" + key + "]";
    }

}
