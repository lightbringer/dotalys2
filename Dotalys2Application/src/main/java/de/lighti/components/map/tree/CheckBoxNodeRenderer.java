package de.lighti.components.map.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

class CheckBoxNodeRenderer implements TreeCellRenderer {
    private final CheckBox leafRenderer = new CheckBox();

    private final DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

    Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;

    public CheckBoxNodeRenderer() {
        Font fontValue;
        fontValue = UIManager.getFont( "Tree.font" );
        if (fontValue != null) {
            leafRenderer.setFont( fontValue );
        }
        final Boolean booleanValue = (Boolean) UIManager.get( "Tree.drawsFocusBorderAroundIcon" );
        leafRenderer.setFocusPainted( booleanValue != null && booleanValue.booleanValue() );

        selectionBorderColor = UIManager.getColor( "Tree.selectionBorderColor" );
        selectionForeground = UIManager.getColor( "Tree.selectionForeground" );
        selectionBackground = UIManager.getColor( "Tree.selectionBackground" );
        textForeground = UIManager.getColor( "Tree.textForeground" );
        textBackground = UIManager.getColor( "Tree.textBackground" );
    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {

        Component returnValue;
//        if (leaf) {

        final String stringValue = tree.convertValueToText( value, selected, expanded, leaf, row, false );
        leafRenderer.setText( stringValue );
        leafRenderer.setSelected( false );

        leafRenderer.setEnabled( tree.isEnabled() );

        if (selected) {
            leafRenderer.setForeground( selectionForeground );
            leafRenderer.setBackground( selectionBackground );
        }
        else {
            leafRenderer.setForeground( textForeground );
            leafRenderer.setBackground( textBackground );
        }

        if (value != null && value instanceof DefaultMutableTreeNode) {
            final Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof CheckBoxNode) {
                final CheckBoxNode node = (CheckBoxNode) userObject;
                leafRenderer.setText( node.getText() );
                leafRenderer.setSelected( node.isSelected() );
                leafRenderer.setUserObject( node.getUserObject() );
            }
        }
        leafRenderer.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
        returnValue = leafRenderer;
//        }
//        else {
//            returnValue = nonLeafRenderer.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
//        }
        return returnValue;
    }

    protected CheckBox getLeafRenderer() {
        return leafRenderer;
    }
}