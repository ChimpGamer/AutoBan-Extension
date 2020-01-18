package nl.chimpgamer.networkmanager.extensions.autoban

import nl.chimpgamer.networkmanager.api.extensions.NMExtension
import nl.chimpgamer.networkmanager.api.utils.PlatformType
import nl.chimpgamer.networkmanager.extensions.autoban.configuration.Settings
import nl.chimpgamer.networkmanager.extensions.autoban.listeners.PunishmentListener

class AutoBan : NMExtension() {
    lateinit var settings: Settings
    override fun onEnable() {
        if (networkManager.platformType != PlatformType.BUNGEECORD) {
            logger.severe("Hey, this NetworkManager extension is for BungeeCord only!")
            return
        }
        settings = Settings(this)
        settings.load()
        networkManager.registerListener(PunishmentListener(this))
    }

    override fun onDisable() {}
    override fun onConfigsReload() {
        settings.reload()
    }
}