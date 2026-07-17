package com.duapar.devent.manager;

import com.duapar.devent.integration.AuctionLookup;
import com.duapar.devent.model.EventType;
import com.duapar.devent.model.ScheduledEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventManager {

    private final JavaPlugin plugin;
    private final Random random = new Random();
    private final List<ScheduledEvent> events = new ArrayList<>();
    private final List<BukkitTask> tasks = new ArrayList<>();

    public EventManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadEvents(FileConfiguration cfg, Logger logger) {
        events.clear();
        List<Map<?, ?>> list = cfg.getMapList("events");
        for (Map<?, ?> raw : list) {
            try {
                String id = String.valueOf(raw.get("id"));
                EventType type = EventType.valueOf(String.valueOf(raw.get("type"))
                        .toUpperCase(Locale.ROOT).replace('-', '_'));
                long interval = Long.parseLong(String.valueOf(raw.get("interval-minutes")));
                String announce = String.valueOf(raw.get("announce"));
                List<Material> lootPool = new ArrayList<>();
                Object rawPool = raw.get("loot-pool");
                if (rawPool instanceof List) {
                    for (Object entry : (List<?>) rawPool) {
                        Material material = Material.matchMaterial(String.valueOf(entry));
                        if (material != null) {
                            lootPool.add(material);
                        }
                    }
                }
                long priceMin = raw.containsKey("price-min") ? Long.parseLong(String.valueOf(raw.get("price-min"))) : 0;
                long priceMax = raw.containsKey("price-max") ? Long.parseLong(String.valueOf(raw.get("price-max"))) : priceMin;
                events.add(new ScheduledEvent(id, type, interval, announce, lootPool, priceMin, priceMax));
            } catch (RuntimeException ex) {
                logger.log(Level.WARNING, "Événement invalide ignoré dans config.yml: " + ex.getMessage());
            }
        }
    }

    public List<ScheduledEvent> listEvents() {
        return events;
    }

    public ScheduledEvent getEventOrThrow(String id) {
        for (ScheduledEvent event : events) {
            if (event.getId().equalsIgnoreCase(id)) {
                return event;
            }
        }
        throw new EventException("Événement introuvable: " + id);
    }

    /**
     * Planifie une tâche répétée par événement. Les minuteries repartent de
     * zéro à chaque (re)démarrage du plugin - rien n'est persisté (voir README).
     */
    public void scheduleAll() {
        cancelAll();
        for (ScheduledEvent event : events) {
            long periodTicks = Math.max(1, event.getIntervalMinutes()) * 20L * 60;
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> trigger(event), periodTicks, periodTicks);
            tasks.add(task);
        }
    }

    public void cancelAll() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
    }

    public void trigger(ScheduledEvent event) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', event.getAnnounce()));
        if (event.getType() != EventType.LOOT_DROP || event.getLootPool().isEmpty()) {
            return;
        }
        Material material = event.getLootPool().get(random.nextInt(event.getLootPool().size()));
        long span = Math.max(1, event.getPriceMax() - event.getPriceMin());
        long price = event.getPriceMin() + (long) (random.nextDouble() * span);
        boolean created = Bukkit.getPluginManager().isPluginEnabled("DAPI")
                && AuctionLookup.createListing(new ItemStack(material), price);
        if (!created) {
            plugin.getLogger().warning("Événement '" + event.getId() + "': DAuction indisponible, aucune annonce créée.");
        }
    }
}
