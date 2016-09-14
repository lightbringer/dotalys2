package de.lighti.components.map.tree;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.lighti.components.map.FullMapComponent;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.game.Player;
import de.lighti.model.game.Tower;

public class CheckBoxTree extends JTree {

    public CheckBoxTree( FullMapComponent map ) {
        super( new DefaultTreeModel( new DefaultMutableTreeNode( new CheckBoxNode( DataImporter.getName( "ALL" ), null, false ) ) ) );

        setPreferredSize( new Dimension( 150, 0 ) );
        getSelectionModel().setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
        setRowHeight( 0 );
        setCellRenderer( new CheckBoxNodeRenderer() );
        setCellEditor( new CheckBoxNodeEditor( this ) );
        setEditable( true );
        setToggleClickCount( 0 );
        final DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.addTreeModelListener( new TreeModelHandler() {

            @Override
            public void treeNodesChanged( TreeModelEvent e ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getTreePath().getLastPathComponent();
                DefaultMutableTreeNode parent = node;

                final int index[] = e.getChildIndices();
                if (index != null) {
                    node = (DefaultMutableTreeNode) node.getChildAt( index[0] );
                }
                final CheckBoxNode userData = (CheckBoxNode) node.getUserObject();
                if (node.isLeaf()) {
                    CheckBoxTree.this.setWaitCursor( true );
                    map.getPlaybackScript().setActive( userData.getUserObject(), userData.isSelected() );
                    CheckBoxTree.this.setWaitCursor( false );
                    if (userData.isSelected()) {
                        //Check if we should set the mark on the parent node
                        while (parent != model.getRoot()) {
                            final Enumeration<DefaultMutableTreeNode> children = parent.children();
                            boolean allTrue = true;
                            while (children.hasMoreElements()) {
                                final DefaultMutableTreeNode c = children.nextElement();
                                final CheckBoxNode n = (CheckBoxNode) c.getUserObject();
                                if (!n.isSelected()) {
                                    allTrue = false;
                                    break;
                                }
                            }
                            if (!allTrue) {
                                break;
                            }
                            final CheckBoxNode pData = (CheckBoxNode) parent.getUserObject();
                            if (!pData.isSelected()) {
                                pData.setPropagateEvents( false );
                                pData.setSelected( true );
                                model.nodeChanged( parent );
                            }
                            parent = (DefaultMutableTreeNode) parent.getParent();
                        }
                    }
                    else {
                        final CheckBoxNode pData = (CheckBoxNode) parent.getUserObject();
                        if (pData.isSelected()) {
                            pData.setPropagateEvents( false );
                            pData.setSelected( false );
                            model.nodeChanged( parent );
                        }
                    }
                }
                else {

                    if (userData.isPropagateEvents()) {
                        for (int i = 0; i < node.getChildCount(); i++) {
                            final DefaultMutableTreeNode c = (DefaultMutableTreeNode) node.getChildAt( i );
                            final CheckBoxNode n = (CheckBoxNode) c.getUserObject();
                            n.setSelected( userData.isSelected() );
                            model.nodeChanged( c );
                        }
                    }
                    else {
                        userData.setPropagateEvents( true );
                    }
                }

            }
        } );
    }

