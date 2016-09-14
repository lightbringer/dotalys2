package de.lighti.components.map;

import java.awt.Dimension;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.lighti.components.console.ConsoleComponent;
import de.lighti.components.map.data.Dota2MapModel;
import de.lighti.components.map.data.FullDota2MapModel;
import de.lighti.components.map.tree.CheckBoxTree;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.game.Ability;
import de.lighti.model.game.Creep;

public class FullMapComponent extends JPanel implements MapComponent {
    /**
     *
     */
    private static final long serialVersionUID = 1045770296903996356L;

    private final AppState appState;

    private CheckBoxTree attributeTree;

    private MapCanvasComponent mapCanvas;

    private OptionContainer optionContainer;

    private JScrollPane mapCanvasContainer;

    private JComponent eventLogContainer;

    private JScrollPane eventLogScrollPane;

    private ConsoleComponent eventLogPane;

    private JPanel eventLogOptionsContainer;

    private JScrollPane attributeTreePane;

    private JLabel radiantGold;

    private JPanel statsPanel;

    private JLabel direXp;

    private JLabel radiantXp;

    private JLabel direGold;
    private final FullDota2MapModel playbackScript;

//    private final static Logger LOGGER = Logger.getLogger( MapCanvasComponent.class.getName() );

    public FullMapComponent( AppState state ) {
        appState = state;
        playbackScript = new FullDota2MapModel( this, state );

        final BoxLayout layout = new BoxLayout( this, BoxLayout.X_AXIS );
        setLayout( layout );
        add( getAttributeTreePane() );
        add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        add( getMapCanvansContainer() );
        add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        add( getEventLogContainer() );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    }

    public CheckBoxTree getAttributeTree() {
        if (attributeTree == null) {
            attributeTree = new CheckBoxTree( this );
        }
        return attributeTree;
    }

    public JLabel getDireGoldLabel() {
        if (direGold == null) {
            direGold = new JLabel();
        }
        return direGold;
    }

    public JLabel getDireXPLabel() {
        if (direXp == null) {
            direXp = new JLabel();
        }
        return direXp;
    }

    public ConsoleComponent getEventLogPane() {
        if (eventLogPane == null) {
            eventLogPane = new ConsoleComponent();

        }
        return eventLogPane;
    }

    @Override
    public MapCanvasComponent getMapCanvas() {
        if (mapCanvas == null) {
            mapCanvas = new MapCanvasComponent();
        }
        return mapCanvas;
    }

    public OptionContainer getOptionContainer() {
        if (optionContainer == null) {
            optionContainer = new OptionContainer( this );
        }
        return optionContainer;
    }

    @Override
    public Dota2MapModel getPlaybackScript() {
        return playbackScript;
    }

    public JLabel getRadiantGoldLabel() {
        if (radiantGold == null) {
            radiantGold = new JLabel();
        }
        return radiantGold;
    }

    public JLabel getRadiantXPLabel() {
        if (radiantXp == null) {
            radiantXp = new JLabel();
        }
        return radiantXp;
    }

    public void setGold( boolean isRadiant, int gold ) {
        if (isRadiant) {
            getRadiantGoldLabel().setText( Integer.toString( gold ) );
        }
        else {
            getDireGoldLabel().setText( Integer.toString( gold ) );
        }

    }

    public void setXp( boolean isRadiant, int xp ) {
        if (isRadiant) {
            getRadiantXPLabel().setText( Integer.toString( xp ) );
        }
        else {
            getDireXPLabel().setText( Integer.toString( xp ) );
        }

    }

    private JScrollPane getAttributeTreePane() {
        if (attributeTreePane == null) {
            attributeTreePane = new JScrollPane( getAttributeTree() );
        }
        return attributeTreePane;
    }

    private JComponent getEventLogContainer() {
        if (eventLogContainer == null) {
            eventLogContainer = new JPanel();
            final BoxLayout logLayout = new BoxLayout( eventLogContainer, BoxLayout.Y_AXIS );
            eventLogContainer.setLayout( logLayout );
            eventLogContainer.add( getEventLogOptionsContainer() );
            eventLogContainer.add( getEventLogScrollPane() );
        }

        return eventLogContainer;
    }

