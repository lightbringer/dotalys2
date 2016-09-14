package de.lighti.io.data;

import java.util.Properties;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.lighti.model.Replay;

public class DatabaseUtil {

    private static SessionFactory buildSessionFactory( Properties p ) {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            final Configuration conf = new Configuration();

            conf.configure();
            final Properties confProp = conf.getProperties();
            confProp.setProperty( "hibernate.connection.url",
                            String.format( "jdbc:mysql://%s/%s?rewriteBatchedStatements=true", p.getProperty( "database.server" ), p.getProperty( "database" ) ) );
            confProp.setProperty( "hibernate.connection.username", p.getProperty( "database.user" ) );
            confProp.setProperty( "hibernate.connection.password", p.getProperty( "database.password" ) );
            final boolean clearDatabase = Boolean.valueOf( p.getProperty( "database.clear" ) );
            if (clearDatabase) {
                confProp.setProperty( "hibernate.hbm2ddl.auto", "create" );
            }
            if (p.getProperty( "debug" ) != null) {
                confProp.setProperty( "hibernate.show_sql", "true" );
            }

            return conf.buildSessionFactory();
        }
        catch (final Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println( "Initial SessionFactory creation failed." + ex );
            throw new ExceptionInInitializerError( ex );
        }
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        sessionFactory = null;
    }

    private static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void init( Properties p ) {
        sessionFactory = buildSessionFactory( p );
    }

    public static void save( Replay r ) {
        if (getSessionFactory() == null) {
            throw new IllegalStateException( "call init first" );
        }
        try {
            Logger.getLogger( DatabaseUtil.class.getName() ).info( "Saving " + r.getName() );
            final Session s = getSessionFactory().openSession();
            s.beginTransaction();
            s.persist( r );
            s.flush();
            s.getTransaction().commit();
            s.close();
            Logger.getLogger( DatabaseUtil.class.getName() ).info( "Success" );

        }
        catch (final Exception e) {
            close();
            throw e;
        }
    }

    private static SessionFactory sessionFactory;

}