package de.lighti.components.match;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.lighti.components.match.GameChartComponent.Mode;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;

public class GameStatisticsComponent extends JSplitPane {

    private static final String AVERAGE_TEAM_DISTANCE = DataImporter.getName( "AVERAGE_TEAM_DISTANCE" );
    private static final String TEAM_XP = DataImporter.getName( "TEAM_XP" );
    private static final String TEAM_GOLD = DataImporter.getName( "TEAM_GOLD" );
    private static final String ENCOUNTER = DataImporter.getName( "ENCOUNTER" );

    private JPanel selectionPanel;
    private GameChartComponent chartPanel;
    private JComboBox<String> modeBox;
    private final AppState appState;
    private EncounterComponent encounterComponent;

    public GameStatisticsComponent( AppState state ) {
        appState = state;
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setLeftComponent( getSelectionPanel() );
        setRightComponent( getChartPanel() );
    }

    public JComboBox<String> getAttributeBox() {
        if (modeBox == null) {
            modeBox = new JComboBox<String>() {

                /**
                 *
                 */
                private static final long serialVersionUID = -3161821740467460702L;

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

            modeBox.setAlignmentX( Component.CENTER_ALIGNMENT );
            modeBox.addItem( AVERAGE_TEAM_DISTANCE );
            modeBox.addItem( TEAM_XP );
            modeBox.addItem( TEAM_GOLD );
            modeBox.addItem( ENCOUNTER );
            modeBox.addActionListener( e -> {

                final String s = (String) modeBox.getSelectedItem();
                if (s.equals( AVERAGE_TEAM_DISTANCE )) {
                    getChartPanel().setMode( Mode.AVERAGE_TEAM_DISTANCE );
                    setRightComponent( getChartPanel() );
                }
                else if (s.equals( TEAM_XP )) {
                    getChartPanel().setMode( Mode.TEAM_XP );
                    setRightComponent( getChartPanel() );
                }
                else if (s.equals( TEAM_GOLD )) {
                    getChartPanel().setMode( Mode.TEAM_GOLD );
                    setRightComponent( getChartPanel() );
                }
                else if (s.equals( ENCOUNTER )) {
                    setRightComponent( getEncounterComponent() );
                }
                else {
                    throw new IllegalStateException();
                }
            } );
        }
        return modeBox;
    }

    public GameChartComponent getChartPanel() {
        if (chartPanel == null) {
            chartPanel = new GameChartComponent( appState );
        }
        return chartPanel;
    }

    private Component getEncounterComponent() {
        if (encounterComponent == null) {
            encounterComponent = new EncounterComponent( appState );
        }
        return encounterComponent;

    }

    public JPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new JPanel();
            selectionPanel.setLayout( new BoxLayout( selectionPanel, BoxLayout.Y_AXIS ) );
            selectionPanel.add( getAttributeBox() );
            selectionPanel.add( Box.createVerticalGlue() );
            selectionPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ), "Mode" ) );
        }
        return selectionPanel;
    }
}
