package de.lighti.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.lighti.model.game.Hero;
import de.lighti.model.game.HeroRole;

@Entity
@Access( AccessType.PROPERTY )
public class Encounter {

    private final Set<Hero> members;
    private final Map<Hero, long[]> times;
    private final Map<Hero, int[]> linkstates;
    private final int[][] all_links;
    private final int[] roles1;
    private final int[] roles2;
    private final Map<Long, Set<Component>> components;
    private long first_tick;
    private long lastTick;
    private int deaths1;
    private int startgold1;
    private int startxp1;
    private int damage1;
    private int deaths2;
    private int startgold2;
    private int startxp2;
    private int endgold1;
    private int endxp1;
    private int endgold2;
    private int endxp2;
    private int damage2;

    // Hibernate ID
    private long id;

    public Encounter( long tick ) {
        setLastTick( tick );
        first_tick = tick;
        components = new HashMap<Long, Set<Component>>();
        times = new HashMap<Hero, long[]>();
        linkstates = new HashMap<Hero, int[]>();
        roles1 = new int[10];
        roles2 = new int[10];
        all_links = new int[2][6];
        members = new HashSet<Hero>();
    }

    public void addComponent( Component component, long tick ) {
        assert component.getMembersAdjacency().size() >= 2;
        setLastTick( tick );
        for (final Hero h : component.getMembersAdjacency().keySet()) {

            if (members.contains( h )) {
                final long[] t = times.get( h );
                t[1] = tick;
            }
            else {
                times.put( h, new long[] { tick, tick } );
                members.add( h );
                linkstates.put( h, new int[6] );
            }

        }
        if (components.containsKey( tick )) {
            components.get( tick ).add( component );
        }
        else {
            final Set<Component> l = new HashSet<Component>();
            l.add( component );
            components.put( tick, l );
        }
    }

    public void close() {
        setLastTick( getLastTick() + 1000 );
        for (final Map.Entry<Long, Set<Component>> e : components.entrySet()) {
            final long tick = e.getKey();
            final Set<Component> complist = e.getValue();
            for (final Component comp : complist) {
                for (final Hero hero : comp.getMembersAdjacency().keySet()) {

                    final int team = hero.isRadiant() ? 0 : 1;
                    final int[] ls = linkstates.get( hero );
                    boolean heal = false;
                    boolean combat = false;
                    for (final Hero hero2 : comp.getMembersAdjacency().keySet()) {
                        if (comp.adjacency( hero, hero2 ) == 1) {
                            all_links[team][0] += 1;
                            ls[0] += 1;
                            combat = true;
                        }
                        if (comp.adjacency( hero, hero2 ) == 2) {
                            all_links[team][1] += 1;
                            ls[1] += 1;
                            heal = true;
                        }
                        if (comp.adjacency( hero2, hero ) == 1) {
                            all_links[team][2] += 1;
                            ls[2] += 1;
                            combat = true;
                        }
                        if (comp.adjacency( hero2, hero ) == 2) {
                            all_links[team][3] += 1;
                            ls[3] += 1;
                            heal = true;
                        }
                    }
                    if (combat) {
                        all_links[team][4] += 1;
                        ls[4] += 1;
                    }
                    if (heal) {
                        all_links[team][5] += 1;
                        ls[5] += 1;
                    }
                }

            }

        }
        final float tt = getLastTick() - first_tick;
        if (tt == 0f) {
            return;
        }

        for (final Hero hero : members) {
            final long[] times = this.times.get( hero );
            final int xp_s = hero.getXP( times[0] );
            final int gold_s = hero.getGold( times[0] );
            final int xp_t = hero.getXP( times[1] );
            final int gold_t = hero.getGold( times[1] );
            final int deaths = hero.getDeaths( times[1] ) - hero.getDeaths( times[0] );
//XXX            final int damage = hero.getDamage( times[1] ) - hero.getDamage( times[0] );
            final long h_time = times[1] - times[0];
            final int roles = HeroRole.rolesForHero( hero );
            if (hero.isRadiant()) {
                startgold1 += gold_s;
                startxp1 += xp_s;
                endgold1 += gold_t;
                endxp1 += xp_t;
                deaths1 += deaths;
//XXX                damage1 += damage;
//XXX                roles1+= roles*(h_time/tt); //????
            }
            else {
                startgold2 += gold_s;
                startxp2 += xp_s;
                endgold2 += gold_t;
                endxp2 += xp_t;
                deaths2 += deaths;
//XXX                damage2 += damage;
//XXX                roles2+= roles*(h_time/tt); //????
            }
        }
    }

    public long getLastTick() {
        return lastTick;
    }

    public boolean isSuccessor( Component component ) {
        boolean team1 = false;
        boolean team2 = false;
        for (final Hero h : component.getMembersAdjacency().keySet()) {
            if (h.isRadiant() && members.contains( h )) {
                team1 = true;
            }
            else if (!h.isRadiant() && members.contains( h )) {
                team2 = true;
            }
            if (team1 && team2) {
                break;
            }
        }
        return team1 && team2;
    }

    public void join( Encounter enc ) {
        first_tick = Math.min( enc.first_tick, first_tick );
        for (final Hero h : enc.members) {
            final long[] startStop = enc.times.get( h );
            if (times.containsKey( h )) {
                final long[] u = times.get( h );

                first_tick = Math.min( u[0], Math.min( first_tick, startStop[0] ) );
                u[0] = Math.min( startStop[0], u[0] );
                u[1] = Math.max( startStop[1], u[1] );
            }
            else {
                times.put( h, startStop );
                members.add( h );
                linkstates.put( h, Arrays.copyOf( enc.linkstates.get( h ), enc.linkstates.get( h ).length ) );
            }

        }
        //join components
        for (final long tick : enc.components.keySet()) {
            if (components.containsKey( tick )) {
                final Set<Component> l = components.get( tick );
                l.addAll( enc.components.get( tick ) );
            }
            else {
                components.put( tick, new HashSet<Component>( enc.components.get( tick ) ) ); //clone components as well?
            }
        }
    }

    public void setLastTick( long lastTick ) {
        this.lastTick = lastTick;
    }

    @Override
    public String toString() {
        return "Encounter [members=" + members + ", times=" + times + ", linkstates=" + linkstates + ", all_links=" + Arrays.toString( all_links ) + ", roles1="
                        + Arrays.toString( roles1 ) + ", roles2=" + Arrays.toString( roles2 ) + ", components=" + components + ", first_tick=" + first_tick
                        + ", lastTick=" + lastTick + ", deaths1=" + deaths1 + ", startgold1=" + startgold1 + ", startxp1=" + startxp1 + ", damage1=" + damage1
                        + ", deaths2=" + deaths2 + ", startgold2=" + startgold2 + ", startxp2=" + startxp2 + ", endgold1=" + endgold1 + ", endxp1=" + endxp1
                        + ", endgold2=" + endgold2 + ", endxp2=" + endxp2 + ", damage2=" + damage2 + ", id=" + id + "]";
    }

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long getId() {
        return id;
    }

    @SuppressWarnings( "unused" )
    private void setId( long id ) {
        this.id = id;
    }
}
