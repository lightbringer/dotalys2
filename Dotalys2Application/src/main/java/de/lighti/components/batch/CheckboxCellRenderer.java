package de.lighti.components.batch;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

class CheckboxCellRenderer extends DefaultListCellRenderer {
    /**
     *
     */
    private static final long serialVersionUID = 6283244183499595383L;

    @Override
    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
        if (value instanceof CheckBoxListEntry) {
            final CheckBoxListEntry checkbox = (CheckBoxListEntry) value;
            checkbox.setBackground( isSelected ? list.getSelectionBackground() : list.getBackground() );
            checkbox.setForeground( isSelected ? list.getSelectionForeground() : list.getForeground() );

            checkbox.setEnabled( list.isEnabled() );
            checkbox.setFont( list.getFont() );
            checkbox.setFocusPainted( false );
            checkbox.setBorderPainted( true );
            checkbox.setBorder( isSelected ? UIManager.getBorder( "List.focusCellHighlightBorder" ) : noFocusBorder );

            return checkbox;
        }
        else {
            return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
        }
    }
}