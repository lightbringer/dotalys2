package de.lighti.model;

public interface GameEvent {
    public enum GameEventType {
        Damage( 0 ), Heal( 1 ), Buff_applied( 2 ), Buff_removed( 3 ), Death( 4 );
        public static GameEventType fromId( int id ) {
            for (final GameEventType e : values()) {
                if (e.id == id) {
                    return e;
                }
            }
            return null;
        }

        private final int id;

        private GameEventType( int id ) {
            this.id = id;
        }
    }

    String getAttackerName();

    String getInflictorName();

    String getSourceName();

    String getTargetName();

    GameEventType getType();

    int getValue();
}
