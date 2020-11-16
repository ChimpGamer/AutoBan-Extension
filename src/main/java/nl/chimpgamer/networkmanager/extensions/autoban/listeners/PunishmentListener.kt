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
        val player = cachedPlayers.getPlayer(punishment.uuid) ?: return // Return if player object cannot be found.
        for (punishmentAction in autoBan.settings.punishmentActions) {
            if (punishment.type == punishmentAction.onActionType) {
                val total = cachedPunishments.getPunishment(punishmentAction.onActionType)
                        .count { it.uuid == punishment.uuid }
                if (total == punishmentAction.count) {
                    val duration = punishmentAction.duration

                    val newPunishment = cachedPunishments.createPunishmentBuilder()
                            .type(punishmentAction.actionType)
                            .uuid(player.uuid)
                            .punisher(cachedPlayers.console.uuid) // Console UUID
                            .end(if (duration != -1L) System.currentTimeMillis() + duration else duration)
                            .ip(player.ip)
                            .reason(punishmentAction.reason
                                    .replace("%count%", total.toString()))
                            .build()
                    if (newPunishment != null) {
                        cachedPunishments.executePunishment(newPunishment)
                    }
                    break
                }
            }
        }
    }
}