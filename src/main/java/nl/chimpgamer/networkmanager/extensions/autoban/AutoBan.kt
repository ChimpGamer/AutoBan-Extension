package nl.chimpgamer.networkmanager.extensions.autoban

import nl.chimpgamer.networkmanager.api.extensions.NMExtension
import nl.chimpgamer.networkmanager.api.utils.PlatformType
import nl.chimpgamer.networkmanager.extensions.autoban.configuration.Settings
import nl.chimpgamer.networkmanager.extensions.autoban.listeners.PunishmentListener

class AutoBan : NMExtension() {
    val settings = Settings(this)
    lateinit var punishmentListener: PunishmentListener

    override fun onEnable() {
        if (networkManager.platformType !== PlatformType.BUNGEECORD) {
            logger.severe("Hey, this NetworkManager extension is for BungeeCord only!")
            return
        }
        settings.load()
        punishmentListener = PunishmentListener(this)
        networkManager.eventHandler.registerListener(punishmentListener)
    }

    override fun onDisable() {
        networkManager.eventHandler.unregisterListener(punishmentListener)
    }

    override fun onConfigsReload() {
        settings.reload()
    }
}