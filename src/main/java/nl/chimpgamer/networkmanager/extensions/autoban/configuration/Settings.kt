package nl.chimpgamer.networkmanager.extensions.autoban.configuration

import com.google.common.collect.Sets
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment
import nl.chimpgamer.networkmanager.api.utils.FileUtils
import nl.chimpgamer.networkmanager.api.utils.TimeUtils
import nl.chimpgamer.networkmanager.api.values.Message
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan
import nl.chimpgamer.networkmanager.extensions.autoban.models.PunishmentAction

class Settings(private val autoBan: AutoBan) : FileUtils(autoBan.dataFolder.path, "settings.yml") {
    val punishmentActions: MutableSet<PunishmentAction> = Sets.newHashSet()
    fun load() {
        for (onActionTypeStr in config.getConfigurationSection("actions").getKeys(false)) {
            val onActionType = Punishment.Type.valueOf(onActionTypeStr)
            for (countKey in config.getConfigurationSection("actions.$onActionTypeStr").getKeys(false)) {
                var count = -1
                count = try {
                    countKey.toInt()
                } catch (ex: NumberFormatException) {
                    autoBan.logger.warning("Action $onActionTypeStr has invalid count: $count")
                    continue
                }
                val action = Punishment.Type.valueOf(this.getString("actions.$onActionTypeStr.$countKey.action"))
                var duration = -1L
                val durationStr = this.getString("actions.$onActionTypeStr.$countKey.duration")
                val reason = this.getString("actions.$onActionTypeStr.$countKey.reason", autoBan.networkManager.getMessage(Message.PUNISHMENT_NO_REASON))
                if (durationStr != null && action.isTemp) {
                    try {
                        duration = TimeUtils.toMilliSec(durationStr)
                    } catch (ex: IllegalArgumentException) {
                        autoBan.logger.warning(ex.message)
                        autoBan.logger.warning("Action $onActionTypeStr has invalid duration: $count")
                        continue
                    }
                }
                punishmentActions.add(PunishmentAction(onActionType, count, action, duration, reason))
            }
        }
    }

    override fun reload() {
        super.reload()
        punishmentActions.clear()
        load()
    }

    init {
        if (file.length() == 0L) {
            saveToFile(autoBan.getResource("settings.yml"))
            reload()
        }
    }
}