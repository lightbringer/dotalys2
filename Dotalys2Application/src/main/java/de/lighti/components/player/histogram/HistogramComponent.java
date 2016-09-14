package de.lighti.components.player.histogram;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import de.lighti.io.ChartCreator;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;

public class HistogramComponent extends JSplitPane {
    /**
     *
     */
    private static final long serialVersionUID = 9074089320819436807L;
    private static final String DEBUG_DIVIDER = "--- DEBUG ---";
    private ChartPanel chartPanel;
    private JList<String> playerBox;
    private final AppState appState;
    private JComboBox<String> attributeBox;
    private JPanel selectionPanel;

    public HistogramComponent( AppState appState ) {
        this.appState = appState;

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );
        setOneTouchExpandable( false );
        setDividerLocation( 150 );
        setDividerSize( 0 );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setLeftComponent( getSelectionPanel() );
        setRightComponent( getChartPanel() );
    }

    public JComboBox<String> getAttributeBox() {
        if (attributeBox == null) {
            attributeBox = new JComboBox<String>( new DefaultComboBoxModel<String>() ) {

                /**
                 *
                 */
                private static final long serialVersionUID = 7315048556938443236L;

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

            //First add the known values
            attributeBox.addItem( DataImporter.getName( "EXPERIENCE" ) );
            attributeBox.addItem( DataImporter.getName( "GOLD" ) );
            attributeBox.addItem( DataImporter.getName( "DEATHS" ) );
            attributeBox.addItem( DataImporter.getName( "STREAK" ) );
            attributeBox.addItem( DEBUG_DIVIDER );

            attributeBox.setEnabled( false );
            attributeBox.setAlignmentX( Component.CENTER_ALIGNMENT );

            attributeBox.addActionListener( e -> {
                if (!attributeBox.getSelectedItem().equals( DEBUG_DIVIDER )) {
                    final JFreeChart data = ChartCreator.createPlayerHistogram( (String) attributeBox.getSelectedItem(), playerBox.getSelectedValuesList(),
                                    appState );
                    getChartPanel().setChart( data );
                }
            } );

        }
        return attributeBox;
    }

    public ChartPanel getChartPanel() {
        if (chartPanel == null) {
            chartPanel = new ChartPanel( null );
            chartPanel.setBackground( Color.WHITE );
            chartPanel.setBorder( BorderFactory.createEtchedBorder() );

        }
        return chartPanel;
    }

    public JList<String> getPlayerBox() {
        if (playerBox == null) {
            playerBox = new JList<String>( new DefaultListModel<String>() );
            playerBox.setLayoutOrientation( JList.VERTICAL );
            playerBox.setVisibleRowCount( 10 );
            playerBox.setFixedCellHeight( 12 );
            playerBox.setFixedCellWidth( 200 );

            playerBox.addListSelectionListener( e -> {
                if (!attributeBox.getSelectedItem().equals( DEBUG_DIVIDER )) {

                    final JFreeChart chart = ChartCreator.createPlayerHistogram( (String) attributeBox.getSelectedItem(), playerBox.getSelectedValuesList(),
                                    appState );
                    getChartPanel().setChart( chart );
                }
            } );
        }

        return playerBox;
    }

    public JPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new JPanel();
            selectionPanel.setLayout( new BoxLayout( selectionPanel, BoxLayout.Y_AXIS ) );

            final JScrollPane listScroller = new JScrollPane( getPlayerBox() );
            final Dimension d = new Dimension( 200, 30 + 10 * 12 );

            listScroller.setPreferredSize( d );
            listScroller.setMaximumSize( d );

            listScroller.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            listScroller.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

            selectionPanel.add( listScroller );
            selectionPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

            selectionPanel.add( getAttributeBox() );
            selectionPanel.add( Box.createVerticalGlue() );
            selectionPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) );
        }
        return selectionPanel;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );

        getAttributeBox().setEnabled( enabled );
    }
}
