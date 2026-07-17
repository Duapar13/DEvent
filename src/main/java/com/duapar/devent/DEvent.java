package com.duapar.devent;

import com.duapar.devent.commands.EventCommand;
import com.duapar.devent.integration.DAPIHook;
import com.duapar.devent.manager.EventManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class DEvent extends JavaPlugin {

    private EventManager eventManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        eventManager = new EventManager(this);
        eventManager.loadEvents(getConfig(), getLogger());
        eventManager.scheduleAll();

        EventCommand eventCommand = new EventCommand(eventManager);
        PluginCommand event = getCommand("event");
        if (event != null) {
            event.setExecutor(eventCommand);
        }

        if (getServer().getPluginManager().isPluginEnabled("DAPI")) {
            DAPIHook.registerPlugin(this);
        } else {
            getLogger().info("DAPI non détecté : DEvent fonctionne en mode autonome (pas de mise en vente automatique).");
        }

        getLogger().info("DEvent activé (" + eventManager.listEvents().size() + " événement(s) planifié(s)).");
    }

    @Override
    public void onDisable() {
        if (eventManager != null) {
            eventManager.cancelAll();
        }
    }
}
