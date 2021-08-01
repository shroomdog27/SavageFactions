package com.massivecraft.factions;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import com.massivecraft.factions.util.Particles.ParticleEffect;
import com.massivecraft.factions.util.Particles.Particles;
import com.massivecraft.factions.zcore.ffly.FlyParticle;
import com.massivecraft.factions.zcore.ffly.flyparticledata.ColorableCloud;
import com.massivecraft.factions.zcore.ffly.flyparticledata.FlyParticleData;
import com.massivecraft.factions.zcore.fperms.DefaultPermissions;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.persist.serializable.ConfigurableGuiItem;
import com.massivecraft.factions.zcore.persist.serializable.ConfigurableItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class Conf {

    // Region Style
    public static final transient String DYNMAP_STYLE_LINE_COLOR = "#00FF00";
    public static final transient double DYNMAP_STYLE_LINE_OPACITY = 0.8D;
    public static final transient int DYNMAP_STYLE_LINE_WEIGHT = 3;
    public static final transient String DYNMAP_STYLE_FILL_COLOR = "#00FF00";
    public static final transient double DYNMAP_STYLE_FILL_OPACITY = 0.35D;
    public static final transient String DYNMAP_STYLE_HOME_MARKER = "greenflag";
    public static final transient boolean DYNMAP_STYLE_BOOST = false;
    public static List<String> baseCommandAliases = new ArrayList<>();
    public static boolean allowNoSlashCommand = true;
    public static Set<String> allowedStealthFactions = new LinkedHashSet<>();

    // Colors
    public static ChatColor colorMember = ChatColor.GREEN;
    public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
    public static ChatColor colorTruce = ChatColor.DARK_PURPLE;
    public static ChatColor colorNeutral = ChatColor.WHITE;
    public static ChatColor colorEnemy = ChatColor.RED;
    public static ChatColor colorPeaceful = ChatColor.GOLD;
    public static ChatColor colorWilderness = ChatColor.GRAY;
    public static ChatColor colorSafezone = ChatColor.GOLD;
    public static ChatColor colorWar = ChatColor.DARK_RED;
    // Power
    public static double powerPlayerMax = 10.0;
    public static double powerPlayerMin = -10.0;
    public static double powerPlayerStarting = 0.0;
    public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
    public static double powerPerDeath = 4.0; // A death makes you lose 4 power
    public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
    public static double powerOfflineLossPerDay = 0.0;  // players will lose this much power per day offline
    public static double powerOfflineLossLimit = 0.0;  // players will no longer lose power from being offline once their power drops to this amount or less
    public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)
    public static String prefixLeader = "***";
    public static String prefixCoLeader = "**";
    public static String prefixMod = "*";
    public static String prefixRecruit = "-";
    public static String prefixNormal = "+";
    public static int factionTagLengthMin = 3;
    public static int factionTagLengthMax = 10;
    public static boolean factionTagForceUpperCase = false;
    public static boolean newFactionsDefaultOpen = false;
    // when faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit
    public static int factionMemberLimit = 0;
    public static int factionAltMemberLimit = 0;
    // what faction ID to start new players in when they first join the server; default is 0, "no faction"
    public static String newPlayerStartingFactionID = "0";
    public static boolean showMapFactionKey = true;
    public static boolean showNeutralFactionsOnMap = true;
    public static boolean showEnemyFactionsOnMap = true;
    public static boolean showTrucesFactionsOnMap = true;
    // Disallow joining/leaving/kicking while power is negative
    public static boolean canLeaveWithNegativePower = true;
    // Configuration for faction-only chat
    public static boolean factionOnlyChat = true;
    // Configuration on the Faction tag in chat messages.
    public static boolean chatTagEnabled = true;
    public static transient boolean chatTagHandledByAnotherPlugin = false;
    public static boolean chatTagRelationColored = true;
    public static String chatTagReplaceString = "[FACTION]";
    public static String chatTagReplaceTitleString = "[FACTION_TITLE]";
    public static String chatTagFormat = "%s" + ChatColor.WHITE;
    public static String factionChatFormat = "%s:" + ChatColor.WHITE + " %s";
    public static String allianceChatFormat = ChatColor.LIGHT_PURPLE + "%s:" + ChatColor.WHITE + " %s";
    public static String truceChatFormat = ChatColor.DARK_PURPLE + "%s:" + ChatColor.WHITE + " %s";
    public static String modChatFormat = ChatColor.RED + "%s:" + ChatColor.WHITE + " %s";
    public static int stealthFlyCheckRadius = 32;
    public static boolean noEnderpearlsInFly = false;
    public static boolean broadcastDescriptionChanges = false;
    public static boolean broadcastTagChanges = false;
    public static double saveToFileEveryXMinutes = 30.0;
    public static double autoLeaveAfterDaysOfInactivity = 10.0;
    public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
    public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;  // 1 server tick is roughly 50ms, so default max 10% of a tick
    public static boolean removePlayerDataWhenBanned = true;
    public static boolean autoLeaveDeleteFPlayerData = true; // Let them just remove player from Faction.
    public static boolean worldGuardChecking = false;
    public static boolean worldGuardBuildPriority = false;
    // server logging options
    public static boolean logFactionCreate = true;
    public static boolean logFactionDisband = true;
    public static boolean logFactionJoin = true;
    public static boolean logFactionKick = true;
    public static boolean logFactionLeave = true;
    public static boolean logLandClaims = true;
    public static boolean logLandUnclaims = true;
    public static boolean logMoneyTransactions = true;
    public static boolean logPlayerCommands = true;
    // prevent some potential exploits
    public static boolean handleExploitObsidianGenerators = true;
    public static boolean handleExploitEnderPearlClipping = true;
    public static boolean handleExploitInteractionSpam = true;
    public static boolean handleExploitTNTWaterlog = false;
    public static boolean handleExploitLiquidFlow = false;
    public static boolean homesEnabled = true;
    public static boolean homesMustBeInClaimedTerritory = true;
    public static boolean homesTeleportToOnDeath = true;
    public static boolean homesRespawnFromNoPowerLossWorlds = true;
    public static boolean homesTeleportCommandEnabled = true;
    public static boolean homesTeleportCommandEssentialsIntegration = true;
    public static boolean homesTeleportCommandSmokeEffectEnabled = true;
    public static float homesTeleportCommandSmokeEffectThickness = 3f;
    public static boolean homesTeleportAllowedFromEnemyTerritory = true;
    public static boolean homesTeleportAllowedFromDifferentWorld = true;
    public static double homesTeleportAllowedEnemyDistance = 32.0;
    public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
    public static boolean homesTeleportIgnoreEnemiesIfInNoClaimingWorld = true;
    public static boolean disablePVPBetweenNeutralFactions = false;
    public static boolean disablePVPForFactionlessPlayers = false;
    public static boolean enablePVPAgainstFactionlessInAttackersLand = false;
    public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;
    public static boolean peacefulTerritoryDisablePVP = true;
    public static boolean peacefulTerritoryDisableMonsters = false;
    public static boolean peacefulTerritoryDisableBoom = false;
    public static boolean peacefulMembersDisablePowerLoss = true;
    public static boolean permanentFactionsDisableLeaderPromotion = false;
    public static boolean claimsMustBeConnected = false;
    public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
    public static int claimsRequireMinFactionMembers = 1;
    public static int claimedLandsMax = 0;
    public static int lineClaimLimit = 5;
    public static int relationWishCooldownSeconds = 5;
    // if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
    public static int radiusClaimFailureLimit = 9;
    public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;
    public static int actionDeniedPainAmount = 1;
    // commands which will be prevented if the player is a member of a permanent faction
    public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<>();
    // commands which will be prevented when in claimed territory of another faction
    public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<>();
    public static Set<String> territoryEnemyDenyCommands = new LinkedHashSet<>();
    public static Set<String> territoryAllyDenyCommands = new LinkedHashSet<>();
    public static Set<String> warzoneDenyCommands = new LinkedHashSet<>();
    public static Set<String> wildernessDenyCommands = new LinkedHashSet<>();
    public static boolean territoryDenyBuild = true;
    public static boolean territoryDenyBuildWhenOffline = true;
    public static boolean territoryPainBuild = false;
    public static boolean territoryPainBuildWhenOffline = false;
    public static boolean territoryDenyUseage = true;
    public static boolean territoryEnemyDenyBuild = true;
    public static boolean territoryEnemyDenyBuildWhenOffline = true;
    public static boolean territoryEnemyPainBuild = false;
    public static boolean territoryEnemyPainBuildWhenOffline = false;
    public static boolean territoryEnemyDenyUseage = true;

    public static boolean territoryAllyDenyBuild = true;
    public static boolean territoryAllyDenyBuildWhenOffline = true;
    public static boolean territoryAllyPainBuild = false;
    public static boolean territoryAllyPainBuildWhenOffline = false;
    public static boolean territoryAllyDenyUseage = true;

    public static boolean territoryTruceDenyBuild = true;
    public static boolean territoryTruceDenyBuildWhenOffline = true;
    public static boolean territoryTrucePainBuild = false;
    public static boolean territoryTrucePainBuildWhenOffline = false;
    public static boolean territoryTruceDenyUseage = true;

    public static boolean territoryBlockCreepers = false;
    public static boolean territoryBlockCreepersWhenOffline = false;
    public static boolean territoryBlockFireballs = false;
    public static boolean territoryBlockFireballsWhenOffline = false;
    public static boolean territoryBlockTNT = false;
    public static boolean territoryBlockTNTWhenOffline = false;
    public static boolean territoryBlockTNTMinecartIgnition = false;
    public static boolean territoryDenyEndermanBlocks = true;
    public static boolean territoryDenyEndermanBlocksWhenOffline = true;
    public static boolean safeZoneDenyBuild = true;
    public static boolean safeZoneDenyUseage = true;
    public static boolean safeZoneBlockTNT = true;
    public static boolean safeZonePreventAllDamageToPlayers = false;
    public static boolean safeZoneDenyEndermanBlocks = true;
    public static boolean safeZoneTerritoryDisablePVP = true;
    public static boolean warZoneDenyBuild = true;
    public static boolean warZoneDenyUseage = true;
    public static boolean warZoneBlockCreepers = false;
    public static boolean warZoneBlockFireballs = false;
    public static boolean warZoneBlockTNT = true;
    public static boolean warZonePowerLoss = true;
    public static boolean warZoneFriendlyFire = false;
    public static boolean warZoneDenyEndermanBlocks = true;
    public static boolean wildernessDenyBuild = false;
    public static boolean wildernessDenyUseage = false;
    public static boolean wildernessBlockCreepers = false;
    public static boolean wildernessBlockFireballs = false;
    public static boolean wildernessBlockTNT = false;
    public static boolean wildernessPowerLoss = true;
    public static boolean wildernessDenyEndermanBlocks = false;
    // for claimed areas where further faction-member ownership can be defined
    public static boolean ownedAreasEnabled = true;
    public static int ownedAreasLimitPerFaction = 0;
    public static boolean ownedAreasModeratorsCanSet = false;
    public static boolean ownedAreaModeratorsBypass = true;
    public static boolean ownedAreaDenyBuild = true;
    public static boolean ownedAreaPainBuild = false;
    public static boolean ownedAreaProtectMaterials = true;
    public static boolean ownedAreaDenyUseage = true;
    public static boolean ownedMessageOnBorder = true;
    public static boolean ownedMessageInsideTerritory = true;
    public static boolean ownedMessageByChunk = false;
    public static boolean pistonProtectionThroughDenyBuild = true;
    public static Set<Material> territoryDenyUseageMaterials = new HashSet<>();
    public static Set<Material> territoryDenyUseageMaterialsWhenOffline = new HashSet<>();
    public static transient Set<EntityType> safeZoneNerfedCreatureTypes = new HashSet<>();
    public static boolean safeZoneNerfIgnorePluginSpawns = false;
    public static boolean sendFactionChangeMessage = true;
    /// <summary>
    /// This defines a set of materials which should always be allowed to use, regardless of factions permissions.
    /// Useful for HCF features.
    /// </summary>
    public static Set<Material> territoryBypasssProtectedMaterials = EnumSet.noneOf(Material.class);
    public static Set<Material> territoryCancelAndAllowItemUseMaterial = new HashSet<>();
    public static Set<Material> territoryDenySwitchMaterials = new HashSet<>();

    public static boolean allowCreeperEggingChests = true;
    public static String mineSpawnersAction = "mine spawners";
    public static boolean allowPlayersToMineOtherFactionsSpawnersWithPerm = false;

    // Economy settings
    public static boolean econEnabled = false;
    public static String econUniverseAccount = "";
    public static double econCostClaimWilderness = 30.0;
    public static double econCostClaimFromFactionBonus = 30.0;
    public static double econOverclaimRewardMultiplier = 0.0;
    public static double econClaimAdditionalMultiplier = 0.5;
    public static double econClaimRefundMultiplier = 0.7;
    public static double econClaimUnconnectedFee = 0.0;
    public static double econCostCreate = 100.0;
    public static double econCostOwner = 15.0;
    public static double econCostSethome = 30.0;
    public static double econCostJoin = 0.0;
    public static double econCostLeave = 0.0;
    public static double econCostKick = 0.0;
    public static double econCostInvite = 0.0;
    public static double econCostHome = 0.0;
    public static double econCostTag = 0.0;
    public static double econCostDesc = 0.0;
    public static double econCostTitle = 0.0;
    public static double econCostList = 0.0;
    public static double econCostMap = 0.0;
    public static double econCostPower = 0.0;
    public static double econCostShow = 0.0;
    public static double econFactionStartingBalance = 0.0;
    public static double econDenyWithdrawWhenMinutesAgeLessThan = 2880; // 2 days


    // -------------------------------------------- //
    // INTEGRATION: DYNMAP
    // -------------------------------------------- //
    public static double econCostStuck = 0.0;
    public static double econCostOpen = 0.0;
    public static double econCostAlly = 0.0;
    public static double econCostTruce = 0.0;
    public static double econCostEnemy = 0.0;
    public static double econCostNeutral = 0.0;
    public static double econCostNoBoom = 0.0;
    // Should the dynmap intagration be used?
    public static boolean dynmapUse = false;
    // Name of the Factions layer
    public static String dynmapLayerName = "Factions";
    // Should the layer be visible per default
    public static boolean dynmapLayerVisible = true;
    // Ordering priority in layer menu (low goes before high - default is 0)
    public static int dynmapLayerPriority = 2;
    // (optional) set minimum zoom level before layer is visible (0 = default, always visible)
    public static int dynmapLayerMinimumZoom = 0;
    // Format for popup - substitute values for macros
    public static String dynmapDescription =
            "<div class=\"infowindow\">\n"
                    + "<span style=\"font-weight: bold; font-size: 150%;\">%name%</span><br>\n"
                    + "<span style=\"font-style: italic; font-size: 110%;\">%description%</span><br>"
                    + "<br>\n"
                    + "<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n"
                    + "<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n"
                    + "</br>\n"
                    + "<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n"
                    + "<br>\n"
                    + "</div>";
    // Enable the %money% macro. Only do this if you know your economy manager is thread-safe.
    public static boolean dynmapDescriptionMoney = false;
    // Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)
    public static boolean dynmapVisibilityByFaction = true;
    // Optional setting to limit which regions to show.
    // If empty all regions are shown.
    // Specify Faction either by name or UUID.
    // To show all regions on a given world, add 'world:<worldname>' to the list.
    public static Set<String> dynmapVisibleFactions = new HashSet<>();
    // Optional setting to hide specific Factions.
    // Specify Faction either by name or UUID.
    // To hide all regions on a given world, add 'world:<worldname>' to the list.
    public static Set<String> dynmapHiddenFactions = new HashSet<>();
    public static DynmapStyle dynmapDefaultStyle = new DynmapStyle()
            .setStrokeColor(DYNMAP_STYLE_LINE_COLOR)
            .setLineOpacity(DYNMAP_STYLE_LINE_OPACITY)
            .setLineWeight(DYNMAP_STYLE_LINE_WEIGHT)
            .setFillColor(DYNMAP_STYLE_FILL_COLOR)
            .setFillOpacity(DYNMAP_STYLE_FILL_OPACITY)
            .setHomeMarker(DYNMAP_STYLE_HOME_MARKER)
            .setBoost(DYNMAP_STYLE_BOOST);

    // Optional per Faction style overrides. Any defined replace those in dynmapDefaultStyle.
    // Specify Faction either by name or UUID.
    public static Map<String, DynmapStyle> dynmapFactionStyles = ImmutableMap.of(
            "SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
            "WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
    );


    //Faction banks, to pay for land claiming and other costs instead of individuals paying for them
    public static boolean bankEnabled = true;
    public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
    public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
    public static boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.

    // mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
    public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<>();

    public static Set<String> worldsNoClaiming = new LinkedHashSet<>();
    public static Set<String> worldsNoPowerLoss = new LinkedHashSet<>();
    public static Set<String> worldsIgnorePvP = new LinkedHashSet<>();
    public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<>();


    // faction-<factionId>
    public static int defaultMaxVaults = 0;

    public static Backend backEnd = Backend.JSON;

    // Taller and wider for "bigger f map"
    public static int mapHeight = 17;
    public static int mapWidth = 49;
    //    public static String mapYouAreHereIcon = "\u271c";
    public static String mapYouAreHereIcon = "+";
    //    public static String mapFlatIcon = "\u2589";
    public static String mapFlatIcon = "-";

    //    public static char[] mapKeyChrs = "\u2726\u2734\u2735\u2777\u2778\u2779\u277a\u277b\u277c\u277d\u277e\u277f".toCharArray();
    public static char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();

    // Grace Period Settings
    public static boolean graceEnabled = false;

    public static boolean showFactionTerrtioryChangeMessage = true;

    //Strike settings.
    public static int maxStrikes = 10;


    // Fly Settings.
    public static boolean enableFlyParticles = true;
    public static List<FlyParticle> enabledParticleEffects = new ArrayList<>();
    public static Map<FlyParticle, FlyParticleData> particleEffectSettings = new EnumMap<>(FlyParticle.class);
    public static boolean disableFlightOnFactionClaimChange = true;
    public static boolean denyFlightIfInNoClaimingWorld = false;

    // Default Faction Permission Settings.
    public static boolean useLockedPermissions = false;
    public static boolean useCustomDefaultPermissions = true;
    public static boolean usePermissionHints = false;
    public static HashMap<String, DefaultPermissions> defaultFactionPermissions = new HashMap<>();
    public static HashSet<PermissableAction> lockedPermissions = new HashSet<>();

    private static transient Conf i = new Conf();
    public static String particleGUITitle = "&cChoose a Particle! &o(Patreon)";

    public static boolean enableFactionAlts = true;
    public static boolean registerAltsCommand = true;

    static {
        enabledParticleEffects.addAll(Arrays.asList(FlyParticle.values()));
    }

    // Custom Ranks - Oof I forgot I was doing this _SvenjaReissaus_
    //public static boolean enableCustomRanks = false; // We will disable it by default to avoid any migration error
    //public static int maxCustomRanks = 2; // Setting this to -1 will allow unlimited custom ranks
    // -------------------------------------------- //
    // Persistance
    // -------------------------------------------- //
    public static ConfigurableItem particleGUIBackgroundItem = new ConfigurableItem("&c ", Collections.emptyList(), XMaterial.GRAY_STAINED_GLASS_PANE, 1);

    public static boolean fchestEnabled = true;
    public static String fchestInventoryTitle = "&2&lFaction Chest";
    public static String fchestLogEntriesTitle = "&bThere are currently {entries} entries in the log, viewing page {currentpage} of {maxpage}";
    public static String fchestLogEntryFormat = "&3{entry#}. {time}, &3By: &b{user}, &3{amount}x, &b{item}";
    public static String fchestLogPageNavigationMessage = "Page Navigation   ";
    public static String fchestLogPageNavigationUp = "&a&l▲";
    public static String fchestLogPageNavigationDown = "&c&l▼";
    public static String fchestLogPreviousPageTooltip = "Go to previous page (Page {page})";
    public static String fchestLogNextPageTooltip = "Go to next page (Page {page})";


    public static int maxChestLogItems = 1000;

    public static int guiRows = 6;
    public static String memberGuiTitle = "&6MemberGUI";
    public static ConfigurableGuiItem memberGUIDummyItem = new ConfigurableGuiItem("&f ", Arrays.asList(""), XMaterial.BLACK_STAINED_GLASS_PANE, 1, -1);
    public static ConfigurableGuiItem memberGUIBackItem = new ConfigurableGuiItem("&cBack", Arrays.asList(""), XMaterial.ARROW, 1, 0);
    public static ConfigurableGuiItem memberGUINextItem = new ConfigurableGuiItem("&cNext", Arrays.asList(""), XMaterial.ARROW, 1, 8);
    public static String memberGUISkullDisplayName = "&c{name}";
    public static List<String> memberGUISkullLore = Arrays.asList("&cPower: {player-power}/{maxPower}", "&7left-click to kick", "&7right-click to promote");


    // Use this to not abuse the server's disk.
    //   public static int maxChestLogItems = 1000;

    static {

        lockedPermissions.add(PermissableAction.CHEST);

        particleEffectSettings.put(FlyParticle.WHITE_CLOUD, new ColorableCloud("White Cloud",
                new ConfigurableItem("White Cloud", Arrays.asList("&cClick me to show a white cloud"), XMaterial.WHITE_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(255, 255, 255)));
        particleEffectSettings.put(FlyParticle.ORANGE_CLOUD, new ColorableCloud("Orange Cloud",
                new ConfigurableItem("Orange Cloud", Arrays.asList("&cClick me to show a Orange cloud"), XMaterial.ORANGE_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(244, 158, 66)));
        particleEffectSettings.put(FlyParticle.GREEN_CLOUD, new ColorableCloud("Green Cloud",
                new ConfigurableItem("Green Cloud", Arrays.asList("&cClick me to show a Green cloud"), XMaterial.GREEN_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(65, 244, 89)));
        particleEffectSettings.put(FlyParticle.BLUE_CLOUD, new ColorableCloud("Blue Cloud",
                new ConfigurableItem("Blue Cloud", Arrays.asList("&cClick me to show a Blue cloud"), XMaterial.BLUE_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(87, 162, 242)));
        particleEffectSettings.put(FlyParticle.BLACK_CLOUD, (new ColorableCloud("Black Cloud",
                new ConfigurableItem("Black Cloud", Arrays.asList("&cClick me to show a Black cloud"), XMaterial.BLACK_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(0, 0, 0))));
        particleEffectSettings.put(FlyParticle.YELLOW_CLOUD, (new ColorableCloud("Yellow Cloud",
                new ConfigurableItem("Yellow Cloud", Arrays.asList("&cClick me to show a Yellow cloud"), XMaterial.YELLOW_WOOL, 1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(234, 237, 66))));
        particleEffectSettings.put(FlyParticle.PURPLE_CLOUD, (new ColorableCloud("Purple Cloud",
                new ConfigurableItem("Purple Cloud", Arrays.asList("&cClick me to show a Purple cloud"), XMaterial.PURPLE_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(218, 99, 255))));
        particleEffectSettings.put(FlyParticle.PINK_CLOUD, (new ColorableCloud("Pink Cloud",
                new ConfigurableItem("Pink Cloud", Arrays.asList("&cClick me to show a Pink cloud"), XMaterial.PINK_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(255, 99, 190))));
        particleEffectSettings.put(FlyParticle.RED_CLOUD, (new ColorableCloud("Red Cloud",
                new ConfigurableItem("Red Cloud", Arrays.asList("&cClick me to show a Red cloud"), XMaterial.RED_WOOL,
                        1), Particles.REDSTONE, new ParticleEffect.OrdinaryColor(255, 99, 190))));
    }


    static {
        baseCommandAliases.add("f");

        territoryEnemyDenyCommands.add("home");
        territoryEnemyDenyCommands.add("sethome");
        territoryEnemyDenyCommands.add("spawn");
        territoryEnemyDenyCommands.add("tpahere");
        territoryEnemyDenyCommands.add("tpaccept");
        territoryEnemyDenyCommands.add("tpa");

        territoryDenySwitchMaterials.add(XMaterial.ACACIA_FENCE_GATE.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BIRCH_FENCE_GATE.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DARK_OAK_FENCE_GATE.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.JUNGLE_FENCE_GATE.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.OAK_FENCE_GATE.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.SPRUCE_FENCE_GATE.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.ACACIA_DOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BIRCH_DOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DARK_OAK_DOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.JUNGLE_DOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.OAK_DOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.SPRUCE_DOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DISPENSER.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.CHEST.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.TRAPPED_CHEST.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.ACACIA_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BIRCH_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DARK_OAK_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.JUNGLE_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.OAK_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.SPRUCE_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DROPPER.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.HOPPER.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.ITEM_FRAME.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.ACACIA_TRAPDOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BIRCH_TRAPDOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DARK_OAK_TRAPDOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.JUNGLE_TRAPDOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.OAK_TRAPDOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.SPRUCE_TRAPDOOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.LEVER.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.COMPARATOR.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.REPEATER.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.ACACIA_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BIRCH_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.DARK_OAK_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.JUNGLE_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.OAK_BUTTON.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.SPRUCE_BUTTON.parseMaterial());

        territoryDenySwitchMaterials.add(XMaterial.PURPLE_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.WHITE_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.MAGENTA_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.LIGHT_BLUE_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.CYAN_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BLUE_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BROWN_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.ORANGE_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.GREEN_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.RED_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.BLACK_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.GRAY_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.LIME_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.LIGHT_GRAY_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.PINK_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.YELLOW_SHULKER_BOX.parseMaterial());
        territoryDenySwitchMaterials.add(XMaterial.SHULKER_BOX.parseMaterial());

        // 1.14 Barrel is a container.
        territoryDenySwitchMaterials.add(XMaterial.BARREL.parseMaterial());

        territoryCancelAndAllowItemUseMaterial.add(XMaterial.GOLDEN_APPLE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.APPLE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.ENCHANTED_GOLDEN_APPLE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_BEEF.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_MUTTON.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_CHICKEN.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_COD.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_PORKCHOP.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_RABBIT.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.COOKED_SALMON.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.ENDER_PEARL.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.POTION.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.SPLASH_POTION.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.CREEPER_SPAWN_EGG.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.BOW.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.DIAMOND_HELMET.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.DIAMOND_CHESTPLATE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.DIAMOND_LEGGINGS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.DIAMOND_BOOTS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.IRON_HELMET.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.IRON_CHESTPLATE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.IRON_LEGGINGS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.IRON_BOOTS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.LEATHER_HELMET.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.LEATHER_CHESTPLATE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.LEATHER_LEGGINGS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.LEATHER_BOOTS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.CHAINMAIL_HELMET.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.CHAINMAIL_LEGGINGS.parseMaterial());
        territoryCancelAndAllowItemUseMaterial.add(XMaterial.CHAINMAIL_BOOTS.parseMaterial());

        territoryDenyUseageMaterials.add(XMaterial.FIRE_CHARGE.parseMaterial());
        territoryDenyUseageMaterials.add(XMaterial.FLINT_AND_STEEL.parseMaterial());
        territoryDenyUseageMaterials.add(XMaterial.BUCKET.parseMaterial());
        territoryDenyUseageMaterials.add(XMaterial.WATER_BUCKET.parseMaterial());
        territoryDenyUseageMaterials.add(XMaterial.LAVA_BUCKET.parseMaterial());
        territoryDenyUseageMaterials.add(XMaterial.ARMOR_STAND.parseMaterial());

        territoryDenyUseageMaterialsWhenOffline.add(XMaterial.FIRE_CHARGE.parseMaterial());
        territoryDenyUseageMaterialsWhenOffline.add(XMaterial.FLINT_AND_STEEL.parseMaterial());
        territoryDenyUseageMaterialsWhenOffline.add(XMaterial.BUCKET.parseMaterial());
        territoryDenyUseageMaterialsWhenOffline.add(XMaterial.WATER_BUCKET.parseMaterial());
        territoryDenyUseageMaterialsWhenOffline.add(XMaterial.LAVA_BUCKET.parseMaterial());
        territoryDenyUseageMaterialsWhenOffline.add(XMaterial.ARMOR_STAND.parseMaterial());

        safeZoneNerfedCreatureTypes.add(EntityType.BLAZE);
        safeZoneNerfedCreatureTypes.add(EntityType.CAVE_SPIDER);
        safeZoneNerfedCreatureTypes.add(EntityType.CREEPER);
        safeZoneNerfedCreatureTypes.add(EntityType.ENDER_DRAGON);
        safeZoneNerfedCreatureTypes.add(EntityType.ENDERMAN);
        safeZoneNerfedCreatureTypes.add(EntityType.GHAST);
        safeZoneNerfedCreatureTypes.add(EntityType.MAGMA_CUBE);
        safeZoneNerfedCreatureTypes.add(EntityType.PIGLIN);
        safeZoneNerfedCreatureTypes.add(EntityType.PIGLIN_BRUTE);
        safeZoneNerfedCreatureTypes.add(EntityType.SILVERFISH);
        safeZoneNerfedCreatureTypes.add(EntityType.SKELETON);
        safeZoneNerfedCreatureTypes.add(EntityType.SPIDER);
        safeZoneNerfedCreatureTypes.add(EntityType.SLIME);
        safeZoneNerfedCreatureTypes.add(EntityType.WITCH);
        safeZoneNerfedCreatureTypes.add(EntityType.WITHER);
        safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIE);

        // Is this called lazy load?
        defaultFactionPermissions.put("COLEADER", new DefaultPermissions(true));
        defaultFactionPermissions.put("MODERATOR", new DefaultPermissions(true));
        defaultFactionPermissions.put("NORMAL MEMBER", new DefaultPermissions(false));
        defaultFactionPermissions.put("RECRUIT", new DefaultPermissions(false));
        defaultFactionPermissions.put("OWNERCLAIM", new DefaultPermissions(false));
        defaultFactionPermissions.put("ALLY", new DefaultPermissions(false));
        defaultFactionPermissions.put("ENEMY", new DefaultPermissions(false));
        defaultFactionPermissions.put("TRUCE", new DefaultPermissions(false));
        defaultFactionPermissions.put("NEUTRAL", new DefaultPermissions(false));

    }


    public static void load() {
        SavageFactions.plugin.persist.loadOrSaveDefault(i, Conf.class, "conf");
    }

    public static void save() {
        SavageFactions.plugin.persist.save(i);
    }

    public static void saveSync() {
        SavageFactions.plugin.persist.saveSync(i);
    }

    public enum Backend {
        JSON,
        //MYSQL,  TODO add MySQL storage
        ;
    }
}

