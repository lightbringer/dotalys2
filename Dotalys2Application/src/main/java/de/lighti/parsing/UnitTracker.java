package de.lighti.parsing;

import java.util.HashMap;
import java.util.Map;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Unit;

public class UnitTracker extends AbstractGameEventTracker {
    protected static Map<Integer, Unit> activeUnits;

    protected static void setTeam( Unit u, int team ) {
        switch (team) {
            case 2:
                u.setRadiant( true );
                break;
            case 3:
                u.setRadiant( false );
                break;
            default:
                u.setNeutral( true );
                break;
        }
    }

    protected UnitTracker( Dotalys2 app ) {
        super( app );

        //Since this is meaningless outside of parsing, and listeners may not be added
        //during the parse, we can just clear/created the set everytime a new lister is created
        activeUnits = new HashMap<Integer, Unit>();
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().equals( "CTEDOTAProjectile" )) {
            final Integer s = e.getProperty( "m_hSource" );
            final Integer t = e.getProperty( "m_hTarget" );
            if (s == null || t == null) {
                return;
            }

            final int sourceId = Entity.getIndexForReference( s );
            final int targetId = Entity.getIndexForReference( t );

            final Unit attacker = activeUnits.get( sourceId );
            if (attacker == null) {
                return;
            }

            final Unit target = activeUnits.get( targetId );
            if (target == null) {
                return;
            }

            target.setLastAttacker( attacker );

        }
    }
}
