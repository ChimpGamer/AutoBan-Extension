package nl.chimpgamer.networkmanager.extensions.autoban

import nl.chimpgamer.networkmanager.api.extensions.NMExtension
import nl.chimpgamer.networkmanager.api.utils.PlatformType
import nl.chimpgamer.networkmanager.extensions.autoban.configuration.Settings
import nl.chimpgamer.networkmanager.extensions.autoban.listeners.PunishmentListener

class AutoBan : NMExtension() {
    val settings = Settings(this)

    override fun onEnable() {
        if (networkManager.platformType !== PlatformType.BUNGEECORD) {
            logger.severe("Hey, this NetworkManager extension is for BungeeCord only!")
            return
        }
        settings.load()
        networkManager.registerListener(PunishmentListener(this))
    }

    override fun onDisable() {}
    override fun onConfigsReload() {
        settings.reload()
    }
}