    private JComponent getEventLogOptionsContainer() {
        if (eventLogOptionsContainer == null) {
            eventLogOptionsContainer = new JPanel();
            final BoxLayout logOptionsLayout = new BoxLayout( eventLogOptionsContainer, BoxLayout.X_AXIS );
            eventLogOptionsContainer.setLayout( logOptionsLayout );

            final JCheckBox logAbilities = new JCheckBox( DataImporter.getName( "ABILITIES" ) );
            final JCheckBox logCreepKills = new JCheckBox( DataImporter.getName( "CREEPS" ) );
            final JCheckBox logRespawnTimer = new JCheckBox( "Respawn Timer" );

            final ItemListener listener = e -> {
                if (e.getSource() == logAbilities) {
                    if (logAbilities.isSelected()) {
                        getEventLogPane().ignoreSources( Ability.class );
                    }
                    else {
                        getEventLogPane().removeIgnoreSources( Ability.class );
                    }
                }
                else if (e.getSource() == logCreepKills) {
                    if (logCreepKills.isSelected()) {
                        getEventLogPane().ignoreSources( Creep.class );
                    }
                    else {
                        getEventLogPane().removeIgnoreSources( Creep.class );
                    }
                }
//                    if (e.getSource() == logRespawnTimer) {
//                        if (logRespawnTimer.isSelected()) {
//                            getOptionContainer().addLogExclusion( "Respawning in " );
//                        }
//                        else {
//                            getOptionContainer().removeLogExclusion( "Respawning in " );
//                        }
//                    }
            };
            logAbilities.addItemListener( listener );
            logCreepKills.addItemListener( listener );
            logRespawnTimer.addItemListener( listener );

            eventLogOptionsContainer.add( new JLabel( DataImporter.getName( "HIDE" ) + ':' ) );
            eventLogOptionsContainer.add( logAbilities );
            eventLogOptionsContainer.add( logCreepKills );
            eventLogOptionsContainer.add( logRespawnTimer );
        }

        return eventLogOptionsContainer;

    }

    private JScrollPane getEventLogScrollPane() {
        if (eventLogScrollPane == null) {
            eventLogScrollPane = new JScrollPane( getEventLogPane() );
            eventLogScrollPane.setPreferredSize( new Dimension( 300, 600 ) );
        }
        return eventLogScrollPane;
    }

    private JComponent getMapCanvansContainer() {
        if (mapCanvasContainer == null) {
            final JPanel mapCanvasContainerView = new JPanel();
            final BoxLayout canvasLayout = new BoxLayout( mapCanvasContainerView, BoxLayout.Y_AXIS );
            mapCanvasContainerView.setLayout( canvasLayout );
            mapCanvasContainerView.add( getStatsPanel() );
            mapCanvasContainerView.add( getMapCanvas() );
            mapCanvasContainerView.add( getOptionContainer() );
            mapCanvasContainer = new JScrollPane( mapCanvasContainerView );
        }

        return mapCanvasContainer;
    }

    private JPanel getStatsPanel() {
        if (statsPanel == null) {
            statsPanel = new JPanel();

            final JLabel radiantGoldLabel = new JLabel( "Radiant Gold" );
            final JLabel direGoldLabel = new JLabel( "Dire Gold" );

            final JLabel radiantXpLabel = new JLabel( "Radiant XP" );
            final JLabel direXpLabel = new JLabel( "Dire XP" );

            final Dimension size = new Dimension( 15, 5 );

            getRadiantGoldLabel().setMinimumSize( size );
            getRadiantXPLabel().setMinimumSize( size );

            getDireGoldLabel().setMinimumSize( size );
            getDireXPLabel().setMinimumSize( size );

            final javax.swing.GroupLayout layout = new javax.swing.GroupLayout( statsPanel );
            statsPanel.setLayout( layout );
            layout.setHorizontalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                            .addGroup( layout.createSequentialGroup().addContainerGap( javax.swing.GroupLayout.DEFAULT_SIZE, 25 )
                                            .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addComponent( radiantGoldLabel )
                                                            .addComponent( radiantXpLabel ) )
                            .addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.UNRELATED )
                            .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addComponent( radiantGold )
                                            .addComponent( radiantXp ) )
                            .addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE )
                            .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                                            .addComponent( direGoldLabel, javax.swing.GroupLayout.Alignment.TRAILING )
                                            .addComponent( direXpLabel, javax.swing.GroupLayout.Alignment.TRAILING ) )
                            .addGap( 5, 5, 5 )
                            .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                                            .addComponent( direXp, javax.swing.GroupLayout.Alignment.TRAILING ).addComponent( direGold ) )
                            .addContainerGap( javax.swing.GroupLayout.DEFAULT_SIZE, 25 ) ) );
            layout.setVerticalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                            .addGroup( layout.createSequentialGroup().addContainerGap()
                                            .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE ).addComponent( radiantGoldLabel )
                                                            .addComponent( direGold ).addComponent( radiantGold ).addComponent( direGoldLabel ) )
                            .addGap( 5, 5, 5 )
                            .addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE ).addComponent( radiantXpLabel )
                                            .addComponent( direXp ).addComponent( radiantXp ).addComponent( direXpLabel ) )
                            .addContainerGap( javax.swing.GroupLayout.DEFAULT_SIZE, 5 ) ) );
        }

        return statsPanel;
    }

}
