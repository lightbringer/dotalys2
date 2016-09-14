package de.lighti.model.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public enum Zone {
    UNKNOWN, LANE_BOTTOM_DIRE, JUNGLE_RADIANT, LANE_SHOP_DIRE, ROSHAN_PIT, VOID_DIRE, BASE_RADIANT, SECRET_SHOP_DIRE, LANE_TOP_RADIANT, LANE_TOP_DIRE, LANE_MID_RADIANT, LANE_MID_DIRE, SECRET_SHOP_RADIANT, LANE_SHOP_RADIANT, JUNGLE_DIRE, BASE_DIRE, VOID_RADIANT, RIVER, LANE_BOTTOM_RADIANT;
    static Zone fromString( String n ) {
        switch (n) {
            case "bottomlane dire":
                return LANE_BOTTOM_DIRE;
            case "jungle radiant":
                return JUNGLE_RADIANT;
            case "laneshop dire":
                return LANE_SHOP_DIRE;
            case "pit dire":
                return ROSHAN_PIT;
            case "void dire":
                return VOID_DIRE;
            case "base1 radiant":
                return BASE_RADIANT;
            case "secretshop dire":
                return SECRET_SHOP_DIRE;
            case "toplane radiant":
                return LANE_TOP_RADIANT;
            case "toplane dire":
                return LANE_TOP_DIRE;
            case "midlane radiant":
                return LANE_MID_RADIANT;
            case "midlane dire":
                return LANE_MID_DIRE;
            case "secretshop radiant":
                return SECRET_SHOP_RADIANT;
            case "laneshop radiant":
                return LANE_SHOP_RADIANT;
            case "jungle dire":
                return JUNGLE_DIRE;
            case "base2 dire":
                return BASE_DIRE;
            case "void radiant":
                return VOID_RADIANT;
            case "river":
                return RIVER;
            case "bottomlane radiant":
                return LANE_BOTTOM_RADIANT;
            default:
                throw new IllegalArgumentException( "zone label doesn't match" );
        }
    }

    static Zone[][] ZONE_DICTIONARY = new Zone[124][124];

    static {
        for (final Zone[] a : Zone.ZONE_DICTIONARY) {
            for (int i = 0; i < a.length; i++) {
                a[i] = Zone.UNKNOWN;
            }
        }
        try {
            final BufferedReader in = new BufferedReader( new InputStreamReader( Unit.class.getResourceAsStream( "Zones.csv" ) ) );
            String line = null;
            int counter = 0;

            while ((line = in.readLine()) != null) {
                if (counter == 0) {
                    counter++;
                    continue;
                }

                final StringTokenizer tokenizer = new StringTokenizer( line, "," );
                final int x = Integer.parseInt( tokenizer.nextToken() );
                final int y = Integer.parseInt( tokenizer.nextToken() );
                final String zonelabel = tokenizer.nextToken();
                Zone.ZONE_DICTIONARY[x][y] = Zone.fromString( zonelabel );

                counter++;
            }
            in.close();
        }
        catch (final IOException e) {
            throw new RuntimeException( "Error parsing the master zone data. File Missing?" );
        }
    }
}