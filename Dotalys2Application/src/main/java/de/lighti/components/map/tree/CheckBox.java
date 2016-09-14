package de.lighti.components.map.tree;

import javax.swing.JCheckBox;

public class CheckBox extends JCheckBox {
    private Object userObject;

    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject( Object userObject ) {
        this.userObject = userObject;
    }

}
