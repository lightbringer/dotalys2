package de.lighti.components.map.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import de.lighti.components.console.LogEvent;
import de.lighti.components.map.FullMapComponent;
import de.lighti.components.map.data.XYZSeries.XYZDataItem;
import de.lighti.io.DataExporter;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.game.Creep;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Unit;

/**
 * Created by Peetz on 1/7/2015.
 */
public class FullDota2MapModel implements ActionListener, Dota2MapModel {

    private static final int MAX_PLAYBACK_SPEED = 8;

    private static Collection<LogEvent> collectLogEvents( UnitScript playback, int oldtime, int currentTime2 ) {
        return playback.textLog.stream().filter( e -> e.time >= oldtime && e.time <= currentTime2 ).collect( Collectors.toSet() );
    }

    private static void copyTimeMarker( boolean all, int currentTime2, XYZSeries src, XYZSeries target ) {
        target.setNotify( false );
        target.removeAll();

        if (all) {
            target.set( src );
        }
        else {
            final XYZDataItem item = src.findItem( currentTime2 );
            if (item != null) {
                target.addOrReplace( item );
            }

        }
        target.setUseImages( target.getItemCount() == 1 );
        target.setNotify( true );
        target.fireSeriesChanged();

    }

    private final AppState appState;
    private final SortedSet<Object> selectedThings;

    private final Map<Object, UnitScript> dataCache;

    private final FullMapComponent mapComponent;

    private TimerTask animation;

    private int playbackSpeed;

    private int currentTime;

    public FullDota2MapModel( FullMapComponent comp, AppState state ) {
        appState = state;
        mapComponent = comp;
        playbackSpeed = 1;

        //Ensure that heroes are at the end of the set yo they'll get dran last.
        //Makes things look prettier
        selectedThings = new TreeSet<Object>( ( o1, o2 ) -> {
            if (o1 instanceof Hero && !(o2 instanceof Hero)) {
                return 1;
            }
            else if (o2 instanceof Hero && !(o1 instanceof Hero)) {
                return -1;
            }
            else {
                return Integer.compare( o1.hashCode(), o2.hashCode() );
            }
        } );
        dataCache = new HashMap<Object, UnitScript>();

    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        if (e.getSource() == mapComponent.getOptionContainer().getPlayButton()) {
            play();
        }
        else if (e.getSource() == mapComponent.getOptionContainer().getPauseButton()) {
            pause();
        }
        else if (e.getSource() == mapComponent.getOptionContainer().getFfButton()) {
//            new ActionListener() {
//
//                @Override
//                public void actionPerformed( ActionEvent e ) {
//                    stepSlider.setValue( stepSlider.getValue() + 25 );
//
//                }
//            }
        }
        else if (e.getSource() == mapComponent.getOptionContainer().getAllButton()) {
            toggleAllValues();
        }
        else if (e.getSource() == mapComponent.getOptionContainer().getStopButton()) {
            stop();
        }

    }

    public AppState getAppState() {
        return appState;
    }

    public int getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void pause() {
        if (animation != null) {
            animation.cancel();
            mapComponent.getEventLogPane().logEvent( currentTime, null, DataImporter.getName( "PAUSE_LOG_EVENT" ) );
            mapComponent.getOptionContainer().getPlayButton().setEnabled( true );
            mapComponent.getOptionContainer().getStopButton().setEnabled( true );
            mapComponent.getOptionContainer().getPauseButton().setEnabled( false );
            mapComponent.getOptionContainer().getFfButton().setEnabled( true );
            animation = null;
        }

    }

    public void play() {

        if (animation != null) {
            animation.cancel();
            if (playbackSpeed < MAX_PLAYBACK_SPEED) {
                playbackSpeed = playbackSpeed * 2;
            }
            else {
                playbackSpeed = 1;
            }
            final JLabel pbsLabel = mapComponent.getOptionContainer().getPlaybackSpeedLabel();
            pbsLabel.setText( playbackSpeed + " x" );
        }
        if (mapComponent.getOptionContainer().getStepSlider().getValue() == mapComponent.getOptionContainer().getStepSlider().getMaximum()) {
            mapComponent.getOptionContainer().getStepSlider().setValue( 0 );
        }

        animation = new TimerTask() {

            @Override
            public void run() {
                //Pass this over to render thread or we'll risk removing an item whil it's being rendered
                SwingUtilities.invokeLater( () -> {
                    final JSlider slider = mapComponent.getOptionContainer().getStepSlider();

                    slider.setValue( slider.getValue() + appState.getReplay().getMsPerTick() * playbackSpeed );

                    if (slider.getValue() >= slider.getMaximum()) {
                        cancel();
                        animation = null;
                        mapComponent.getOptionContainer().getPauseButton().setEnabled( false );
                    }
                } );
            }
        };
        final Timer timer = new Timer();
        mapComponent.getEventLogPane().logEvent( currentTime, null, DataImporter.getName( "START_LOG_EVENT" ) );
        timer.schedule( animation, 0, appState.getReplay().getMsPerTick() );
        mapComponent.getOptionContainer().getStepSlider().setEnabled( true );
        mapComponent.getOptionContainer().getPlayButton().setEnabled( true );
        mapComponent.getOptionContainer().getPauseButton().setEnabled( true );
        mapComponent.getOptionContainer().getStopButton().setEnabled( true );
        mapComponent.getOptionContainer().getFfButton().setEnabled( true );
        mapComponent.getOptionContainer().getAllButton().setSelected( false );

    }

