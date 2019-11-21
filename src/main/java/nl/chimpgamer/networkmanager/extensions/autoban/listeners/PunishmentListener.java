package nl.chimpgamer.networkmanager.extensions.autoban.listeners;

import lombok.RequiredArgsConstructor;
import nl.chimpgamer.networkmanager.api.NMListener;
import nl.chimpgamer.networkmanager.api.cache.modules.CachedPlayers;
import nl.chimpgamer.networkmanager.api.cache.modules.CachedPunishments;
import nl.chimpgamer.networkmanager.api.event.NMEvent;
import nl.chimpgamer.networkmanager.api.event.events.PunishmentEvent;
import nl.chimpgamer.networkmanager.api.models.player.Player;
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment;
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan;
import nl.chimpgamer.networkmanager.extensions.autoban.models.PunishmentAction;

import java.util.Optional;

@RequiredArgsConstructor
public class PunishmentListener implements NMListener {
    private final AutoBan autoBan;

    @NMEvent
    public void onPunishment(PunishmentEvent event) {
        final CachedPlayers cachedPlayers = this.getAutoBan().getNetworkManager().getCacheManager().getCachedPlayers();
        final CachedPunishments cachedPunishments = this.getAutoBan().getNetworkManager().getCacheManager().getCachedPunishments();
        final Punishment punishment = event.getPunishment();
        for (PunishmentAction punishmentAction : this.getAutoBan().getSettings().getPunishmentActions()) {
            if (punishment.getType() == punishmentAction.getOnActionType()) {
                Optional<Player> opPlayer = cachedPlayers.getPlayerSafe(punishment.getUuid());
                if (!opPlayer.isPresent()) {
                    // couldn't load player for some reason.
                    return;
                }
                Player player = opPlayer.get();
                long total = cachedPunishments.getPunishment(punishmentAction.getOnActionType()).stream()
                        .filter(punishment1 -> punishment1.getUuid().equals(punishment.getUuid())).count();
                if (total == punishmentAction.getCount()) {
                    Punishment newPunishment = cachedPunishments.createPunishmentBuilder()
                            .setType(punishmentAction.getActionType())
                            .setUuid(player.getUuid())
                            .setPunisher(cachedPlayers.getConsole().getUuid()) // Console UUID
                            .setEnd(punishmentAction.getDuration())
                            .setIp(player.getIp())
                            .setReason(punishmentAction.getReason())
                            .build();
                    cachedPunishments.executePunishment(newPunishment);
                }
                break;
            }
        }
    }

    private AutoBan getAutoBan() {
        return autoBan;
    }
}