package com.duapar.devent.model;

import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

public class ScheduledEvent {

    private final String id;
    private final EventType type;
    private final long intervalMinutes;
    private final String announce;
    private final List<Material> lootPool;
    private final long priceMin;
    private final long priceMax;

    public ScheduledEvent(String id, EventType type, long intervalMinutes, String announce,
                           List<Material> lootPool, long priceMin, long priceMax) {
        this.id = id;
        this.type = type;
        this.intervalMinutes = intervalMinutes;
        this.announce = announce;
        this.lootPool = Collections.unmodifiableList(lootPool);
        this.priceMin = priceMin;
        this.priceMax = priceMax;
    }

    public String getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public long getIntervalMinutes() {
        return intervalMinutes;
    }

    public String getAnnounce() {
        return announce;
    }

    public List<Material> getLootPool() {
        return lootPool;
    }

    public long getPriceMin() {
        return priceMin;
    }

    public long getPriceMax() {
        return priceMax;
    }
}
