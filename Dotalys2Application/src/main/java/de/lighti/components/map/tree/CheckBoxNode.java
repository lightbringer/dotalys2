package de.lighti.components.map.tree;

class CheckBoxNode {
    String text;
    Object userObject;
    boolean selected;

    private boolean propagateEvents;

    public CheckBoxNode( String text, Object object, boolean selected ) {
        userObject = object;
        this.text = text;
        this.selected = selected;
        propagateEvents = true;
    }

    public String getText() {
        return text;
    }

    public Object getUserObject() {
        return userObject;
    }

    public boolean isPropagateEvents() {
        return propagateEvents;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setPropagateEvents( boolean b ) {
        propagateEvents = b;

    }

    public void setSelected( boolean newValue ) {
        selected = newValue;
    }

    public void setText( String newValue ) {
        text = newValue;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + text + "/" + selected + "]";
    }
}