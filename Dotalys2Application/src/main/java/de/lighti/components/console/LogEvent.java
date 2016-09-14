package de.lighti.components.console;

import de.lighti.model.game.Unit;

public class LogEvent implements Comparable<LogEvent> {
    public Unit u;
    public String event;
    public Object object;
    public long time;

    public LogEvent( Unit u, String event, long time ) {
        super();
        this.u = u;
        this.event = event;
        this.time = time;
    }

    @Override
    public int compareTo( LogEvent o ) {
        return Long.compare( time, o.time );
    }
}