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
import nl.chimpgamer.networkmanager.extensions.autoban.models.WarnAction;

import java.util.Optional;

@RequiredArgsConstructor
public class PunishmentListener implements NMListener {
    private final AutoBan autoBan;

    @NMEvent
    public void onPunishment(PunishmentEvent event) {
        final CachedPlayers cachedPlayers = this.getAutoBan().getNetworkManager().getCacheManager().getCachedPlayers();
        final CachedPunishments cachedPunishments = this.getAutoBan().getNetworkManager().getCacheManager().getCachedPunishments();
        final Punishment punishment = event.getPunishment();
        if (punishment.getType() == Punishment.Type.WARN) {
            Optional<Player> opPlayer = cachedPlayers.getPlayerSafe(punishment.getUuid());
            if (!opPlayer.isPresent()) {
                // couldn't load player for some reason.
                return;
            }
            Player player = opPlayer.get();
            long totalWarns = cachedPunishments.getPunishment(Punishment.Type.WARN).stream()
                    .filter(punishment1 -> punishment1.getUuid().equals(punishment.getUuid())).count();
            for (WarnAction warnAction : this.getAutoBan().getSettings().getWarnActions()) {
                if (totalWarns == warnAction.getCount()) {
                    Punishment newPunishment = cachedPunishments.createPunishmentBuilder()
                            .setType(warnAction.getAction())
                            .setUuid(player.getUuid())
                            .setPunisher(cachedPlayers.getUUID("Console"))
                            .setEnd(warnAction.getDuration()) // 2 minutes
                            .setIp(player.getIp())
                            .setReason(warnAction.getReason())
                            .build();
                    cachedPunishments.executePunishment(newPunishment);
                }
            }
        }
    }

    private AutoBan getAutoBan() {
        return autoBan;
    }
}