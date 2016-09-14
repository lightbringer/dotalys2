package de.lighti.model;

import skadistats.clarity.model.CombatLogEntry;

public class CombatLogEvent implements de.lighti.model.GameEvent {

    private final CombatLogEntry entry;

    public CombatLogEvent( CombatLogEntry event ) {
        entry = event;
    }

    @Override
    public String getAttackerName() {
        return entry.getAttackerName();
    }

    @Override
    public String getInflictorName() {
        return entry.getInflictorName();
    }

    @Override
    public String getSourceName() {
        return entry.getDamageSourceName();
    }

    @Override
    public String getTargetName() {
        return entry.getTargetName();
    }

    public String getTargetNameCompiled() {
        return getTargetName() + (isTargetIllusion() ? " (Illusion)" : "");
    }

    public String getTargetSourceName() {
        return entry.getTargetSourceName();
    }

    public float getTimestamp() {
        return entry.getTimestamp();
    }

    public float getTimestampRaw() {
        return entry.getTimestampRaw();
    }

    @Override
    public GameEventType getType() {
        return GameEventType.fromId( entry.getType().getNumber() );
    }

    @Override
    public int getValue() {
        return entry.getValue();
    }

    public boolean isTargetIllusion() {
        return entry.isTargetIllusion();
    }

}