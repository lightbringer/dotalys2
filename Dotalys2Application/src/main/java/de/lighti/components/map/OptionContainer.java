package de.lighti.components.map;

import java.awt.Font;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import de.lighti.io.DataImporter;

public class OptionContainer extends JComponent {
    private final static String ICON_FAST_FORWARD = "resources/ffButton.png";
    private static final String ICON_PAUSE = "resources/pauseButton.png";
    private static final String ICON_PLAY = "resources/playButton.png";
    private static final String ICON_STOP = "resources/stopButton.png";

    private final MapComponent mapComponent;

    private JCheckBox allButton;
    private JSlider stepSlider;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton ffButton;
    private JButton fbButton;

    private JLabel playbackSpeedLabel;

    private JLabel timeCode;

    private final String currentTimeCode;

    public OptionContainer( MapComponent mapComponent ) {
        super();
        this.mapComponent = mapComponent;

        currentTimeCode = "-- : -- : -- : --";

        final JButton toggleButton = new JButton( DataImporter.getName( "MAP_ZONES" ) );
        toggleButton.addActionListener( e -> {
            mapComponent.getMapCanvas().setPaintMapModel( !mapComponent.getMapCanvas().isPaintMapModel() );
            mapComponent.getMapCanvas().repaint();
        } );

        final JTextArea playbackLog = new JTextArea();
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView( playbackLog );

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout( this );
        setLayout( layout );
        layout.setHorizontalGroup( layout.createParallelGroup( Alignment.LEADING ).addGroup( layout.createSequentialGroup().addComponent( getAllButton() )
                        .addPreferredGap( ComponentPlacement.RELATED ).addComponent( toggleButton ).addPreferredGap( ComponentPlacement.RELATED )
                        .addGroup( layout.createParallelGroup( Alignment.LEADING, false )
                                        .addComponent( getTimeCode(), GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                        .addComponent( getStepSlider(), GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                        .addPreferredGap( ComponentPlacement.RELATED ).addComponent( getPlaybackSpeedLabel() ).addPreferredGap( ComponentPlacement.RELATED )
                        .addComponent( getPlayButton() ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( getPauseButton() )
                        .addPreferredGap( ComponentPlacement.RELATED ).addComponent( getStopButton() ) ) );
        layout.setVerticalGroup( layout.createParallelGroup( Alignment.LEADING ).addGroup( layout.createSequentialGroup().addGroup( layout
                        .createParallelGroup( Alignment.LEADING, false )
                        .addGroup( layout.createParallelGroup( Alignment.BASELINE )
                                        .addComponent( getPlaybackSpeedLabel(), javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE )
                                        .addComponent( getPlayButton() ).addComponent( getPauseButton() ).addComponent( getStopButton() )
                                        .addComponent( getStopButton() ).addComponent( getStopButton() ) )
                        .addGroup( Alignment.LEADING,
                                        layout.createParallelGroup( Alignment.BASELINE ).addComponent( getAllButton() ).addComponent( toggleButton ) )
                        .addComponent( getStepSlider(), GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                        .addPreferredGap( ComponentPlacement.RELATED ).addComponent( getTimeCode() ) ) );

        // add( getFfButton() );
    }

    public JCheckBox getAllButton() {
        if (allButton == null) {
            allButton = new JCheckBox( DataImporter.getName( "ALL" ) );
            allButton.setEnabled( false );
            allButton.setSelected( true );
            allButton.addActionListener( mapComponent.getPlaybackScript() );
        }
        return allButton;
    }

    public JButton getFfButton() {
        if (ffButton == null) {
            try {
                final ImageIcon icon = new ImageIcon( ImageIO.read( getClass().getResource( ICON_FAST_FORWARD ) ) );
                ffButton = new JButton( icon );
                // to remote the spacing between the image and button's borders
                ffButton.setMargin( new Insets( 0, 0, 0, 0 ) );

                // to remove the border
                ffButton.setBorder( null );
            }
            catch (final IOException e1) {
                ffButton = new JButton( DataImporter.getName( "FASTFORWARD" ) );
            }
            ffButton.setEnabled( false );
            ffButton.addActionListener( mapComponent.getPlaybackScript() );
        }
        return ffButton;
    }

    public JButton getPauseButton() {
        if (pauseButton == null) {
            try {
                final ImageIcon icon = new ImageIcon( ImageIO.read( getClass().getResource( ICON_PAUSE ) ) );
                pauseButton = new JButton( icon );
                // to remote the spacing between the image and button's borders
                pauseButton.setMargin( new Insets( 0, 0, 0, 0 ) );

                // to remove the border
                pauseButton.setBorder( null );
            }
            catch (final IOException e1) {
                pauseButton = new JButton( DataImporter.getName( "PAUSE" ) );
            }
            pauseButton.setEnabled( false );
            pauseButton.addActionListener( mapComponent.getPlaybackScript() );
        }
        return pauseButton;
    }

    public JLabel getPlaybackSpeedLabel() {
        if (playbackSpeedLabel == null) {
            playbackSpeedLabel = new JLabel( mapComponent.getPlaybackScript().getPlaybackSpeed() + " x", SwingConstants.CENTER );
            playbackSpeedLabel.setEnabled( false );
        }
        return playbackSpeedLabel;
    }

    public JButton getPlayButton() {
        if (playButton == null) {

            try {
                final ImageIcon icon = new ImageIcon( ImageIO.read( getClass().getResource( ICON_PLAY ) ) );
                playButton = new JButton( icon );
                // to remote the spacing between the image and button's borders
                playButton.setMargin( new Insets( 0, 0, 0, 0 ) );

                // to remove the border
                playButton.setBorder( null );
            }
            catch (final IOException e1) {
                playButton = new JButton( DataImporter.getName( "PLAY" ) );
            }

            playButton.setEnabled( false );
            playButton.addActionListener( mapComponent.getPlaybackScript() );

        }
        return playButton;
    }

    public JSlider getStepSlider() {
        if (stepSlider == null) {
            stepSlider = new JSlider();
            stepSlider.setEnabled( false );
            stepSlider.setMinimum( 0 );
            stepSlider.setValue( 0 );
            stepSlider.addChangeListener( e -> mapComponent.getPlaybackScript().setTime( stepSlider.getValue() ) );
        }
        return stepSlider;
    }

    public JButton getStopButton() {
        if (stopButton == null) {
            try {
                final ImageIcon icon = new ImageIcon( ImageIO.read( getClass().getResource( ICON_STOP ) ) );
                stopButton = new JButton( icon );
                // to remote the spacing between the image and button's borders
                stopButton.setMargin( new Insets( 0, 0, 0, 0 ) );

                // to remove the border
                stopButton.setBorder( null );
            }
            catch (final IOException e1) {
                stopButton = new JButton( DataImporter.getName( "STOP" ) );
            }
            stopButton.setEnabled( false );
            stopButton.addActionListener( mapComponent.getPlaybackScript() );
        }
        return stopButton;
    }

    public JLabel getTimeCode() {
        if (timeCode == null) {
            timeCode = new JLabel( currentTimeCode, SwingConstants.CENTER );
            timeCode.setFont( new Font( "Serif", Font.BOLD, 20 ) );
        }
        return timeCode;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );

        getStepSlider().setEnabled( enabled );
        getAllButton().setEnabled( enabled );
        getPlayButton().setEnabled( enabled );
    }

}
