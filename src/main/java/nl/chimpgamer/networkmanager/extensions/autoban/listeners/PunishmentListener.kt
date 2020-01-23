package nl.chimpgamer.networkmanager.extensions.autoban.listeners

import nl.chimpgamer.networkmanager.api.NMListener
import nl.chimpgamer.networkmanager.api.event.NMEvent
import nl.chimpgamer.networkmanager.api.event.events.PunishmentEvent
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan

class PunishmentListener(private val autoBan: AutoBan) : NMListener {

    @NMEvent
    fun onPunishment(event: PunishmentEvent) {
        val cachedPlayers = autoBan.networkManager.cacheManager.cachedPlayers
        val cachedPunishments = autoBan.networkManager.cacheManager.cachedPunishments
        val punishment = event.punishment
        val opPlayer = cachedPlayers.getPlayerSafe(punishment.uuid)
        if (!opPlayer.isPresent) { // couldn't load player for some reason.
            return
        }
        val player = opPlayer.get()
        for (punishmentAction in autoBan.settings.punishmentActions) {
            if (punishment.type == punishmentAction.onActionType) {
                val total = cachedPunishments.getPunishment(punishmentAction.onActionType).stream()
                        .filter { punishment1: Punishment -> punishment1.uuid == punishment.uuid }.count()
                if (total.toInt() == punishmentAction.count) {
                    val newPunishment = cachedPunishments.createPunishmentBuilder()
                            .setType(punishmentAction.actionType)
                            .setUuid(player.uuid)
                            .setPunisher(cachedPlayers.console.uuid) // Console UUID
                            .setEnd(punishmentAction.duration)
                            .setIp(player.ip)
                            .setReason(punishmentAction.reason)
                            .build()
                    cachedPunishments.executePunishment(newPunishment)
                    break
                }
            }
        }
    }
}