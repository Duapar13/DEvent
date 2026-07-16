package com.devent.integration;

import com.dapi.DAPI;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * DEvent ne fournit aucun service DAPI (rien à exposer : c'est un
 * orchestrateur, pas une source de données) - il se contente de s'enregistrer
 * auprès de DAPI pour apparaître dans /dapi list, et consomme AuctionService
 * via un lookup isolé (AuctionLookup) pour les événements "loot-drop".
 */
public final class DAPIHook {

    private DAPIHook() {
    }

    public static void registerPlugin(JavaPlugin plugin) {
        DAPI.registerPlugin(plugin);
    }
}
