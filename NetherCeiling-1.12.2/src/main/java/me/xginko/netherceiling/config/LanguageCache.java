package me.xginko.netherceiling.config;

import me.xginko.netherceiling.NetherCeiling;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageCache {
    private final FileConfiguration fileConfiguration;
    boolean addedMissing = false;
    public String noPermission, youre_not_on_the_ceiling, general_cant_be_on_ceiling, general_cant_move_on_ceiling, general_cant_tp_to_ceiling,
            portals_cant_create_on_ceiling, portals_cant_use_on_ceiling, portals_cant_use_to_ceiling,
            building_disabled_on_ceiling, building_block_cant_be_placed, building_block_limit_reached, building_build_height_is_at,
            building_bed_respawn_set, vehicles_cant_ride_this_on_ceiling, potions_effect_removed, potions_effect_nerfed,
            fastblocks_moving_on_block_is_limited;

    public LanguageCache(String lang) {
        NetherCeiling plugin = NetherCeiling.getInstance();
        File langFile = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        }
        try {
            fileConfiguration.load(langFile);

            // No Permission
            this.noPermission = getStringTranslation("no-permission", "&cYou don't have permission to use this command.");
            // Not on the ceiling
            this.youre_not_on_the_ceiling = getStringTranslation("not-on-ceiling", "&cYou are not on the nether ceiling.");
            // General
            this.general_cant_be_on_ceiling = getStringTranslation("general.cant-be-on-ceiling", "&cAccess to the nether ceiling is disabled.");
            this.general_cant_move_on_ceiling = getStringTranslation("general.cant-move-on-ceiling", "&cYou can't move on the nether ceiling.");
            this.general_cant_tp_to_ceiling = getStringTranslation("general.cant-teleport-to-ceiling", "&cYou can't teleport to the nether ceiling.");
            // Portals
            this.portals_cant_create_on_ceiling = getStringTranslation("portals.cant-create-portals-on-ceiling", "&cPortals can't be created on the nether ceiling.");
            this.portals_cant_use_on_ceiling = getStringTranslation("portals.cant-use-portals-on-ceiling", "&cPortals can't be used on the nether ceiling.");
            this.portals_cant_use_to_ceiling = getStringTranslation("portals.cant-use-portals-to-ceiling", "&cPortals linked to the nether ceiling can't be used.");
            // Building
            this.building_disabled_on_ceiling  = getStringTranslation("building.disabled-on-ceiling", "&cBuilding is disabled on the nether ceiling.");
            this.building_block_cant_be_placed = getStringTranslation("building.block-cant-be-placed", "&c%block% can't be placed on the nether ceiling.");
            this.building_block_limit_reached = getStringTranslation("building.block-limit-reached", "&cYou cant place more than %amount% blocks of %block% per ceiling chunk.");
            this.building_build_height_is_at = getStringTranslation("building.build-height-is", "&cThe maximum build height is at Y%buildheight%.");
            this.building_bed_respawn_set = getStringTranslation("building.bed-respawn-set", "&fRespawnpoint set.");
            // Vehicles
            this.vehicles_cant_ride_this_on_ceiling = getStringTranslation("vehicles.cant-ride-on-ceiling", "&c%vehicle% can't be used on the nether ceiling.");
            // Potions
            this.potions_effect_removed = getStringTranslation("potions.one-or-more-effects-removed", "&cOne or more effects have been removed because you're on the nether ceiling.");
            this.potions_effect_nerfed = getStringTranslation("potions.one-or-more-effects-nerfed", "&cOne or more effects have been nerfed because you're on the nether ceiling.");
            // Fast Blocks
            this.fastblocks_moving_on_block_is_limited = getStringTranslation("fast-blocks.moving-on-block-is-limited", "&cMoving on %fastblock% is restricted on the nether ceiling.");

            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            NetherCeiling.getInstance().getLogger().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public String getStringTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }
}
