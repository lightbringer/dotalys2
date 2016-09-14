package de.lighti.model;

import java.util.Properties;

import de.lighti.Dotalys2;

public class AppState {
    private Replay replay;

    private Properties properties;

    private static String DATABASE_SERVER = "database.server";

    private static String DATABASE_USER = "database.user";

    private static String DATABASE_PASSWORD = "database.password";

    private static String DATABASE = "database";

    private static String DATABASE_CLEAR = "database.clear";
    private final Dotalys2 owner;

    public AppState( Dotalys2 owner ) {
        properties = new Properties();
        properties.put( DATABASE_SERVER, "" );
        properties.put( DATABASE_USER, "" );
        properties.put( DATABASE_PASSWORD, "" );
        properties.put( DATABASE, "" );
        properties.put( DATABASE_CLEAR, "" );
        this.owner = owner;
    }

    private void checkIfMaySave() {
        boolean maySave = !getDatabase().isEmpty();
        maySave &= !getServer().isEmpty();
        maySave &= !getUser().isEmpty();

        owner.enableSave( maySave );

    }

    public String getDatabase() {
        return properties.getProperty( DATABASE );
    }

    public String getPassword() {
        return properties.getProperty( DATABASE_PASSWORD );
    }

    public Properties getProperties() {
        return properties;
    }

    public Replay getReplay() {
        return replay;
    }

    public String getServer() {
        return properties.getProperty( DATABASE_SERVER );
    }

    public String getUser() {
        return properties.getProperty( DATABASE_USER );
    }

    public void init( Properties p ) {
        properties = p;
        checkIfMaySave();
    }

    public boolean isClearDatabase() {
        return Boolean.valueOf( properties.getProperty( DATABASE_CLEAR ) );
    }

    public void setClearDatabase( boolean clearDatabase ) {
        properties.setProperty( DATABASE_CLEAR, Boolean.toString( clearDatabase ) );
    }

    public void setDatabase( String database ) {
        properties.setProperty( DATABASE, database );

        checkIfMaySave();
    }

    public void setPassword( String password ) {
        properties.setProperty( DATABASE_PASSWORD, password );

//        checkIfMaySave(); //Might be password free
    }

    public void setReplay( Replay replay ) {
        this.replay = replay;

        checkIfMaySave();
    }

    public void setServer( String server ) {
        properties.setProperty( DATABASE_SERVER, server );

        checkIfMaySave();
    }

    public void setUser( String user ) {
        properties.setProperty( DATABASE_USER, user );
        checkIfMaySave();
    }

}
