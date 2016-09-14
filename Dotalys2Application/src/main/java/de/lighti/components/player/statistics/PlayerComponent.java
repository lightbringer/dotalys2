package de.lighti.components.player.statistics;

import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.game.Ability;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Hero.ItemEvent;
import de.lighti.model.game.Player;
import de.lighti.model.game.Player.KillingSpree;

public class PlayerComponent extends JSplitPane {

    private final AppState appState;
    private JComboBox<String> playerBox;
    private final static NumberFormat TWO_DIGITS = new DecimalFormat( "#####.##" );

    public PlayerComponent( AppState appState ) {
        super( JSplitPane.HORIZONTAL_SPLIT, null, null );

        this.appState = appState;

        setOneTouchExpandable( false );
        setDividerLocation( 150 );
        setDividerSize( 0 );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setLeftComponent( createLeftComponent() );
        setRightComponent( createRightComponent() );
    }

    private Component createLeftComponent() {

        final JPanel leftPane = new JPanel();
        leftPane.setLayout( new BoxLayout( leftPane, BoxLayout.Y_AXIS ) );

        leftPane.add( getPlayerBox() );
        leftPane.add( Box.createVerticalGlue() );

        leftPane.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ), DataImporter.getName( "PLAYER" ) ) );
        return leftPane;
    }

    private JComponent createPlayerBuildOrderTab() {
        final BuildOrderComponent bc = new BuildOrderComponent();
        getPlayerBox().addItemListener( e -> {
            final String id = (String) playerBox.getSelectedItem();
            if (id == null) {
                return;
            }

            final Player p = appState.getReplay().getPlayerByName( id );
            final Queue<Dota2Item> buildOrder = new LinkedBlockingQueue<Dota2Item>();

            final Hero hero = p.getHero();
            if (hero != null) {
                final List<ItemEvent> completeLog = hero.getItemLog();

                for (final Hero.ItemEvent n : completeLog) {

                    //Only the bag, and only added items
                    if (n.slot <= 5 && n.added) {
                        buildOrder.add( n.item );
                    }
//                        if (n.startsWith( "+" )) {
//                            buildOrder.add( appState.getItemByName( n.substring( 1 ) ) );
//                        }
                }
                bc.setItems( buildOrder );
            }

        } );
        return bc;
    }

    private JComponent createPlayerSkillTreeTab() {
        final SkillTreecomponent c = new SkillTreecomponent();
        getPlayerBox().addItemListener( e -> {
            final String id = (String) playerBox.getSelectedItem();
            if (id == null) {
                return;
            }
            final Player p = appState.getReplay().getPlayerByName( id );
            final Hero hero = p.getHero();

            final List<Ability> abilities = new ArrayList<Ability>();
            for (final Ability a : hero.getAbilities()) {
                abilities.add( a );
            }
            c.setAbilities( abilities );
            c.repaint();

        } );
        return c;
    }

    private JComponent createPlayerStatisticsTab() {

        final JTable table = new JTable();

        final TableModel model = new DefaultTableModel( 9, 2 ) {

            /**
             *
             */
            private static final long serialVersionUID = -5417710077260844257L;

            @Override
            public boolean isCellEditable( int row, int column ) {
                //all cells false
                return false;
            }
        };
        table.setModel( model );

        model.setValueAt( DataImporter.getName( "NAME" ), 0, 0 );
        model.setValueAt( DataImporter.getName( "TEAM" ), 1, 0 );
        model.setValueAt( DataImporter.getName( "HERO" ), 2, 0 );
        model.setValueAt( DataImporter.getName( "TOTAL_GOLD" ), 3, 0 );
        model.setValueAt( DataImporter.getName( "GOLD_PER_MINUTE" ), 4, 0 );
        model.setValueAt( DataImporter.getName( "TOTAL_XP" ), 5, 0 );
        model.setValueAt( DataImporter.getName( "XP_PER_MINUTE" ), 6, 0 );
        model.setValueAt( DataImporter.getName( "DEATHS" ), 7, 0 );
        model.setValueAt( DataImporter.getName( "HIGHEST_STREAK" ), 8, 0 );
        getPlayerBox().addActionListener( e -> {
            final String id = (String) getPlayerBox().getSelectedItem();
            final Player p = appState.getReplay().getPlayerByName( id );
            if (p != null) {
                final long ms = appState.getReplay().getGameLength();

                final double minutes = ms / 60000.0;

                final String team = p.isRadiant() ? DataImporter.getName( "RADIANT" ) : DataImporter.getName( "DIRE" );
                final Hero hero = p.getHero();
                final String name = hero != null ? DataImporter.getName( hero.getKey() ) : DataImporter.getName( "UNKNOWN_HERO" );
                final int gold = p.getTotalEarnedGold();
                final double gpm = gold / minutes;
                final int toalXp = p.getTotalXP();
                final double xpm = toalXp / minutes;
                final int deaths = p.getHero().getDeaths().size();
                final KillingSpree streak = p.getHighestStreak();

                model.setValueAt( p.getName(), 0, 1 );
                model.setValueAt( team, 1, 1 );
                model.setValueAt( name, 2, 1 );
                model.setValueAt( TWO_DIGITS.format( gold ), 3, 1 );

                model.setValueAt( TWO_DIGITS.format( gpm ), 4, 1 );
                model.setValueAt( TWO_DIGITS.format( toalXp ), 5, 1 );
                model.setValueAt( TWO_DIGITS.format( xpm ), 6, 1 );
                model.setValueAt( TWO_DIGITS.format( deaths ), 7, 1 );

                model.setValueAt( DataImporter.getName( streak.name() ), 8, 1 );
            }
        } );

        table.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
        table.setPreferredSize( new Dimension( 300, 600 ) ); //Magic numbers
        final JPanel container = new JPanel();
        container.add( table );
        return container;
    }

    private Component createRightComponent() {
        final JTabbedPane rightPane = new JTabbedPane();
        rightPane.addTab( "Statistics", createPlayerStatisticsTab() );
        rightPane.addTab( "Build Order", createPlayerBuildOrderTab() );
        rightPane.addTab( "Skill Tree", createPlayerSkillTreeTab() );
        rightPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        return rightPane;
    }

    public JComboBox<String> getPlayerBox() {
        if (playerBox == null) {
            playerBox = new JComboBox<String>() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                /**
                 * @inherited <p>
                 */
                @Override
                public Dimension getMaximumSize() {
                    final Dimension max = super.getMaximumSize();
                    max.height = getPreferredSize().height;
                    return max;
                }

            };

            playerBox.setAlignmentX( Component.CENTER_ALIGNMENT );
        }
        return playerBox;
    }

}
