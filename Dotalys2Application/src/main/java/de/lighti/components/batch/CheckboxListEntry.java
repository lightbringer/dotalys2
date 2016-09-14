package de.lighti.components.batch;

import javax.swing.JCheckBox;

class CheckBoxListEntry extends JCheckBox {

    /**
     * 
     */
    private static final long serialVersionUID = -1131696904513781417L;

    private String value = null;

    public CheckBoxListEntry( String itemValue, boolean selected ) {
        super( itemValue == null ? "" : "" + itemValue, selected );
        setValue( itemValue );
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

}