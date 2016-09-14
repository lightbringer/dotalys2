package de.lighti.model.game;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.SortNatural;

import de.lighti.model.game.CombatEvent.Type;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@Access( AccessType.FIELD )
public abstract class Unit {
    public enum LIFE_STATE {
        ALIVE( 0 ), DYING( 1 ), DEAD( 2 ), RESPAWNABLE( 3 ), DISCARD_BODY( 4 );

        public static LIFE_STATE fromId( int i ) {
            for (final LIFE_STATE l : LIFE_STATE.values()) {
                if (l.id == i) {
                    return l;
                }
            }
            throw new IllegalArgumentException( "no such life state" );
        }

        private int id;;

        private LIFE_STATE( int i ) {
            id = i;
        }
    }

    /**
     * Standard Euclidean distance function
     *
     * @param myX
     * @param myY
     * @param theirX
     * @param theirY
     * @return
     */
    private static double euclidean( double myX, double myY, double theirX, double theirY ) {
        final double dx = myX - theirX;
        final double dy = myY - theirY;
        return Math.sqrt( dx * dx + dy * dy );
    }

    @ElementCollection
    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @OneToMany( cascade = javax.persistence.CascadeType.ALL)
    @JoinTable(name = "UNIT_ORIGIN", joinColumns = { @JoinColumn(name = "ID") }, inverseJoinColumns = { @JoinColumn(name = "UNIT_ID") })
    private SortedMap<Long, PositionInteger> origin;
    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    private boolean isRadiant;

    @ElementCollection
    private final Set<Integer> entityIds;
    @ElementCollection
    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @OneToMany( cascade = javax.persistence.CascadeType.ALL )
    protected SortedMap<Long, PositionInteger> xy;
    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @ElementCollection
    protected SortedMap<Long, Integer> stuns;
    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableSetType" )
    @ElementCollection
    protected SortedSet<Long> respawns;

    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @ElementCollection
    private SortedMap<Long, Zone> zones;
    @OneToMany( cascade = javax.persistence.CascadeType.ALL )
    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableSetType" )
    private final SortedSet<CombatEvent> combatLog;
    @Column( name = "unitKey" )
    private String key;
    private boolean isNeutral;
    private float totalStunTime;

    @OneToOne
    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @JoinColumn( name = "LAST_ATTACKER_ID" )
    private Unit lastAttacker;

    //Technically, these values are stored in the entity as well, but I've never seen them change
    private static final int CELL_BITS = 7;

    private static final int CELL_WIDTH = 1 << CELL_BITS;

    private static final int MAX_COORD = 16384;

    protected Unit( String key, int entityId ) {
        xy = new TreeMap<Long, PositionInteger>();
        zones = new TreeMap<Long, Zone>();
        respawns = new TreeSet<Long>();
        this.key = key.toUpperCase();
        entityIds = new HashSet<Integer>();
        entityIds.add( entityId );
        stuns = new TreeMap<Long, Integer>();
        origin = new TreeMap<Long, PositionInteger>();
        combatLog = new TreeSet<CombatEvent>( ( c1, c2 ) -> {
            if (c1.tick == c2.tick) {
                return Integer.compare( c1.hashCode(), c2.hashCode() );
            }
            else {
                return Long.compare( c1.tick, c2.tick );
            }
        } );
    }

    public void addDamage( long tickMs, Unit other, String inflictor, boolean isReceiving, int value ) {
        final CombatEvent e = new CombatEvent();
        e.source = isReceiving ? other : this;
        e.target = !isReceiving ? other : this;
        e.tick = tickMs;
        e.value = value;
        e.inflictor = inflictor;
        final PositionInteger p = getPosition( tickMs );
        if (p == null) {
            //Can't receive damage before spawned
            return;
        }
        e.x = p.x;
        e.y = p.y;
        e.type = isReceiving ? Type.DAMGE_RECEIVED : Type.DAMAGE_DONE;
        combatLog.add( e );
    }

    public void addDeath( long tickMs, int x, int y ) {
        addDeath( tickMs, x, y, null );
    }

    public void addDeath( long tickMs, int x, int y, Unit attacker ) {
        final CombatEvent death = new CombatEvent();
        death.source = attacker != null ? attacker : lastAttacker;
        death.target = this;
        death.tick = tickMs;
        death.x = x;
        death.y = y;

        death.type = Type.DEATH;
        combatLog.add( death );

    }

    public void addDeath( long tickMs, Unit attacker ) {
        final PositionInteger p = getPosition( tickMs );
        addDeath( tickMs, p.x, p.y, attacker );

    }

    public void addHealing( long tickMs, Unit other, String inflictor, boolean isReceiving, int value ) {
        final CombatEvent e = new CombatEvent();
        e.source = isReceiving ? other : this;
        e.target = !isReceiving ? other : this;
        e.tick = tickMs;
        e.value = -value;
        e.inflictor = inflictor;
        final PositionInteger p = getPosition( tickMs );
        e.x = p.x;
        e.y = p.y;
        e.type = isReceiving ? Type.HEAL_RECEIVED : Type.HEAL_DONE;
        combatLog.add( e );
    }

    public void addOriginVector( long time, float[] v ) {
        if (((NavigableMap<Long, PositionInteger>) xy).floorEntry( time ) == null) {
            //Ignore the first value. We loose a datapoint, but we don't have to worry that getAbsolutePosition will throw a NullPointer
            return;
        }
        origin.put( time, new PositionInteger( (int) v[0], (int) v[1] ) ); //ignore v[2] for now. Also: the origin vector seems to have no fractals
    }

    public void addRespawn( long ticks ) {
        respawns.add( ticks );
    }

