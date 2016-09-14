package de.lighti.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataExporter {
    public static class LogItem {
        public long time;
        public String text;

        public LogItem( long time, String text ) {
            super();
            this.time = time;
            this.text = text;
        }

    }

//    public static List<LogItem> convertEncounter( Replay r, Encounter e ) {
//        final List<LogItem> log = new ArrayList<LogItem>();
//
//        final long start = e.getFirstTimestamp();
//        final long end = e.getLastTimestamp();
//
//        //Enter/Exit
//        final Set<Hero> heroes = new HashSet<Hero>();
//        heroes.addAll( e.getRadiant() );
//        heroes.addAll( e.getDire() );
//
//        //Set to US to have a unified number output
//        final Locale currentLocale = Locale.getDefault();
//        Locale.setDefault( Locale.US );
//
//        for (final Hero h : heroes) {
//            final long enter = e.getEnterTime( h );
//            final long exit = e.getExitTime( h );
//            log.add( new LogItem( enter, "ENTER_" + (e.getDire().contains( h ) ? "DIRE" : "RADIANT") + " " + h.getKey() ) );
//            log.add( new LogItem( exit, "EXIT_" + h.getKey() ) );
//
//            h.getDeaths()
//
//                            .stream().filter( entry -> entry.tick >= start && entry.tick <= end ).forEach( entry -> {
//                                log.add( new LogItem( entry.tick, "DEATH " + h.getKey() + (entry.source != null ? " BY " + entry.source.getKey() : "") ) );
//                            } );
//
//            h.getAbilities().forEach( a -> {
//                a.getInvocations().stream().filter( i -> i >= start && i <= end ).forEach( l -> {
//                    log.add( new LogItem( l, "ABILITY " + h.getKey() + " " + a.getKey() ) );
//                } );
//            } );
//
//            h.getStuns().entrySet().stream().filter( s -> s.getKey() >= start && s.getKey() <= end ).forEach( s -> {
//                log.add( new LogItem( s.getKey(), "STUNNED " + h.getKey() + (s.getValue() != null ? " BY " + s.getValue() : "") ) );
//            } );
//
//            //Player data
//            final Player p = r.getPlayerByHero( h );
//            p.getStreaks().entrySet().stream().filter( entry -> entry.getKey() >= start && entry.getKey() <= end && entry.getValue() != KillingSpree.NONE )
//                            .forEach( entry -> {
//                                log.add( new LogItem( entry.getKey(), "STREAK " + h.getKey() + " " + entry.getValue().name() ) );
//                            } );
//        }
//        final Set<Tower> towersInRange = new HashSet<Tower>();
//
//        //Positions & Tower
//        for (final Entry<Long, PositionDouble> en : e.getCentroids().entrySet()) {
//            for (final Tower t : r.getTowers()) {
//                if (t.distance( en.getValue().x, en.getValue().y, en.getKey() ) < Encounter.DISTANCE_THRESHOLD && !towersInRange.contains( t )) {
//                    log.add( new LogItem( en.getKey(), "TOWER_IN_RANGE " + t.getKey() ) );
//                    towersInRange.add( t );
//                }
//                else if (towersInRange.contains( t )) {
//                    log.add( new LogItem( en.getKey(), "TOWER_OUT_OF_RANGE " + t.getKey() ) );
//                    towersInRange.remove( t );
//                }
//            }
//            log.add( new LogItem( en.getKey(), "POSITION " + String.format( "%.2f", en.getValue().x ) + " " + String.format( "%.2f", en.getValue().y ) ) );
//        }
//
//        //DAMAGE
//        for (final CombatEvent d : e.getDamagesAndHeals()) {
//            String text;
//            if (d.value > 0) {
//                text = "DAMAGE ";
//            }
//            else {
//                text = "HEAL ";
//            }
//            text += d.source.getKey() + " TO " + d.target.getKey() + " " + (d.inflictor != null ? " WITH " + d.inflictor : "");
//            text += " VALUE " + d.value;
//            log.add( new LogItem( d.tick, text ) );
//        }
//
//        //Add summary
////        log.add( new LogItem( e.getLastTimestamp(), "ROLES_DIRE "
////                        + Arrays.asList( HeroRole.asString( e.getDireRoles() ) ).stream().collect( Collectors.joining( " " ) ) ) );
////        log.add( new LogItem( e.getLastTimestamp(), "ROLES_RADIANT "
////                        + Arrays.asList( HeroRole.asString( e.getRadiantRoles() ) ).stream().collect( Collectors.joining( " " ) ) ) );
//        for (int i = 0; i < e.getDireRoles().length; i++) {
//            log.add( new LogItem( e.getLastTimestamp(), "Dire_" + HeroRole.values()[i].id + e.getDireRoles()[i] ) );
//        }
//        for (int i = 0; i < e.getDireRoles().length; i++) {
//            log.add( new LogItem( e.getLastTimestamp(), "Radiant_" + HeroRole.values()[i].id + e.getRadiantRoles()[i] ) );
//        }
//        log.add( new LogItem( e.getLastTimestamp(), "KILLS_DIRE " + e.getKillsDire() ) );
//        log.add( new LogItem( e.getLastTimestamp(), "KILLS_RADIANT " + e.getKillsRadiant() ) );
//        log.add( new LogItem( e.getLastTimestamp(), "AVG_RADIANT_CENTROID_DISTANCE " + String.format( "%.2f", e.getAverageRadiantDistanceToCentroid() ) ) );
//        log.add( new LogItem( e.getLastTimestamp(), "AVG_DIRE_CENTROID_DISTANCE " + String.format( "%.2f", e.getAverageDireDistanceToCentroid() ) ) );
//        log.add( new LogItem( e.getLastTimestamp(), "AVG_RADIANT_TEAM_DISTANCE " + String.format( "%.2f", e.getAverageRadiantIntraTeamDistance() ) ) );
//        log.add( new LogItem( e.getLastTimestamp(), "AVG_DIRE_TEAM_DISTANCE " + String.format( "%.2f", e.getAverageDireIntraTeamDistance() ) ) );
//        log.add( new LogItem( e.getLastTimestamp(), "AVG_RADIANT_ENEMY_DISTANCE " + String.format( "%.2f", e.getAverageRadiantEnemyDistance() ) ) );
//        log.add( new LogItem( e.getLastTimestamp(), "AVG_DIRE_ENEMY_DISTANCE " + String.format( "%.2f", e.getAverageDireEnemyDistance() ) ) );
//        log.add( new LogItem( e.getLastTimestamp(), "ABILITIES_USED_DIRE " + e.getAbilitiesUsed( false ).size() ) );
//        log.add( new LogItem( e.getLastTimestamp(), "ABILITIES_USED_RADIANT " + e.getAbilitiesUsed( true ).size() ) );
//
//        //Leaves and enter summary
//        if (e.getLeavesEarly( true ) > 0) {
//            log.add( new LogItem( e.getLastTimestamp(), "LEAVE_RADIANT_EARLY " + e.getLeavesEarly( true ) ) );
//
//        }
//        if (e.getLeavesEarly( false ) > 0) {
//            log.add( new LogItem( e.getLastTimestamp(), "LEAVE_DIRE_EARLY " + e.getLeavesEarly( false ) ) );
//
//        }
//        if (e.getLeavesMid( true ) > 0) {
//            log.add( new LogItem( e.getLastTimestamp(), "LEAVE_RADIANT_MID " + e.getLeavesMid( true ) ) );
//
//        }
//        if (e.getLeavesMid( false ) > 0) {
//            log.add( new LogItem( e.getLastTimestamp(), "LEAVE_DIRE_MID " + e.getLeavesMid( false ) ) );
//
//        }
//        if (e.getLeavesLate( true ) > 0) {
//            log.add( new LogItem( e.getLastTimestamp(), "LEAVE_RADIANT_LATE " + e.getLeavesLate( true ) ) );
//
//        }
//        if (e.getLeavesLate( false ) > 0) {
//            log.add( new LogItem( e.getLastTimestamp(), "LEAVE_DIRE_LATE " + e.getLeavesLate( false ) ) );
//
//        }
//
//        final Integer[][] d = e.getDamage();
//        int row = 0;
//        for (final Hero h : e.getHeroes()) {
//            log.add( new LogItem( e.getLastTimestamp(), "DAMAGE_SUMMARY " + h.getKey() + " "
//                            + Arrays.asList( d[row] ).stream().map( i -> i.toString() ).collect( Collectors.joining( " " ) ) ) );
//            row++;
//        }
//
//        Collections.sort( log, ( l1, l2 ) -> {
//            return Long.compare( l1.time, l2.time );
//        } );
//        //Restore the default locale
//        Locale.setDefault( currentLocale );
//        return log;
//    }

    public static void exportCSV( File file, String header, String[][] data ) throws IOException {
        final BufferedWriter fo = new BufferedWriter( new FileWriter( file ) );
        fo.write( header );
        fo.newLine();
        for (final String[] line : data) {
            for (int i = 0; i < line.length; i++) {
                fo.write( line[i] );
                if (i < line.length - 1) {
                    fo.write( ", " );
                }
            }
            fo.newLine();

        }
        fo.close();

    }

//    public static void exportEncounter( File file, Replay r, Encounter e ) throws IOException {
//        final List<LogItem> log = convertEncounter( r, e );
//
//        final String[][] data = new String[log.size()][2];
//        for (int i = 0; i < log.size(); i++) {
//            final LogItem l = log.get( i );
//            data[i][0] = Long.toString( l.time );
//            data[i][1] = l.text;
//        }
//
//        exportCSV( file, "#time, event", data );
//
//    }

    public static String generateTimeCode( long ms ) {
        return DataExporter.generateTimeCode( ms, true );
    }

    public static String generateTimeCode( long ms, boolean spaces ) {

        long remainder;
        final int hours = (int) (ms / (60 * 60 * 1000));
        remainder = ms % (60 * 60 * 1000);
        final int minutes = (int) (remainder / (60 * 1000));
        remainder = remainder % (60 * 1000);
        final int seconds = (int) (remainder / 1000);
        remainder = remainder % 1000 / 10;
        if (spaces) {
            return String.format( "%02d : %02d : %02d : %02d", hours, minutes, seconds, remainder );
        }
        else {
            return String.format( "%02d:%02d:%02d:%02d", hours, minutes, seconds, remainder );
        }
    }
}
