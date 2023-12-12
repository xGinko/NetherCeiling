package me.xginko.netherceiling.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.netherceiling.NetherCeiling;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.util.List;

public class LanguageCache {

    private final ConfigFile langFile;

    public final Component no_permission, youre_not_on_the_ceiling, general_cant_be_on_ceiling, general_cant_move_on_ceiling,
            general_cant_tp_to_ceiling, portals_cant_create_on_ceiling, portals_cant_use_on_ceiling, portals_cant_use_to_ceiling,
            building_disabled_on_ceiling, building_block_cant_be_placed, building_block_limit_reached, building_build_height_is_at,
            building_bed_respawn_set, vehicles_cant_ride_this_on_ceiling, potions_effect_removed, potions_effect_nerfed,
            fastblocks_moving_on_block_is_limited, teleport_commencing_in, teleport_dont_move, teleport_cancelled;

    public LanguageCache(String locale) throws Exception {
        NetherCeiling plugin = NetherCeiling.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", locale + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            NetherCeiling.getLog().severe("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugins resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang/" + locale + ".yml", false);
        // Finally load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        // No Permission
        this.no_permission = getTranslation("no-permission", "<red>You don't have permission to use this command.", false);
        // Not on the ceiling
        this.youre_not_on_the_ceiling = getTranslation("not-on-ceiling", "<red>You are not on the nether ceiling.", false);
        // General
        this.general_cant_be_on_ceiling = getTranslation("general.cant-be-on-ceiling", "<red>Access to the nether ceiling is disabled.", false);
        this.general_cant_move_on_ceiling = getTranslation("general.cant-move-on-ceiling", "<red>You can't move on the nether ceiling.", false);
        this.general_cant_tp_to_ceiling = getTranslation("general.cant-teleport-to-ceiling", "<red>You can't teleport to the nether ceiling.", false);
        // Portals
        this.portals_cant_create_on_ceiling = getTranslation("portals.cant-create-portals-on-ceiling", "<red>Portals can't be created on the nether ceiling.", false);
        this.portals_cant_use_on_ceiling = getTranslation("portals.cant-use-portals-on-ceiling", "<red>Portals can't be used on the nether ceiling.", false);
        this.portals_cant_use_to_ceiling = getTranslation("portals.cant-use-portals-to-ceiling", "<red>Portals linked to the nether ceiling can't be used.", false);
        // Building
        this.building_disabled_on_ceiling  = getTranslation("building.disabled-on-ceiling", "<red>Building is disabled on the nether ceiling.", false);
        this.building_block_cant_be_placed = getTranslation("building.block-cant-be-placed", "<red>%block% can't be placed on the nether ceiling.", false);
        this.building_block_limit_reached = getTranslation("building.block-limit-reached", "<red>You cant place more than %amount% blocks of %block% per ceiling chunk.", false);
        this.building_build_height_is_at = getTranslation("building.build-height-is", "<red>The maximum build height is at Y%buildheight%.", false);
        this.building_bed_respawn_set = getTranslation("building.bed-respawn-set", "<white>Respawnpoint set.", false);
        // Vehicles
        this.vehicles_cant_ride_this_on_ceiling = getTranslation("vehicles.cant-ride-on-ceiling", "<red>%vehicle% can't be used on the nether ceiling.", false);
        // Potions
        this.potions_effect_removed = getTranslation("potions.one-or-more-effects-removed", "<red>One or more effects have been removed because you're on the nether ceiling.", false);
        this.potions_effect_nerfed = getTranslation("potions.one-or-more-effects-nerfed", "<red>One or more effects have been nerfed because you're on the nether ceiling.", false);
        // Fast Blocks
        this.fastblocks_moving_on_block_is_limited = getTranslation("fast-blocks.moving-on-block-is-limited", "<red>Moving on %fastblock% is restricted on the nether ceiling.", false);
        // Teleport
        this.teleport_commencing_in = getTranslation("teleport.commencing-in", "<aqua>Teleport commencing in %seconds% seconds.", false);
        this.teleport_dont_move = getTranslation("teleport.dont-move", "<dark_aqua>Don't move.", false);
        this.teleport_cancelled = getTranslation("teleport.cancelled", "<dark_aqua>Teleport cancelled.", false);

        try {
            langFile.save();
        } catch (Exception e) {
            NetherCeiling.getLog().severe("Failed to save language file: "+ langFile.getFile().getName() +" - " + e.getLocalizedMessage());
        }
    }

    public Component getTranslation(String path, String defaultTranslation, boolean upperCase) {
        langFile.addDefault(path, defaultTranslation);
        return MiniMessage.miniMessage().deserialize(upperCase ? langFile.getString(path, defaultTranslation).toUpperCase() : langFile.getString(path, defaultTranslation));
    }

    public Component getTranslation(String path, String defaultTranslation, boolean upperCase, String comment) {
        langFile.addDefault(path, defaultTranslation, comment);
        return MiniMessage.miniMessage().deserialize(upperCase ? langFile.getString(path, defaultTranslation).toUpperCase() : langFile.getString(path, defaultTranslation));
    }

    public List<Component> getListTranslation(String path, List<String> defaultTranslation, boolean upperCase) {
        langFile.addDefault(path, defaultTranslation);
        return langFile.getStringList(path).stream().map(line -> MiniMessage.miniMessage().deserialize(upperCase ? line.toUpperCase() : line)).toList();
    }

    public List<Component> getListTranslation(String path, List<String> defaultTranslation, boolean upperCase, String comment) {
        langFile.addDefault(path, defaultTranslation, comment);
        return langFile.getStringList(path).stream().map(line -> MiniMessage.miniMessage().deserialize(upperCase ? line.toUpperCase() : line)).toList();
    }
}