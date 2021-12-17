package nl.chimpgamer.networkmanager.extensions.autoban

import nl.chimpgamer.networkmanager.api.event.events.PunishmentEvent
import nl.chimpgamer.networkmanager.api.extensions.NMExtension
import nl.chimpgamer.networkmanager.api.utils.PlatformType
import nl.chimpgamer.networkmanager.extensions.autoban.configuration.Settings
import nl.chimpgamer.networkmanager.extensions.autoban.listeners.PunishmentListener

class AutoBan : NMExtension() {
    val settings = Settings(this)
    lateinit var punishmentListener: PunishmentListener

    override fun onEnable() {
        if (!networkManager.platformType.isProxy) {
            logger.severe("Hey, this NetworkManager extension is for BungeeCord and Velocity only!")
            return
        }
        settings.load()
        punishmentListener = PunishmentListener(this)
        eventBus.subscribe(PunishmentEvent::class.java, punishmentListener::onPunishment)
    }

    override fun onDisable() {
        //eventBus.unsubscribe(punishmentListener::onPunishment)
    }

    override fun onConfigsReload() {
        settings.reload()
    }
}