    public void addStun( long time, float s ) {
        totalStunTime += s;
        BigDecimal b = new BigDecimal( s );
        b = b.movePointRight( 3 );
        stuns.put( time, b.intValue() );
    }

    public void addX( long tick, int x ) {
        if (xy.isEmpty()) {
            xy.put( tick, new PositionInteger( x, -1 ) );
        }
        else {
            PositionInteger p = xy.get( tick );
            if (p == null) {
                p = (PositionInteger) ((NavigableMap<Long, PositionInteger>) xy).floorEntry( tick ).getValue().clone();
                xy.put( tick, p );
            }
            p.x = x;
        }
        updateZone( tick );
    }

    public void addY( long tick, int y ) {
        if (xy.isEmpty()) {
            xy.put( tick, new PositionInteger( -1, y ) );
        }
        else {
            PositionInteger p = xy.get( tick );
            if (p == null) {
                p = (PositionInteger) ((NavigableMap<Long, PositionInteger>) xy).floorEntry( tick ).getValue().clone();
                xy.put( tick, p );
            }
            p.y = y;
        }
        updateZone( tick );
    }

    /**
     * Returns the Euclidian distance to the cell x,y of this unit at timestamp tickMs.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param tickMs the timestamp
     * @return the distance
     */
    public double distance( double x, double y, long tickMs ) {
        if (xy.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }
        final PositionInteger p = getPosition( tickMs );
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        }

        return euclidean( p.x, p.y, x, y );
    }

    /**
     * Returns the Euclidian distance between this hunitero and another at timestanp tickMs
     * Equivalent to distance(other.getX( tickMs ),other.getY( tickMs ), tickMs)
     *
     * @param other the other hero
     * @param tickMs the timestamp
     * @return the distance
     */
    public double distance( Unit other, long tickMs ) {
        if (other.xy.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }
        final PositionInteger p = other.getPosition( tickMs );
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        }

        return distance( p.x, p.y, tickMs );
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Unit other = (Unit) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        }
        else if (!key.equals( other.key )) {
            return false;
        }
        return true;
    }

    public int[] getAbsolutePosition( long time ) {
        final NavigableMap<Long, PositionInteger> tempXY = (NavigableMap<Long, PositionInteger>) xy;
        final NavigableMap<Long, PositionInteger> tempOrigin = (NavigableMap<Long, PositionInteger>) origin;
        final PositionInteger cell = tempXY.floorEntry( time ).getValue();
        final PositionInteger origin = tempOrigin.floorEntry( time ).getValue();
        final int x = cell.x * CELL_WIDTH - MAX_COORD + origin.x;
        final int y = cell.y * CELL_WIDTH - MAX_COORD + origin.y;

        return new int[] { x, y };
    }

    public TreeSet<CombatEvent> getCombatLog() {
        return (TreeSet<CombatEvent>) combatLog;

    }

    public List<CombatEvent> getDeaths() {
        return combatLog.stream().filter( c -> c.type == Type.DEATH ).collect( Collectors.toList() );
    }

    public int getDeaths( long ms ) {
        return (int) combatLog.stream().filter( l -> l.tick <= ms && l.type == Type.DEATH ).count();
    }

    public Set<Integer> getEntityIds() {
        return entityIds;
    }

    public String getKey() {
        return key;
    }

    public long getLastStunTime() {
        if (stuns.isEmpty()) {
            return 0l;
        }
        else {
            return stuns.lastKey();
        }
    }

    public Long getNextRespawn( long ticks ) {
        return ((NavigableSet<Long>) respawns).ceiling( ticks );
    }

    public NavigableMap<Long, PositionInteger> getOrigins() {
        return (NavigableMap<Long, PositionInteger>) origin;
    }

    public PositionInteger getPosition( long ms ) {
        final Entry<Long, PositionInteger> e = ((NavigableMap<Long, PositionInteger>) xy).floorEntry( ms );
        if (e != null) {
            return e.getValue();
        }
        else {
            return null;
        }
    }

    public Set<Long> getRespawns() {
        return Collections.unmodifiableSet( respawns );
    }

    public long getSpawnTime() {
        return xy.firstKey();
    }

    public NavigableMap<Long, Integer> getStuns() {
        return (NavigableMap<Long, Integer>) stuns;
    }

    /**
     * @return the cumulative time (in seconds) the unit has been stunned over the course of the whole game
     */
    public float getTotalStunTime() {
        return totalStunTime;
    }

    public NavigableMap<Long, PositionInteger> getXY() {
        return (NavigableMap<Long, PositionInteger>) xy;
    }

    public Zone getZone( long ms ) {
        return ((NavigableMap<Long, Zone>) zones).floorEntry( ms ).getValue();
    }

    public Map<Long, Zone> getZones() {
        return zones;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (key == null ? 0 : key.hashCode());
        return result;
    }

    public boolean isNeutral() {
        return isNeutral;
    }

    public boolean isRadiant() {
        return isRadiant;
    }

    public void setKey( String name ) {
        key = name.toUpperCase();
    }

    public void setLastAttacker( Unit attacker ) {
        lastAttacker = attacker;

    }

    public void setNeutral( boolean isNeutral ) {
        this.isNeutral = isNeutral;
    }

    public void setRadiant( boolean isRadiant ) {
        this.isRadiant = isRadiant;
    }

    public void setZones( TreeMap<Long, Zone> zones ) {
        this.zones = zones;
    }

    private void updateZone( long ms ) {
//        int x = getX( ms );
//        int y = getY( ms );
//        final Zone z;
//        if (x < 64 || y < 68) {
//            z = Zone.UNKNOWN;
//        }
//        else {
//            //Translate the coordinate to match it up with the label data
//            x -= 64;
//            y -= 68;
//
//            z = ZONE_DICTIONARY[x][y];
//        }
//        zones.put( ms, z );
    }
}
