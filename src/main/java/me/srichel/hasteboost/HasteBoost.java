package me.srichel.hasteboost;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class HasteBoost extends JavaPlugin implements Listener {

    private Material inHand = Material.NETHERITE_PICKAXE;
    private boolean isMuted = true;
    private Logger log = Bukkit.getLogger();
    private FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " Version: " + pdfFile.getVersion() + " is now enabled!");
        saveDefaultConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("hb")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("mute")) {
                    this.isMuted = true;
                    sender.sendMessage("[Haste Boost] isMuted = " + this.isMuted);
                    return true;

                } else if (args[0].equalsIgnoreCase("unmute")) {
                    this.isMuted = false;
                    sender.sendMessage("[Haste Boost] isMuted = " + this.isMuted);
                    return true;

                } else if (args[0].equalsIgnoreCase("showconfig")) {
                    String str = "HASTE BOOST CURRENT CONFIG SETTINGS:\n";
                    str += "yLvl: " + config.getInt("minYLvl") + "\n";
                    str += "boostAmplifier: " + config.getInt("boostAmplifier") + "\n";
                    str += "netheritePicOnly: " + config.getBoolean("netheritePicOnly") + "\n";

                    sender.sendMessage(str);
                    log.info(str);
                    return true;

                } else if (args[0].equalsIgnoreCase("reload")) {
                    this.reloadConfig();
                    this.config = this.getConfig();
                    sender.sendMessage("[Haste Boost] Reloaded config.yml");
                    log.info("[Hate Boost] Reloated config.yml");
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onBeaconEffect(BeaconEffectEvent e) {
        Player p = e.getPlayer();
        boolean isBelowYLvl = p.getLocation().getY() < config.getInt("yLvl");
        boolean hasNetheriteInHand = p.getInventory().getItemInMainHand().getType() == this.inHand;
        boolean isInHaste = p.getPotionEffect(PotionEffectType.FAST_DIGGING) != null;
        if (config.getBoolean("netheritePicOnly")) {
            if (isInHaste && isBelowYLvl && hasNetheriteInHand) {
               applyEffect(e, p);
            }
        } else {
            if (isInHaste && isBelowYLvl) {
                applyEffect(e, p);
            }
        }
    }

    public void applyEffect(BeaconEffectEvent e, Player p) {
        e.setEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, config.getInt("boostAmplifier")));
        if (!this.isMuted) {
            log.info("Boosting Haste for: " + p.getName());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Hast Boost Stopped");

    }
}
