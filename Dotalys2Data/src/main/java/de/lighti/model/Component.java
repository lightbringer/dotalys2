package de.lighti.model;

import java.util.HashMap;
import java.util.Map;

import de.lighti.model.game.Hero;

public class Component {
    private final Map<Hero, Map<Hero, Integer>> membersAdjacency;

    public Component() {

        membersAdjacency = new HashMap<Hero, Map<Hero, Integer>>();
//        positions.keySet().forEach( h -> getMembersAdjacency().put( h, new HashMap<Hero, Integer>() ) );
    }

    public int adjacency( Hero hero, Hero hero2 ) {
        if (!getMembersAdjacency().containsKey( hero )) {
            throw new IllegalArgumentException( hero + " not part of this encounter " );
        }
        if (!getMembersAdjacency().get( hero ).containsKey( hero2 )) {
            return 0;
        }
        else {
            return getMembersAdjacency().get( hero ).get( hero2 );
        }

    }

//    public Map<Hero, List<PositionInteger>> getPosTeam( boolean radiant ) {
//        return positions.entrySet().stream().filter( e -> e.getKey().isRadiant() == radiant )
//                        .collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
//    }

    public Map<Hero, Map<Hero, Integer>> getMembersAdjacency() {
        return membersAdjacency;
    }
}