package de.lighti.components.map.tree;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

    CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

    ChangeEvent changeEvent = null;

    JTree tree;

    public CheckBoxNodeEditor( JTree tree ) {
        this.tree = tree;
    }

    @Override
    public Object getCellEditorValue() {
        final CheckBox checkbox = renderer.getLeafRenderer();

        final CheckBoxNode checkBoxNode = new CheckBoxNode( checkbox.getText(), checkbox.getUserObject(), checkbox.isSelected() );
        return checkBoxNode;
    }

    @Override
    public Component getTreeCellEditorComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row ) {

        final Component editor = renderer.getTreeCellRendererComponent( tree, value, true, expanded, leaf, row, true );

        // editor always selected / focused
        final ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged( ItemEvent itemEvent ) {
                if (stopCellEditing()) {
                    fireEditingStopped();
                }
            }
        };
        if (editor instanceof JCheckBox) {
            ((JCheckBox) editor).addItemListener( itemListener );
        }

        return editor;
    }

    @Override
    public boolean isCellEditable( EventObject event ) {
        boolean returnValue = false;
        if (event instanceof MouseEvent) {
            final MouseEvent mouseEvent = (MouseEvent) event;
            final TreePath path = tree.getPathForLocation( mouseEvent.getX(), mouseEvent.getY() );
            if (path != null) {
                final Object node = path.getLastPathComponent();
                if (node != null && node instanceof DefaultMutableTreeNode) {
                    final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    final Object userObject = treeNode.getUserObject();
                    returnValue = userObject instanceof CheckBoxNode;
                }
            }
        }
        return returnValue;
    }
}