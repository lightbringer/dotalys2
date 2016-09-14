package de.lighti;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import de.lighti.components.DotalysMenuBar;
import de.lighti.components.console.ConsoleComponent;
import de.lighti.components.map.FullMapComponent;
import de.lighti.components.match.GameStatisticsComponent;
import de.lighti.components.player.histogram.HistogramComponent;
import de.lighti.components.player.statistics.PlayerComponent;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.GameEvent;
import de.lighti.model.Statics;
import de.lighti.model.game.Player;
import de.lighti.model.state.ParseState;

public class Dotalys2App extends JFrame implements Dotalys2, GameEventListener {

    /**
     *
     */
    private static final long serialVersionUID = -5920990846685808741L;

    static {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

        }
        catch (final Exception e) {
            // Don't care
        }
    }

    private final AppState appState;

    private JTabbedPane mainView;

    private HistogramComponent histogramComponent;

    private PlayerComponent playerComponent;

    private FullMapComponent mapComponent;

    private ConsoleComponent console;

    public Dotalys2App() {
        super( DataImporter.getName( "APPLICATION_TITLE" ) + " " + Statics.DOTALYS_VERSION );

        appState = new AppState( this );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setSize( new Dimension( 1280, 1024 ) );
        setResizable( false );

        final JComponent com = getMainView();
        com.setPreferredSize( getContentPane().getPreferredSize() );

        getContentPane().add( com, BorderLayout.CENTER );
        // pack();
        setJMenuBar( new DotalysMenuBar( this ) );

        try {
            setIconImage( ImageIO.read( Dotalys2App.class.getResourceAsStream( "dota2icon.png" ) ) );
        }
        catch (final IOException e) {
            //NOP
        }
        DataImporter.loadProperties( appState );
    }

    @Override
    public void enableSave( boolean maySave ) {
        ((DotalysMenuBar) getJMenuBar()).getFileSaveDatabaseMenuItem().setEnabled( maySave && getAppState().getReplay() != null );
    }

    @Override
    public void entityCreated( long tickMs, Entity entity ) {
        // NOP

    }

    @Override
    public void entityRemoved( long tickMs, Entity removed ) {
        // NOP

    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        // NOP
    }

    @Override
    public void gameEvent( long timeStamp, GameEvent event ) {
        // NOP

    }

    @Override
    public AppState getAppState() {
        return appState;
    }

    public HistogramComponent getHistogramComponent() {
        if (histogramComponent == null) {
            histogramComponent = new HistogramComponent( appState );
        }
        return histogramComponent;
    }

    public JComponent getMainView() {
        if (mainView == null) {
            mainView = new JTabbedPane();/* {
                                         @Override
                                         public void setEnabled( boolean enabled ) {
                                         super.setEnabled( enabled );
                                         
                                         //The player box which is visible at start is the only one we need to send an extra notification to
                                         getHistogramComponent().setEnabled( enabled );
                                         }
                                         };*/
            mainView.addTab( DataImporter.getName( "PLAYER_HISTOGRAMS" ), getHistogramComponent() );
            mainView.addTab( DataImporter.getName( "PLAYER_STATISTICS" ), getPlayerComponent() );
            mainView.addTab( DataImporter.getName( "MAP_EVENTS" ), getMapComponent() );
            mainView.addTab( DataImporter.getName( "MATCH_ANALYSIS" ), new GameStatisticsComponent( getAppState() ) );

            //Disable everything until we have replay in memory
            for (int i = 0; i < mainView.getTabCount(); i++) {
//                mainView.setEnabledAt( i, false );
            }

            //This is enabled by default
            mainView.addTab( DataImporter.getName( "CONSOLE" ), getConsole() );

            mainView.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        }

        return mainView;
    }

    public FullMapComponent getMapComponent() {
        if (mapComponent == null) {
            mapComponent = new FullMapComponent( appState );
        }
        return mapComponent;
    }

    public PlayerComponent getPlayerComponent() {
        if (playerComponent == null) {
            playerComponent = new PlayerComponent( appState );
        }
        return playerComponent;
    }

    @Override
    public void handleError( Throwable t ) {
        Main.displayException( t );

    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        //Unhandled PlayerVariables
        final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getHistogramComponent().getAttributeBox().getModel();
        for (final String s : getAppState().getReplay().getUnhandledPlayerVariableNames()) {
            model.addElement( s );
        }

        //Players
        final DefaultListModel<String> playerHistogramModel = (DefaultListModel<String>) getHistogramComponent().getPlayerBox().getModel();
        final DefaultComboBoxModel<String> playerModel = (DefaultComboBoxModel<String>) getPlayerComponent().getPlayerBox().getModel();
        final List<Player> sortedPlayers = new ArrayList<Player>( getAppState().getReplay().getPlayers() );
        Collections.sort( sortedPlayers, ( o1, o2 ) -> Integer.compare( o1.getId(), o2.getId() ) );
        for (final Player p : sortedPlayers) {
            playerHistogramModel.addElement( p.getName() );
            playerModel.addElement( p.getName() );
        }
        getMapComponent().getMapCanvas().setGameVersion( appState.getReplay().getGameVersion() );
        getMapComponent().getAttributeTree().buildTreeNodes( appState );

        //Now fire up the application
        for (int i = 0; i < mainView.getTabCount(); i++) {
            mainView.setEnabledAt( i, true );
        }
        getHistogramComponent().setEnabled( true );

    }

    @Override
    public void tickEnd( long tick, ParseState state ) {
        // NOP

    }

    private ConsoleComponent getConsole() {
        if (console == null) {
            //False means we probably run in the Eclipse IDE where it's more convenient to have
            //the output in the system console

            final boolean redirectSysout = System.getenv( "stdout" ) == null;

            console = new ConsoleComponent( redirectSysout );
        }
        return console;
    }

}
