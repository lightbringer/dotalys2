package de.lighti.model.game;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.SortNatural;

@Entity
@Access( AccessType.FIELD )
public class Player {
    public enum KillingSpree {
        NONE( 0 ), KILLING_SPREE( 3 ), // � 3 kills
        DOMINATING( 4 ), // � 4 kills
        MEGA_KILL( 5 ), // � 5 kills
        UNSTOPPABLE( 6 ), // � 6 kills
        WICKED_SICK( 7 ), // � 7 kills
        MONSTER_KILL( 8 ), // � 8 kills
        GODLIKE( 9 ), // � 9 kills
        BEYOND_GODLIKE( 10 );// � 10 kills and above

        public static KillingSpree ofValue( int value ) {
            return values()[value];

        }

        public final int kills;

        private KillingSpree( int i ) {
            kills = i;
        }
//    Other:
//
//        Ownage � 5 or more kills in a row by one team without them losing any heroes.

    }

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;

    private String name;

    private int playerId;
    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @OneToOne
    private Hero hero;

    private boolean isRadiant;

    @SortNatural
    @Cascade( { CascadeType.ALL } )
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @ElementCollection
    private final SortedMap<Long, KillingSpree> streak;

    private int heroID;

    public Player( int id ) {

        playerId = id;
        streak = new TreeMap<Long, KillingSpree>();
        streak.put( 0l, KillingSpree.NONE );
        heroID = -1;

    }

    public int getEarnedGold( long time ) {
        return hero.getGold( time );
    }

    public Hero getHero() {
        return hero;
    }

    public int getHeroID() {
        return heroID;
    }

    public KillingSpree getHighestStreak() {
        return streak.values().stream().max( ( e1, e2 ) -> Integer.compare( e1.ordinal(), e2.ordinal() ) ).orElse( KillingSpree.NONE );
    }

    public int getId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public NavigableMap<Long, KillingSpree> getStreaks() {
        return (NavigableMap<Long, KillingSpree>) streak;

    }

    public KillingSpree getStreaks( long ms ) {
        return ((NavigableMap<Long, KillingSpree>) streak).floorEntry( ms ).getValue();
    }

    public int getTotalEarnedGold() {
        return hero.getTotalGold();
    }

    public int getTotalXP() {
        return hero.getTotalXP();
    }

    public int getXP( long time ) {
        return hero.getXP( time );
    }

    public boolean isRadiant() {
        return isRadiant;
    }

    public void setHero( Hero hero ) {
        this.hero = hero;
    }

    public void setHeroID( int heroID ) {
        this.heroID = heroID;
    }

    public void setID( int value ) {
        playerId = value;

    }

    public void setName( String value ) {
        name = value;
    }

    public void setRadiant( boolean isRadiant ) {
        this.isRadiant = isRadiant;
    }

    public void setStreak( long time, KillingSpree value ) {
        boolean put = true;
        if (!streak.isEmpty() && getHero() != null) {
            final KillingSpree previousValue = ((NavigableMap<Long, KillingSpree>) streak).floorEntry( time ).getValue();
            if (previousValue != value) {

                if (previousValue.ordinal() > value.ordinal()) {
                    final int previousDeaths = getHero().getDeaths( ((NavigableMap<Long, KillingSpree>) streak).lowerKey( time ) );
                    final int deaths = getHero().getDeaths( time );
                    if (deaths <= previousDeaths) {
                        put = false;
                    }
                }

            }
            else {
                put = false;
            }
        }
        if (put) {
            streak.put( time, value );
        }
    }
}
