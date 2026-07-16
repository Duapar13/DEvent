package com.devent.integration;

import com.dapi.DAPI;
import com.dapi.service.AuctionService;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Classe isolée : ne doit être référencée que derrière un
 * {@code isPluginEnabled("DAPI")}, jamais directement, pour ne jamais
 * provoquer de NoClassDefFoundError si DAPI est absent (même principe que
 * dans DRank/DChat).
 */
public final class AuctionLookup {

    /** UUID "système" utilisé comme vendeur pour les annonces créées par un événement. */
    private static final UUID SYSTEM_SELLER = new UUID(0L, 0L);

    private AuctionLookup() {
    }

    /** @return {@code true} si l'annonce a pu être créée (DAuction présent). */
    public static boolean createListing(ItemStack item, long price) {
        AuctionService auction = DAPI.getService(AuctionService.class);
        if (auction == null) {
            return false;
        }
        auction.createListing(SYSTEM_SELLER, "Serveur", item, price);
        return true;
    }
}
