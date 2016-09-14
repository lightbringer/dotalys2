package de.lighti.io;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.TextAnchor;

import de.lighti.components.map.data.XYZSeries;
import de.lighti.model.AppState;
import de.lighti.model.Replay;
import de.lighti.model.game.Ability;
import de.lighti.model.game.CombatEvent;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;
import de.lighti.model.game.Player.KillingSpree;
import de.lighti.model.game.PositionInteger;
import de.lighti.model.game.Unit;
import de.lighti.model.game.Zone;

public final class ChartCreator {
    public static String[][] createAbilityLog( Player p ) {
        final List<Object[]> log = new ArrayList<Object[]>();
        final Hero h = p.getHero();
        for (final Ability a : h.getAbilities()) {
            for (final long l : a.getInvocations()) {
                final Object[] o = new Object[4];
                final PositionInteger pos = h.getPosition( l );
                o[0] = l;
                o[1] = pos.x;
                o[2] = pos.y;
                o[3] = a.getKey();
                log.add( o );
            }
        }
        Collections.sort( log, ( o1, o2 ) -> {
            final long l1 = (long) o1[0];
            final long l2 = (long) o2[0];
            return Long.compare( l1, l2 );
        } );
        final String[][] ret = new String[log.size()][4];
        for (int i = 0; i < log.size(); i++) {
            final Object[] o = log.get( i );
            ret[i][0] = o[0].toString();
            ret[i][1] = o[1].toString();
            ret[i][2] = o[2].toString();
            ret[i][3] = o[3].toString();
        }
        return ret;
    }

    public static XYZSeries createAbilityMap( Hero hero, String name ) {
        final Ability ability = hero.getAbilityByName( name );
        if (ability == null) {
            throw new IllegalArgumentException( "Hero " + hero.getKey() + " has no ability " + name );
        }
        final XYZSeries ret = new XYZSeries( name + DataImporter.getName( "CAT_ABILITIES" ) );

        for (final Long l : ability.getInvocations()) {
            final PositionInteger p = hero.getPosition( l );
            ret.add( p.x, p.y, l );
        }

        return ret;
    }

