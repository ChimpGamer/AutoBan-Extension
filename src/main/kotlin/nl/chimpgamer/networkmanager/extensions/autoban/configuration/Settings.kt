package nl.chimpgamer.networkmanager.extensions.autoban.configuration

import nl.chimpgamer.networkmanager.api.models.punishments.Punishment
import nl.chimpgamer.networkmanager.api.utils.FileUtils
import nl.chimpgamer.networkmanager.api.utils.TimeUtils
import nl.chimpgamer.networkmanager.api.values.Message
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan
import nl.chimpgamer.networkmanager.extensions.autoban.models.PunishmentAction

class Settings(private val autoBan: AutoBan) : FileUtils(autoBan.dataFolder.path, "settings.yml") {
    val punishmentActions = HashSet<PunishmentAction>()
    fun load() {
        for (onActionTypeStr in config.getConfigurationSection("actions").getKeys(false)) {
            val onActionType = try {
                Punishment.Type.valueOf(onActionTypeStr)
            } catch (ex: IllegalArgumentException) {
                autoBan.logger.warning(ex.message)
                autoBan.logger.warning("Action $onActionTypeStr has invalid onActionType: $onActionTypeStr")
                continue
            }
            for (countKey in config.getConfigurationSection("actions.$onActionTypeStr").getKeys(false)) {
                val count = try {
                    countKey.toInt()
                } catch (ex: NumberFormatException) {
                    autoBan.logger.warning("Action $onActionTypeStr has invalid count: $countKey")
                    continue
                }
                val action = this.getString("actions.$onActionTypeStr.$countKey.action")
                if (action == null) {
                    autoBan.logger.warning("Action $onActionTypeStr.$countKey has no action.")
                    continue
                }
                val punishmentType = try {
                    Punishment.Type.valueOf(action)
                } catch (ex: IllegalArgumentException) {
                    autoBan.logger.warning(ex.message)
                    autoBan.logger.warning("Action $onActionTypeStr.$countKey has invalid actionType: $action")
                    continue
                }
                var duration = -1L
                val durationStr = this.getString("actions.$onActionTypeStr.$countKey.duration")
                val reason = this.getString("actions.$onActionTypeStr.$countKey.reason", autoBan.networkManager.getMessage(Message.PUNISHMENT_NO_REASON))
                if (durationStr != null && punishmentType.isTemp) {
                    try {
                        duration = TimeUtils.toMilliSec(durationStr)
                    } catch (ex: IllegalArgumentException) {
                        autoBan.logger.warning(ex.message)
                        autoBan.logger.warning("Action $onActionTypeStr.$countKey has invalid duration: $duration")
                        continue
                    }
                }
                punishmentActions.add(PunishmentAction(onActionType, count, punishmentType, duration, reason))
            }
        }
    }

    override fun reload() {
        super.reload()
        punishmentActions.clear()
        load()
    }

    init {
        setupFile(autoBan.getResource("settings.yml"))
    }
}