    public void buildTreeNodes( AppState state ) {
//  //Encounter
//  final DefaultMutableTreeNode encounters = new DefaultMutableTreeNode( CAT_ENCOUNTER );
//  final List<Encounter> l = appState.getEncounters().stream().sorted( ( e1, e2 ) -> {
//      return Long.compare( e1.getFirstTimestamp(), e2.getFirstTimestamp() );
//  } ).collect( Collectors.toList() );
//  for (final Encounter e : l) {
//      encounters.add( new DefaultMutableTreeNode( e ) );
//  }
//
//  //Movement
//  final DefaultMutableTreeNode movement = new DefaultMutableTreeNode( CAT_MOVEMENT );
//  for (final Player p : players) {
//      movement.add( new DefaultMutableTreeNode( p.getName() ) );
//  }
//  //Deaths
//  final DefaultMutableTreeNode deaths = new DefaultMutableTreeNode( CAT_DEATHS );
//  for (final Player p : players) {
//      deaths.add( new DefaultMutableTreeNode( p.getName() ) );
//  }
//  //Abilities
//  final DefaultMutableTreeNode abilities = new DefaultMutableTreeNode( CAT_ABILITIES );
//  for (final Player p : players) {
//      final DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode( p.getName() );
//      final Hero h = p.getHero();
//      for (final Ability a : h.getAbilities()) {
//          if (!a.getKey().equals( "attribute_bonus" )) {
//              playerNode.add( new DefaultMutableTreeNode( a ) );
//          }
//      }
//      abilities.add( playerNode );
//  }
//
//  //Items
//  final DefaultMutableTreeNode items = new DefaultMutableTreeNode( CAT_ITEMS );
//  for (final Player p : players) {
//      final DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode( p.getName() );
//      final Hero h = p.getHero();
//      //The hero internally stores an object for every item entity it seen. We have
//      //to flatten this down to only unique item names. We'll ask the Hero later for only
//      //specific items of a certain type when we build the data set in the ChartCreator
//      final Set<String> itemNames = new HashSet<String>();
//      for (final Dota2Item i : h.getAllItems()) {
//          //Just take items that can be and were actively used
//          if (!i.getUsage().isEmpty()) {
//              itemNames.add( i.getKey() );
//          }
//      }
//      for (final String s : itemNames) {
//          playerNode.add( new DefaultMutableTreeNode( s ) );
//      }
//      items.add( playerNode );
//  }
//
//
//  root.add( movement );
//  root.add( deaths );
//  root.add( abilities );
//  root.add( items );
//  root.add( encounters );
//  model.reload( root );

        //Towers
        final DefaultMutableTreeNode towersNode = new DefaultMutableTreeNode( new CheckBoxNode( DataImporter.getName( "TOWERS" ), null, false ) );
        for (final Tower t : state.getReplay().getTowers()) {
            final DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode( new CheckBoxNode( t.getKey(), t, false ) );
            towersNode.add( playerNode );
        }

        //Players
        final DefaultMutableTreeNode playersNode = new DefaultMutableTreeNode( new CheckBoxNode( DataImporter.getName( "PLAYER" ), null, false ) );
        for (final Player p : state.getReplay().getPlayers()) {
            final DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode( new CheckBoxNode( p.getName(), p.getHero(), false ) );
            playersNode.add( playerNode );
        }

        //Roshan
        final DefaultMutableTreeNode roshanNode = new DefaultMutableTreeNode(
                        new CheckBoxNode( DataImporter.getName( "ROSHAN" ), state.getReplay().getRoshan(), false ) );

        //Creeps
        final DefaultMutableTreeNode creepNode = new DefaultMutableTreeNode( new CheckBoxNode( DataImporter.getName( "CREEPS" ), null, false ) );
        final DefaultMutableTreeNode radiantCreepNode = new DefaultMutableTreeNode(
                        new CheckBoxNode( DataImporter.getName( "RADIANT" ), state.getReplay().getCreeps( true ), false ) );
        final DefaultMutableTreeNode direCreepNode = new DefaultMutableTreeNode(
                        new CheckBoxNode( DataImporter.getName( "DIRE" ), state.getReplay().getCreeps( false ), false ) );
        creepNode.add( radiantCreepNode );
        creepNode.add( direCreepNode );

        //Root
        final DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        if (root != null) {
            root.removeAllChildren();
        }
        else {
            root = new DefaultMutableTreeNode();
            model.setRoot( root );
        }
        root.add( playersNode );
        root.add( towersNode );
        root.add( roshanNode );
        root.add( creepNode );
        model.reload( root );
    }

    private void setWaitCursor( boolean b ) {
        if (b) {
            getRootPane().setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        }
        else {
            getRootPane().setCursor( Cursor.getDefaultCursor() );
        }

    }
}