    /**
     * This method creates a data set with two series(one radiant, one dire) representing the average
     * distance between all members of that team. For each timestep, the symmetrical half of
     * and Euclidian distance matrix is calculated, and the entries for players of the same team are
     * added to the sum. Each sum is then divided by 5.
     * @param appState the current app state containing player data
     * @return a dat set containing two data series
     */
    private static TimeSeriesCollection createAverageTeamDistanceDataSet( AppState appState ) {
        final TimeSeriesCollection series = new TimeSeriesCollection();
        final TimeSeries goodGuys = new TimeSeries( DataImporter.getName( "RADIANT" ) );
        final TimeSeries badGuys = new TimeSeries( DataImporter.getName( "DIRE" ) );

        final List<Hero> radiant = new ArrayList<Hero>();
        final List<Hero> dire = new ArrayList<Hero>();

        for (final Player p : appState.getReplay().getPlayers()) {
            if (p.isRadiant()) {
                radiant.add( p.getHero() );
            }
            else {
                dire.add( p.getHero() );
            }
        }

        for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
            long baddyDistance = 0l;
            long goodDistance = 0l;

            //Radiant
            outerLoop: for (final Hero h : radiant) {
                for (final Hero i : radiant) {
                    if (h == i) {
                        continue outerLoop;
                    }
                    final PositionInteger a = i.getPosition( seconds );
                    final PositionInteger b = h.getPosition( seconds );
                    final int xDiff = a.x - b.x;
                    final int yDiff = a.y - b.y;
                    goodDistance += Math.sqrt( Math.pow( xDiff, 2 ) + Math.pow( yDiff, 2 ) );
                }
            }

            //Dire
            outerLoop: for (final Hero h : dire) {
                for (final Hero i : dire) {
                    if (h == i) {
                        continue outerLoop;
                    }
                    final PositionInteger a = i.getPosition( seconds );
                    final PositionInteger b = h.getPosition( seconds );
                    final int xDiff = a.x - b.x;
                    final int yDiff = a.y - b.y;
                    baddyDistance += Math.sqrt( Math.pow( xDiff, 2 ) + Math.pow( yDiff, 2 ) );
                }
            }

            //Average
            goodDistance /= 5l;
            baddyDistance /= 5l;

            goodGuys.add( new FixedMillisecond( seconds ), goodDistance );
            badGuys.add( new FixedMillisecond( seconds ), baddyDistance );

        }
        series.addSeries( badGuys );
        series.addSeries( goodGuys );
        return series;
    }

    public static JFreeChart createAverageTeamDistanceGraph( AppState state ) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart( DataImporter.getName( "AVERAGE_TEAM_DISTANCE" ), // chart title
                        DataImporter.getName( "MILISECONDS" ), // x axis label
                        "", // y axis label
                        createAverageTeamDistanceDataSet( state ), // data
                        true, // include legend
                        true, // tooltips
                        false // urls
                        );
        final XYPlot plot = chart.getXYPlot();

        final DateAxis domainAxis = new DateAxis( DataImporter.getName( "TIME" ) );
        domainAxis.setTickMarkPosition( DateTickMarkPosition.MIDDLE );
        domainAxis.setLowerMargin( 0.0 );
        domainAxis.setUpperMargin( 0.0 );
        plot.setDomainAxis( domainAxis );
        plot.setForegroundAlpha( 0.5f );
        plot.setDomainPannable( false );
        plot.setRangePannable( false );

        final NumberAxis rangeAxis = new NumberAxis( DataImporter.getName( "AVERAGE_TEAM_DISTANCE" ) );
        rangeAxis.setLowerMargin( 0.15 );
        rangeAxis.setUpperMargin( 0.15 );
        plot.setRangeAxis( rangeAxis );

        return chart;
    }

    public static XYZSeries createDeathMap( Unit u ) {

        final List<CombatEvent> coords = u.getDeaths();

        final XYZSeries ret = new XYZSeries( u.getKey() + "_" + DataImporter.getName( "DEATHS" ) );
        for (final CombatEvent e : coords) {
            ret.add( e.x, e.y, e.tick );
        }
        return ret;
    }

