package de.lighti.components.batch;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class CheckBoxList extends JList<CheckBoxListEntry> {

    /**
     *
     */
    private static final long serialVersionUID = 7046275812111622580L;

    public CheckBoxList() {
        super();

        setModel( new DefaultListModel<CheckBoxListEntry>() );
        setCellRenderer( new CheckboxCellRenderer() );

        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                final int index = locationToIndex( e.getPoint() );

                if (index != -1) {
                    final JCheckBox checkbox = getModel().getElementAt( index );
                    final boolean old = checkbox.isSelected();
                    checkbox.setSelected( !old );
                    repaint();
                    CheckBoxList.this.firePropertyChange( getModel().getElementAt( index ).getValue(), old, !old );
                }
            }
        }

        );

        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    }

    public int[] getCheckedIndexes() {
        final java.util.List<Integer> list = new java.util.ArrayList<Integer>();
        final DefaultListModel<CheckBoxListEntry> dlm = (DefaultListModel<CheckBoxListEntry>) getModel();
        for (int i = 0; i < dlm.size(); ++i) {
            final JCheckBox checkbox = getModel().getElementAt( i );

            if (checkbox.isSelected()) {
                list.add( new Integer( i ) );
            }
        }

        final int[] indexes = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            indexes[i] = list.get( i ).intValue();
        }

        return indexes;
    }

    @Override
    public List<CheckBoxListEntry> getSelectedValuesList() {
        final java.util.List<CheckBoxListEntry> list = new java.util.ArrayList<CheckBoxListEntry>();
        final DefaultListModel<CheckBoxListEntry> dlm = (DefaultListModel<CheckBoxListEntry>) getModel();
        for (int i = 0; i < dlm.size(); ++i) {
            final CheckBoxListEntry checkbox = getModel().getElementAt( i );

            if (checkbox.isSelected()) {
                list.add( checkbox );
            }

        }
        return list;
    }

}