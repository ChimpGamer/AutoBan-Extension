package nl.chimpgamer.networkmanager.extensions.autoban.listeners

import nl.chimpgamer.networkmanager.api.NMListener
import nl.chimpgamer.networkmanager.api.event.NMEvent
import nl.chimpgamer.networkmanager.api.event.events.PunishmentEvent
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
                val total = cachedPunishments.getPunishment(punishmentAction.onActionType)
                        .filter { it.uuid == punishment.uuid }.size
                if (total == punishmentAction.count) {
                    val duration = punishmentAction.duration

                    val newPunishment = cachedPunishments.createPunishmentBuilder()
                            .setType(punishmentAction.actionType)
                            .setUuid(player.uuid)
                            .setPunisher(cachedPlayers.console.uuid) // Console UUID
                            .setEnd(if (duration != -1L) System.currentTimeMillis() + duration else duration)
                            .setIp(player.ip)
                            .setReason(punishmentAction.reason
                                    .replace("%count%", total.toString()))
                            .build()
                    cachedPunishments.executePunishment(newPunishment)
                    break
                }
            }
        }
    }
}