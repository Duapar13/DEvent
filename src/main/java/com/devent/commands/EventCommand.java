package com.devent.commands;

import com.devent.manager.EventException;
import com.devent.manager.EventManager;
import com.devent.model.ScheduledEvent;
import com.devent.util.Msg;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

public class EventCommand implements CommandExecutor {

    private final EventManager eventManager;

    public EventCommand(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("devent.use")) {
            Msg.error(sender, "Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }

        try {
            if (args.length > 0 && "trigger".equalsIgnoreCase(args[0])) {
                handleTrigger(sender, args);
            } else {
                handleList(sender);
            }
        } catch (EventException e) {
            Msg.error(sender, e.getMessage());
        }
        return true;
    }

    private void handleTrigger(CommandSender sender, String[] args) {
        if (!sender.hasPermission("devent.admin")) {
            throw new EventException("Tu n'as pas la permission de déclencher un événement.");
        }
        if (args.length < 2) {
            throw new EventException("Utilisation: /event trigger <id>");
        }
        ScheduledEvent event = eventManager.getEventOrThrow(args[1]);
        eventManager.trigger(event);
        Msg.success(sender, "Événement '" + event.getId() + "' déclenché.");
    }

    private void handleList(CommandSender sender) {
        List<ScheduledEvent> events = eventManager.listEvents();
        if (events.isEmpty()) {
            Msg.send(sender, "Aucun événement configuré.");
            return;
        }
        sender.sendMessage(ChatColor.DARK_GRAY + "==== " + ChatColor.YELLOW + "Événements planifiés" + ChatColor.DARK_GRAY + " ====");
        for (ScheduledEvent event : events) {
            sender.sendMessage(ChatColor.GOLD + event.getId() + ChatColor.GRAY + " - "
                    + event.getType().name().toLowerCase(Locale.ROOT) + " - toutes les " + event.getIntervalMinutes() + " min");
        }
    }
}
