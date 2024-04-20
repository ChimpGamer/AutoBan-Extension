package nl.chimpgamer.networkmanager.extensions.autoban

import nl.chimpgamer.networkmanager.api.extensions.NMExtension
import nl.chimpgamer.networkmanager.extensions.autoban.configuration.Settings
import nl.chimpgamer.networkmanager.extensions.autoban.listeners.PunishmentListener

class AutoBan : NMExtension() {
    val settings = Settings(this)
    private val punishmentListener = PunishmentListener(this)

    override fun onEnable() {
        if (!networkManager.platformType.isProxy) {
            logger.severe("Hey, this NetworkManager extension is for BungeeCord and Velocity only!")
            return
        }
        settings.load()
        punishmentListener.register()
    }

    override fun onDisable() {
        punishmentListener.unregister()
    }

    override fun onConfigsReload() {
        settings.reload()
    }
}