package de.lighti.components.match;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.lighti.components.map.MapCanvasComponent;
import de.lighti.components.map.MapComponent;
import de.lighti.components.map.OptionContainer;
import de.lighti.components.map.data.Dota2MapModel;
import de.lighti.model.AppState;
import de.lighti.model.Encounter;

public class EncounterComponent extends JComponent implements MapComponent {
//    private final AppState appState;
    private JComboBox<Encounter> encounterBox;
    private JTextArea encounterPanel;
    private MapCanvasComponent mapCanvas;
    private OptionContainer optionContainer;

    private final EncounterMapModel script;

    public EncounterComponent( AppState appState ) {
//        this.appState = appState;
        script = new EncounterMapModel( this, appState );

        setLayout( new BorderLayout() );

        add( getEncounterBox(), BorderLayout.NORTH );
        add( new JScrollPane( getEncounterPanel() ), BorderLayout.CENTER );
        final JPanel canvas = new JPanel();
        canvas.setLayout( new BorderLayout() );
        canvas.add( getMapCanvas(), BorderLayout.NORTH );
        canvas.add( getOptionContainer(), BorderLayout.SOUTH );
        add( canvas, BorderLayout.EAST );
    }

    public JComboBox<Encounter> getEncounterBox() {
        if (encounterBox == null) {
            encounterBox = new JComboBox<Encounter>();//appState.getReplay().getEncounters().stream().toArray( size -> new Encounter[size] ) );
            encounterBox.setRenderer( new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
                    final Encounter e = (Encounter) value;
                    //XXX
                    if (e != null) {
                        setText( "Encounter [" + e.getLastTick() + "]" );
                    }
                    return this;
                }

            } );
            encounterBox.addActionListener( e -> {
                final Encounter enc = (Encounter) ((JComboBox<Encounter>) e.getSource()).getSelectedItem();
                getPlaybackScript().setActive( enc, true );
                getEncounterPanel().setText( enc.toString() );
            } );
        }
        return encounterBox;
    }

    public JTextArea getEncounterPanel() {
        if (encounterPanel == null) {
            encounterPanel = new JTextArea();
//            encounterPanel.setPreferredSize( new Dimension( 100, 0 ) );
            encounterPanel.setEditable( false );
        }
        return encounterPanel;
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
        return script;
    }

}
