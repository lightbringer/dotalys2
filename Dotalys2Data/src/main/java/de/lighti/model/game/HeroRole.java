package de.lighti.model.game;

/**
 * Dictionary of the official role annotations. Collected from http://dota2.gamepedia.com
 * and http://wiki.teamliquid.net on 13.04.2015
 * @author Tobias Mahlmann
 *
 */
public enum HeroRole {
    CARRY( 1 << 0 ), DISABLER( 1 << 1 ), LANE_SUPPORT( 1 << 2 ), INITIATOR( 1 << 3 ), JUNGLER( 1 << 4 ), SUPPORT( 1 << 5 ), DURABLE( 1 << 6 ), NUKER(
                    1 << 7 ), PUSHER( 1 << 8 ), ESCAPE( 1 << 9 );
    public static String[] asString( int mask ) {
        final String[] ret = new String[Integer.bitCount( mask )];
        int i = 0;
        for (final HeroRole r : values()) {
            if ((r.id & mask) > 0) {
                ret[i++] = r.name();
            }
        }

        return ret;
    }

    public static int rolesForHero( Hero h ) {
        return rolesForHero( h.getKey() );
    }

    public static int rolesForHero( String key ) {
        switch (key.toUpperCase()) {
            case "CDOTA_UNIT_HERO_ABADDON":
                return CARRY.id | SUPPORT.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_ALCHEMIST":
                return CARRY.id | DISABLER.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_ANCIENTAPPARITION":
                return DISABLER.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_ANTIMAGE":
                return CARRY.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_AXE":
                return DISABLER.id | INITIATOR.id | JUNGLER.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_BANE":
                return DISABLER.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_BATRIDER":
                return DISABLER.id | INITIATOR.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_BEASTMASTER":
                return DISABLER.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_BLOODSEEKER":
                return CARRY.id | JUNGLER.id;
            case "CDOTA_UNIT_HERO_BOUNTYHUNTER":
                return CARRY.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_BREWMASTER":
                return CARRY.id | INITIATOR.id | DURABLE.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_BRISTLEBACK":
                return DISABLER.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_BROODMOTHER":
                return CARRY.id | PUSHER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_CENTAUR":
                return DISABLER.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_CHAOSKNIGHT":
                return CARRY.id | DISABLER.id | DURABLE.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_CHEN":
                return JUNGLER.id | SUPPORT.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_CLINKZ":
                return CARRY.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_CRYSTALMAIDEN":
                return DISABLER.id | LANE_SUPPORT.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_DARKSEER":
                return INITIATOR.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_DAZZLE":
                return LANE_SUPPORT.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_DEATHPROPHET":
                return DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_DISRUPTOR":
                return DISABLER.id | INITIATOR.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_DOOMBRINGER":
                return CARRY.id | DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_DRAGONKNIGHT":
                return CARRY.id | DISABLER.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_DROWRANGER":
                return CARRY.id;
            case "CDOTA_UNIT_HERO_EARTHSHAKER":
                return DISABLER.id | LANE_SUPPORT.id | INITIATOR.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_EARTHSPIRIT":
                return CARRY.id | DISABLER.id | DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_ELDER_TITAN":
                return INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_EMBERSPIRIT":
                return CARRY.id | NUKER.id;
            case "CDOTA_UNIT_HERO_ENCHANTRESS":
                return JUNGLER.id | SUPPORT.id | DURABLE.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_ENIGMA":
                return DISABLER.id | INITIATOR.id | JUNGLER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_FACELESSVOID":
                return CARRY.id | DISABLER.id | INITIATOR.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_FURION": //Nature's Prophet
                return CARRY.id | JUNGLER.id | PUSHER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_GYROCOPTER":
                return DISABLER.id | INITIATOR.id | NUKER.id;
            case "CDOTA_UNIT_HERO_HUSKAR":
                return CARRY.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_INVOKER":
                return CARRY.id | INITIATOR.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_JAKIRO":
                return DISABLER.id | LANE_SUPPORT.id | NUKER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_JUGGERNAUT":
                return CARRY.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_KEEPEROFTHELIGHT":
                return LANE_SUPPORT.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_KUNKKA":
                return CARRY.id | DISABLER.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_LEGION_COMMANDER":
                return CARRY.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_LESHRAC":
                return DISABLER.id | SUPPORT.id | NUKER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_LICH":
                return LANE_SUPPORT.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_LIFE_STEALER":
                return CARRY.id | JUNGLER.id | DURABLE.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_LINA":
                return DISABLER.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_LION":
                return DISABLER.id | LANE_SUPPORT.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_LONEDRUID":
                return CARRY.id | JUNGLER.id | DURABLE.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_LUNA":
                return CARRY.id | NUKER.id;
            case "CDOTA_UNIT_HERO_LYCAN":
                return CARRY.id | JUNGLER.id | DURABLE.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_MAGNATAUR":
                return CARRY.id | DISABLER.id | INITIATOR.id | NUKER.id;
            case "CDOTA_UNIT_HERO_MEDUSA":
                return CARRY.id;
            case "CDOTA_UNIT_HERO_MEEPO":
                return CARRY.id | DISABLER.id | INITIATOR.id;
            case "CDOTA_UNIT_HERO_MIRANA":
                return CARRY.id | DISABLER.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_MORPHLING":
                return CARRY.id | INITIATOR.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_NAGA_SIREN":
                return CARRY.id | DISABLER.id | PUSHER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_NECROLYTE":
                return CARRY.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_NEVERMORE": //Shadow fiend
                return CARRY.id | NUKER.id;
            case "CDOTA_UNIT_HERO_NIGHTSTALKER":
                return INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_NYX_ASSASSIN":
                return DISABLER.id | NUKER.id;
            case "CDOTA_UNIT_HERO_OBSIDIAN_DESTROYER": //Outworld
                return CARRY.id;
            case "CDOTA_UNIT_HERO_OGRE_MAGI":
                return DISABLER.id | DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_OMNIKNIGHT":
                return LANE_SUPPORT.id | SUPPORT.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_ORACLE":
                return LANE_SUPPORT.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_PHANTOMASSASSIN":
                return CARRY.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_PHANTOMLANCER":
                return CARRY.id | PUSHER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_PHOENIX":
                return DISABLER.id | INITIATOR.id | NUKER.id;
            case "CDOTA_UNIT_HERO_PUCK":
                return DISABLER.id | INITIATOR.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_PUDGE":
                return DISABLER.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_PUGNA":
                return SUPPORT.id | NUKER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_QUEENOFPAIN":
                return CARRY.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_RATTLETRAP": //Clockwerk
                return INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_RAZOR":
                return CARRY.id | DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_RIKI":
                return CARRY.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_RUBICK":
                return DISABLER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_SANDKING":
                return DISABLER.id | INITIATOR.id | NUKER.id;
            case "CDOTA_UNIT_HERO_SHADOW_DEMON":
                return DISABLER.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_SHADOWSHAMAN":
                return DISABLER.id | SUPPORT.id | NUKER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_SHREDDER": //Timbersaw
                return DURABLE.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_SILENCER":
                return CARRY.id | INITIATOR.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_SKELETONKING":
                return CARRY.id | DISABLER.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_SKYWRATH_MAGE":
                return SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_SLARDAR":
                return CARRY.id | DISABLER.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_SLARK":
                return ESCAPE.id;
            case "CDOTA_UNIT_HERO_SNIPER":
                return CARRY.id;
            case "CDOTA_UNIT_HERO_SPECTRE":
                return CARRY.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_SPIRITBREAKER":
                return CARRY.id | DISABLER.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_STORMSPIRIT":
                return CARRY.id | DISABLER.id | INITIATOR.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_SVEN":
                return CARRY.id | INITIATOR.id | SUPPORT.id | DISABLER.id;
            case "CDOTA_UNIT_HERO_TECHIES":
                return NUKER.id | SUPPORT.id | DISABLER.id;
            case "CDOTA_UNIT_HERO_TEMPLARASSASSIN":
                return CARRY.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_TERRORBLADE":
                return CARRY.id;
            case "CDOTA_UNIT_HERO_TIDEHUNTER":
                return DISABLER.id | INITIATOR.id | SUPPORT.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_TINKER":
                return NUKER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_TINY":
                return DISABLER.id | INITIATOR.id | DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_TREANT":
                return DISABLER.id | LANE_SUPPORT.id | INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_TROLLWARLORD":
                return CARRY.id;
            case "CDOTA_UNIT_HERO_TUSK":
                return INITIATOR.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_UNDYING":
                return DISABLER.id | INITIATOR.id | DURABLE.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_URSA":
                return CARRY.id | JUNGLER.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_VENGEFULSPIRIT":
                return DISABLER.id | LANE_SUPPORT.id | INITIATOR.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_VENOMANCER":
                return INITIATOR.id | SUPPORT.id | NUKER.id | PUSHER.id;
            case "CDOTA_UNIT_HERO_VIPER":
                return CARRY.id | DURABLE.id;
            case "CDOTA_UNIT_HERO_VISAGE":
                return DISABLER.id | DURABLE.id | NUKER.id;
            case "CDOTA_UNIT_HERO_WARLOCK":
                return DISABLER.id | LANE_SUPPORT.id | INITIATOR.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_WEAVER":
                return CARRY.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_WINDRUNNER":
                return DISABLER.id | SUPPORT.id | NUKER.id | ESCAPE.id;
            case "CDOTA_UNIT_HERO_WINTERWYVERN":
                return SUPPORT.id | DISABLER.id;
            case "CDOTA_UNIT_HERO_WISP": //IO
                return LANE_SUPPORT.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_WITCHDOCTOR":
                return DISABLER.id | SUPPORT.id;
            case "CDOTA_UNIT_HERO_WINTER_WYVERN":
                return DISABLER.id | SUPPORT.id | NUKER.id;
            case "CDOTA_UNIT_HERO_ZUUS":
                return SUPPORT.id | NUKER.id;
            default:
                return 0;
        }
    }

    public static double[] rolesForHeroArray( Hero h ) {
        final int rolNum = HeroRole.values().length;
        final double[] result = new double[rolNum];
        double sum = 0;
        int i = 0;
        final int heroRole = rolesForHero( h );
        for (final HeroRole r : values()) {
            if ((r.id & heroRole) > 0) {
                result[i] += 1.0;
                sum++;
            }
            i++;
        }
        for (int x = 0; x < HeroRole.values().length; x++) {
            result[x] /= sum;
        }
        return result;
    }

    public final int id;

    private HeroRole( int id ) {
        this.id = id;
    }
}