    public void setActive( Object object, boolean active ) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        if (!active) {
            if (selectedThings.remove( object )) {
                hideThingsFor( object );

                selectedThingsChanged( currentTime );
            }
        }
        else {
            if (selectedThings.add( object )) {
                if (!dataCache.containsKey( object )) {
                    createThingsFor( object );
                }
                displayThingsFor( object );
                selectedThingsChanged( currentTime );
            }
        }
        mapComponent.getOptionContainer().setEnabled( !selectedThings.isEmpty() );
        mapComponent.getOptionContainer().getStepSlider().setMaximum( (int) appState.getReplay().getGameLength() );

    }

    public void setTime( int value ) {
        //Note: do not set the slider here
        final int oldTime = currentTime;
        currentTime = value;
        selectedThingsChanged( oldTime );

        mapComponent.getOptionContainer().getTimeCode().setText( DataExporter.generateTimeCode( value ) );

        mapComponent.getRadiantGoldLabel().setText( Integer.toString( appState.getReplay().getTeamGold( value, true ) ) );
        mapComponent.getDireGoldLabel().setText( Integer.toString( appState.getReplay().getTeamGold( value, false ) ) );

        mapComponent.getRadiantXPLabel().setText( Integer.toString( appState.getReplay().getTeamXP( value, true ) ) );
        mapComponent.getDireXPLabel().setText( Integer.toString( appState.getReplay().getTeamXP( value, false ) ) );

    }

    public void stop() {
        mapComponent.getOptionContainer().getStepSlider().setValue( 0 );
        if (animation != null) {
            animation.cancel();
            animation = null;
            mapComponent.getEventLogPane().logEvent( currentTime, null, DataImporter.getName( "STOP_LOG_EVENT" ) );
        }
        playbackSpeed = 1;
        mapComponent.getOptionContainer().getPlaybackSpeedLabel().setText( playbackSpeed + " x" );
        mapComponent.getOptionContainer().getPlayButton().setEnabled( true );
        mapComponent.getOptionContainer().getPauseButton().setEnabled( false );
        mapComponent.getOptionContainer().getStopButton().setEnabled( false );
        mapComponent.getOptionContainer().getFfButton().setEnabled( true );

    }

    private void createThingsFor( Object o ) {
        if (o instanceof Unit) {
            final Unit u = (Unit) o;
            final UnitScript playback = UnitScript.create( u );

            dataCache.put( o, playback );

        }
        else if (o instanceof Set) {
            final Set<Creep> c = (Set<Creep>) o;
            for (final Unit u : c) {
                createThingsFor( u );
            }
        }
        else {
            throw new IllegalArgumentException( o.getClass().toGenericString() );
        }

    }

    private void displayThingsFor( Object o ) {
        if (o instanceof Unit) {
            final UnitScript playback = dataCache.get( o );
            if (!mapComponent.getMapCanvas().hasSeries( playback.timeslotMap )) {
                mapComponent.getMapCanvas().addSeries( playback.timeslotMap );
            }
            playback.timeslotMap.setVisible( true );
        }
        else if (o instanceof Set) {
            final Set<Creep> c = (Set<Creep>) o;
            for (final Unit u : c) {
                displayThingsFor( u );
            }
        }

    }

    private void hideThingsFor( Object o ) {
        if (o instanceof Unit) {
            final UnitScript playback = dataCache.get( o );
//            mapComponent.getMapCanvas().removeSeries( playback.timeslotMap );
            playback.timeslotMap.setVisible( false );
        }
        else if (o instanceof Set) {
            final Set<Creep> c = (Set<Creep>) o;
            for (final Unit u : c) {
                hideThingsFor( u );
            }
        }
    }

    private void logThingsFor( Object o, int oldTime ) {
        if (o instanceof Unit) {
            final UnitScript playback = dataCache.get( o );
            mapComponent.getEventLogPane().logEvents( collectLogEvents( playback, oldTime, currentTime ) );
        }
        else if (o instanceof Set) {
            final Set<Creep> c = (Set<Creep>) o;
            final Collection<LogEvent> e = new HashSet<LogEvent>();
            for (final Unit u : c) {
                e.addAll( collectLogEvents( dataCache.get( u ), oldTime, currentTime ) );

            }
            mapComponent.getEventLogPane().logEvents( e );
        }

    }

    private void selectedThingsChanged( int oldTime ) {
        mapComponent.getMapCanvas().startUpdate();
        for (final Object o : selectedThings) {
            selectThingsFor( o );
            logThingsFor( o, oldTime );
        }
        mapComponent.getMapCanvas().endUpdate();
    }

    private void selectThingsFor( Object o ) {
        if (o == null) {
            throw new IllegalArgumentException();
        }

        if (o instanceof Unit) {
            final UnitScript playback = dataCache.get( o );
            copyTimeMarker( mapComponent.getOptionContainer().getAllButton().isSelected(), currentTime, playback.completeMap, playback.timeslotMap );
        }
        else if (o instanceof Set) {
            final Set<Creep> c = (Set<Creep>) o;
            for (final Unit u : c) {
                if (!u.getDeaths().isEmpty() && currentTime >= u.getDeaths().get( 0 ).tick) {
                    hideThingsFor( u );
                }
                else {
                    selectThingsFor( u );
                }
            }
        }

    }

    private void toggleAllValues() {
        final JCheckBox allButton = mapComponent.getOptionContainer().getAllButton();
        final boolean allValue = allButton.isSelected();
        mapComponent.getOptionContainer().getStepSlider().setEnabled( !allValue );
        mapComponent.getOptionContainer().getPlayButton().setEnabled( !allValue );
        mapComponent.getOptionContainer().getPlaybackSpeedLabel().setEnabled( !allValue );
        mapComponent.getOptionContainer().getTimeCode().setEnabled( !allValue );
        if (allValue) {
            stop();
            selectedThingsChanged( currentTime );
        }

    }
}