//    public static XYZSeries createEncounterMap( Encounter enc ) {
//
//        final XYZSeries ret = new XYZSeries( enc.toString() );
//        for (final Map.Entry<Long, double[]> e : enc.getCentroids().entrySet()) {
//            ret.add( e.getValue()[0], e.getValue()[1], e.getKey() );
//        }
//        return ret;
//    }

    public static String[][] createItemLog( Player p ) {
        final List<Object[]> log = new ArrayList<Object[]>();
        final Hero h = p.getHero();
        for (final Dota2Item a : h.getAllItems()) {
            for (final long l : a.getUsage()) {
                final PositionInteger pos = h.getPosition( l );
                final Object[] o = new Object[4];
                o[0] = l;
                o[1] = pos.x;
                o[2] = pos.y;
                o[3] = a.getKey();
                log.add( o );
            }
        }
        Collections.sort( log, ( o1, o2 ) -> {
            final long l1 = (long) o1[0];
            final long l2 = (long) o2[0];
            return Long.compare( l1, l2 );
        } );
        final String[][] ret = new String[log.size()][4];
        for (int i = 0; i < log.size(); i++) {
            final Object[] o = log.get( i );
            ret[i][0] = o[0].toString();
            ret[i][1] = o[1].toString();
            ret[i][2] = o[2].toString();
            ret[i][3] = o[3].toString();
        }
        return ret;
    }

    public static XYZSeries createItemMap( Hero hero, String itemKey ) {
        final Set<Dota2Item> items = hero.getItemsByName( itemKey );
        final XYZSeries ret = new XYZSeries( hero.getKey() + itemKey + DataImporter.getName( "CAT_ABILITIES" ) );

        for (final Dota2Item i : items) {
            for (final Long l : i.getUsage()) {
                final PositionInteger p = hero.getPosition( l );
                ret.add( p.x, p.y, l );
            }
        }

        return ret;
    }

    public static String[][] createMoveLog( String string, Replay r, boolean useCells ) {
        final Player p = r.getPlayerByName( string );

        final Unit hero = p.getHero();
        String[][] ret;
        if (useCells) {
            final Map<Long, PositionInteger> xy = hero.getXY();

            ret = new String[xy.size()][];
            int i = 0;
            for (final Entry<Long, PositionInteger> e : xy.entrySet()) {
                ret[i] = new String[] { e.getKey().toString(), Integer.toString( e.getValue().x ), Integer.toString( e.getValue().y ) };
                i++;
            }
        }
        else {
            final Map<Long, PositionInteger> xy = hero.getOrigins();
            ret = new String[xy.size()][];
            int i = 0;
            for (final Long l : xy.keySet()) {
                final int[] pos = hero.getAbsolutePosition( l );
                ret[i] = new String[] { Long.toString( l ), Integer.toString( pos[0] ), Integer.toString( pos[1] ) };
                i++;
            }
        }

        return ret;
    }

    public static XYZSeries createMoveMap( Unit u ) {

        final NavigableMap<Long, PositionInteger> xy = u.getXY();
        final XYZSeries ret = new XYZSeries( u.getKey() + "_" + DataImporter.getName( "MOVEMENT" ) );
        ret.setNotify( false );

        for (final Entry<Long, PositionInteger> e : xy.entrySet()) {
            ret.add( e.getValue().x, e.getValue().y, e.getKey() );
        }
        ret.setNotify( true );

        return ret;
    }

    private static TimeSeriesCollection createPlayerDataSet( String attribute, List<String> players, AppState appState ) {
        final TimeSeriesCollection series = new TimeSeriesCollection();

        try {
            for (final String player : players) {
                final Player p = appState.getReplay().getPlayerByName( player );
                final TimeSeries series1 = new TimeSeries( player );

                if (attribute.equals( DataImporter.getName( "EXPERIENCE" ) )) {
                    for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
                        series1.add( new FixedMillisecond( seconds ), p.getXP( seconds ) );
                    }
                }
                else if (attribute.equals( DataImporter.getName( "GOLD" ) )) {
                    for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
                        series1.add( new FixedMillisecond( seconds ), p.getEarnedGold( seconds ) );
                    }
                }
                else if (attribute.equals( DataImporter.getName( "STREAK" ) )) {
                    for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
                        series1.add( new FixedMillisecond( seconds ), p.getStreaks( seconds ).ordinal() );
                    }
                }
                else if (attribute.equals( DataImporter.getName( "DEATHS" ) )) {
                    for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
                        series1.add( new FixedMillisecond( seconds ), p.getHero().getDeaths( seconds ) );
                    }
                }
                else {
                    for (final Entry<Long, Map<String, Object>> e : appState.getReplay().gameEventsPerMs.entrySet()) {
                        if (e.getValue().containsKey( attribute + "." + ID_TO_GAMEEVENT_FORMAT.format( p.getId() ) )) {
                            final Number v = (Number) e.getValue().get( attribute + "." + ID_TO_GAMEEVENT_FORMAT.format( p.getId() ) );
                            series1.add( new FixedMillisecond( e.getKey() ), v );
                        }
                    }
                }

                series.addSeries( series1 );

            }
        }
        catch (final ClassCastException e) {
            LOGGER.warning( "Selected attribute contained alpha-numeric data" );
        }
        return series;
    }

    public static JFreeChart createPlayerHistogram( String selectedItem, List<String> selectedValuesList, AppState state ) {
        final JFreeChart chart = ChartFactory.createXYLineChart( selectedItem, // chart title
                        DataImporter.getName( "MILISECONDS" ), // x axis label
                        "", // y axis label
                        createPlayerDataSet( selectedItem, selectedValuesList, state ), // data
                        PlotOrientation.VERTICAL, true, // include legend
                        true, // tooltips
                        false // urls
                        );

        final XYPlot plot = chart.getXYPlot();

        final DateAxis domainAxis = new DateAxis( DataImporter.getName( "TIME" ) );
        domainAxis.setTickMarkPosition( DateTickMarkPosition.MIDDLE );
        domainAxis.setLowerMargin( 0.0 );
        domainAxis.setUpperMargin( 0.0 );
        plot.setDomainAxis( domainAxis );
        plot.setForegroundAlpha( 0.5f );
        plot.setDomainPannable( false );
        plot.setRangePannable( false );

        final NumberAxis rangeAxis = new NumberAxis( selectedItem );
        rangeAxis.setLowerMargin( 0.15 );
        rangeAxis.setUpperMargin( 0.15 );
        plot.setRangeAxis( rangeAxis );

        if (selectedItem.equals( DataImporter.getName( "STREAK" ) )) {
            for (final KillingSpree k : KillingSpree.values()) {
                final ValueMarker marker = new ValueMarker( k.ordinal() );
                marker.setLabel( DataImporter.getName( k.name() ) );
                marker.setLabelTextAnchor( TextAnchor.BOTTOM_LEFT );
                plot.addRangeMarker( marker );
            }
        }
        else {
            plot.clearRangeMarkers();
        }
        return chart;
    }

    private static TimeSeriesCollection createTeamGoldDiffDataSet( AppState appState ) {

        final TimeSeriesCollection series = new TimeSeriesCollection();

        final TimeSeries goodGuys = new TimeSeries( DataImporter.getName( "RADIANT" ) );
        final TimeSeries badGuys = new TimeSeries( DataImporter.getName( "DIRE" ) );

        final List<Player> radiant = new ArrayList<Player>();
        final List<Player> dire = new ArrayList<Player>();

        for (final Player p : appState.getReplay().getPlayers()) {
            if (p.isRadiant()) {
                radiant.add( p );
            }
            else {
                dire.add( p );
            }
        }

        for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
            long baddyGold = 0l;
            long goodGold = 0l;

            //Radiant
            for (final Player p : radiant) {
                goodGold += p.getEarnedGold( seconds );
            }

            //Dire
            for (final Player p : dire) {
                baddyGold += p.getEarnedGold( seconds );
            }

            goodGuys.add( new FixedMillisecond( seconds ), goodGold );
            badGuys.add( new FixedMillisecond( seconds ), baddyGold );

        }
        series.addSeries( badGuys );
        series.addSeries( goodGuys );
        return series;
    }

    public static JFreeChart createTeamGoldDifferenceGraph( AppState appState ) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart( DataImporter.getName( "TEAM_XP" ), DataImporter.getName( "EXPERIENCE" ), "Time",
                        createTeamGoldDiffDataSet( appState ), true, // legend
                        true, // tool tips
                        false // URLs
                        );

        final XYDifferenceRenderer renderer = new XYDifferenceRenderer( Color.GREEN, Color.RED, false );

        renderer.setSeriesPaint( 0, Color.GREEN );
        renderer.setSeriesPaint( 1, Color.RED );
        final XYPlot plot = chart.getXYPlot();
        plot.setRenderer( renderer );

        final DateAxis domainAxis = new DateAxis( DataImporter.getName( "TIME" ) );
        domainAxis.setTickMarkPosition( DateTickMarkPosition.MIDDLE );
        domainAxis.setLowerMargin( 0.0 );
        domainAxis.setUpperMargin( 0.0 );
        plot.setDomainAxis( domainAxis );
        plot.setForegroundAlpha( 0.5f );

        final NumberAxis rangeAxis = new NumberAxis( DataImporter.getName( "GOLD" ) );
        rangeAxis.setLowerMargin( 0.15 );
        rangeAxis.setUpperMargin( 0.15 );
        plot.setRangeAxis( rangeAxis );
        return chart;
    }

    private static TimeSeriesCollection createTeamXPDiffDataSet( AppState appState ) {

        final TimeSeriesCollection series = new TimeSeriesCollection();

        final TimeSeries goodGuys = new TimeSeries( DataImporter.getName( "RADIANT" ) );
        final TimeSeries badGuys = new TimeSeries( DataImporter.getName( "DIRE" ) );

        final List<Player> radiant = new ArrayList<Player>();
        final List<Player> dire = new ArrayList<Player>();

        for (final Player p : appState.getReplay().getPlayers()) {
            if (p.isRadiant()) {
                radiant.add( p );
            }
            else {
                dire.add( p );
            }
        }

        for (long seconds = 0l; seconds < appState.getReplay().getGameLength(); seconds += appState.getReplay().getMsPerTick() * 1000) {
            long baddyXP = 0l;
            long goodXP = 0l;

            //Radiant
            for (final Player p : radiant) {
                goodXP += p.getXP( seconds );
            }

            //Dire
            for (final Player p : dire) {
                baddyXP += p.getXP( seconds );
            }

            goodGuys.add( new FixedMillisecond( seconds ), goodXP );
            badGuys.add( new FixedMillisecond( seconds ), baddyXP );

        }
        series.addSeries( badGuys );
        series.addSeries( goodGuys );
        return series;
    }

    public static JFreeChart createTeamXpDifferenceGraph( AppState state ) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart( DataImporter.getName( "TEAM_XP" ), DataImporter.getName( "EXPERIENCE" ), "Time",
                        createTeamXPDiffDataSet( state ), true, // legend
                        true, // tool tips
                        false // URLs
                        );

        final XYDifferenceRenderer renderer = new XYDifferenceRenderer( Color.GREEN, Color.RED, false );

        renderer.setSeriesPaint( 0, Color.GREEN );
        renderer.setSeriesPaint( 1, Color.RED );
        final XYPlot plot = chart.getXYPlot();
        plot.setRenderer( renderer );

        final DateAxis domainAxis = new DateAxis( DataImporter.getName( "TIME" ) );
        domainAxis.setTickMarkPosition( DateTickMarkPosition.MIDDLE );
        domainAxis.setLowerMargin( 0.0 );
        domainAxis.setUpperMargin( 0.0 );
        plot.setDomainAxis( domainAxis );
        plot.setForegroundAlpha( 0.5f );

        final NumberAxis rangeAxis = new NumberAxis( DataImporter.getName( "EXPERIENCE" ) );
        rangeAxis.setLowerMargin( 0.15 );
        rangeAxis.setUpperMargin( 0.15 );
        plot.setRangeAxis( rangeAxis );
        return chart;

    }

    public static String[][] createZoneLog( String name, Replay r ) {
        final Player p = r.getPlayerByName( name );

        final Unit hero = p.getHero();
        final Map<Long, Zone> zones = hero.getZones();

        final String[][] ret = new String[zones.size()][];
        int i = 0;
        for (final Entry<Long, Zone> e : zones.entrySet()) {
            ret[i] = new String[] { e.getKey().toString(), e.getValue().name() };
            i++;
        }

        return ret;
    }

    private final static Logger LOGGER = Logger.getLogger( ChartCreator.class.getName() );

    /**
     * TODO
     * We store player id as a real int, but unhandled game events are stored as name.XXXX.
     * We temporaily solve this by expanding the real id to four digits.
     */
    private final static DecimalFormat ID_TO_GAMEEVENT_FORMAT = new DecimalFormat( "0000" );

    /**
     * Default constructor to prevent instantiation.
     */
    private ChartCreator() {

    }
}
