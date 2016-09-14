package de.lighti.components.map.data;

import java.awt.Color;
import java.util.Map;
import java.util.TreeSet;

import de.lighti.components.console.LogEvent;
import de.lighti.components.map.data.XYZSeries.XYZDataItem;
import de.lighti.components.map.resources.MinimapIcons;
import de.lighti.components.map.resources.MinimapIcons.ICON_FLAVOUR;
import de.lighti.io.ChartCreator;
import de.lighti.io.DataImporter;
import de.lighti.model.game.Ability;
import de.lighti.model.game.CombatEvent;
import de.lighti.model.game.Hero;
import de.lighti.model.game.PositionInteger;
import de.lighti.model.game.Tower;
import de.lighti.model.game.Unit;

class UnitScript {
    static UnitScript create( Unit u ) {
        final UnitScript playback = new UnitScript();
        playback.textLog = new TreeSet<LogEvent>();
        playback.completeMap = ChartCreator.createMoveMap( u );

        //Deaths
        for (final CombatEvent e : u.getDeaths()) {

            final XYZDataItem item = playback.completeMap.add( e.x, e.y, e.tick );
            String deathString;
            if (e.source != null) {
                final String killerName = DataImporter.getName( e.source.getKey() );
                deathString = DataImporter.getName( "DIES_FROM" ) + " " + killerName;
            }
            else {
                deathString = DataImporter.getName( "DIES" );
            }
            playback.textLog.add( new LogEvent( u, deathString, item.z ) );
            if (u instanceof Tower) {
                item.image = MinimapIcons.getMinimapIcon( u.getKey(), u.isRadiant(), ICON_FLAVOUR.DEAD );
                final Tower t = (Tower) u;
                playback.completeMap.getItemsUnsafe().values().stream().forEach( di -> {
                    //Towers are still a bit glitchy. Need to track this down
                                final PositionInteger p = t.getPosition( 0 );
                                di.x = p.x;
                                di.y = p.y;
                            } );
                //AND STAY DEAD!
                playback.completeMap.getItemsUnsafe().values().stream().filter( di -> di.z >= item.z ).forEach( di -> {
                    di.image = MinimapIcons.getMinimapIcon( u.getKey(), u.isRadiant(), ICON_FLAVOUR.DEAD );
                } );
            }
            if (u instanceof Hero) {
                item.image = MinimapIcons.getMinimapIcon( u.getKey(), u.isRadiant(), ICON_FLAVOUR.DEAD );
                final Long respawn = u.getNextRespawn( item.z );
                if (respawn != null) {
                    playback.completeMap.getItemsUnsafe().values().stream().filter( di -> di.z >= item.z && di.z <= respawn ).forEach( di -> {
                        di.image = MinimapIcons.getMinimapIcon( u.getKey(), u.isRadiant(), ICON_FLAVOUR.DEAD );
                    } );
                }

            }//else doesn't respawn, probably because the game ended or it's a creep

        }
        //Respawns
        for (final Long l : u.getRespawns()) {
            playback.textLog.add( new LogEvent( u, DataImporter.getName( "RESPAWNS" ), l ) );
        }

        //Abilities
        if (u instanceof Hero) {
            final Hero h = (Hero) u;
            for (final Ability a : h.getAbilities()) {
                if (!a.getKey().equals( "ATTRIBUTE_BONUS" )) {
                    for (final long l : a.getInvocations()) {
                        final LogEvent le = new LogEvent( u, DataImporter.getName( "USES" ) + " " + DataImporter.getName( a.getKey() ), l );
                        le.object = a;
                        playback.textLog.add( le );
                    }
                }
            }
        }

        //Stuns
        if (u instanceof Hero) {
            for (final Map.Entry<Long, Integer> stun : u.getStuns().entrySet()) {
                playback.textLog.add( new LogEvent( u, String.format( DataImporter.getName( "STUNNED_FOR_SECONDS" ), stun.getValue() / 1000f ), stun.getKey() ) );

                XYZDataItem start = playback.completeMap.getByZ( stun.getKey() );

                if (start == null) {
                    start = playback.completeMap.new XYZDataItem();
                    start.z = stun.getKey();

                    final PositionInteger pos = u.getPosition( start.z );
                    start.x = pos.x;
                    start.y = pos.y;
                    start.z = stun.getKey();
                    playback.completeMap.getItemsUnsafe().put( start.z, start );
                }

                final long starttime = start.z;
                final long endtime = stun.getKey() + stun.getValue();

                XYZDataItem end = playback.completeMap.getByZ( endtime );
                if (end == null) {
                    final PositionInteger p = u.getPosition( endtime );
                    end = playback.completeMap.new XYZDataItem();
                    end.x = p.x;
                    end.y = p.y;
                    end.z = endtime;
                    playback.completeMap.getItemsUnsafe().put( endtime, end );
                }

                //Stun times are a bit glitchy and don't really sync with the positions (ore the hero has been moved while stunned)
//                if (start.x != end.x || start.y != end.y) {
//                    System.out.println( stun.getValue() );
//                    playback.completeMap.getItemsUnsafe().entrySet().stream().filter( e -> e.getKey() >= starttime && e.getKey() <= endtime ).forEach( e -> {
//                        System.out.println( e.getValue().x + " " + e.getValue().y + " " + e.getValue().z );
//                    } );
//                }

                playback.completeMap.getItemsUnsafe().entrySet().stream().filter( e -> e.getKey() >= starttime && e.getKey() <= endtime ).forEach( e -> {
                    e.getValue().image = MinimapIcons.getMinimapIcon( u.getKey(), u.isRadiant(), ICON_FLAVOUR.STUNNED );
                } );

            }
        }
        playback.timeslotMap = new XYZSeries( u.getKey() );
        if (u instanceof Hero || u instanceof Tower) {
            playback.timeslotMap.setDefaultImage( MinimapIcons.getMinimapIcon( u.getKey(), u.isRadiant(), ICON_FLAVOUR.NORMAL ) );
        }
        else {
            playback.timeslotMap.setSeriesColor( u.isRadiant() ? Color.GREEN : Color.RED );
            playback.timeslotMap.setUseImages( false );
        }
        return playback;
    }

    TreeSet<LogEvent> textLog;
    XYZSeries completeMap;
    XYZSeries timeslotMap;
}