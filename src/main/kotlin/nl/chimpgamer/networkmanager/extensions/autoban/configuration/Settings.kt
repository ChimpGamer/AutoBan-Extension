package nl.chimpgamer.networkmanager.extensions.autoban.configuration

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment
import nl.chimpgamer.networkmanager.api.utils.TimeUtils
import nl.chimpgamer.networkmanager.api.values.Message
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan
import nl.chimpgamer.networkmanager.extensions.autoban.models.PunishmentAction
import java.io.IOException
import java.util.logging.Level

class Settings(private val autoBan: AutoBan) {
    private val config: YamlDocument
    val punishmentActions = HashSet<PunishmentAction>()

    init {
        val file = autoBan.dataFolder.resolve("settings.yml")
        val inputStream = autoBan.getResource("settings.yml")
        val generalSettings = GeneralSettings.builder().setUseDefaults(false).build()
        val loaderSettings = LoaderSettings.builder().setAutoUpdate(false).build()
        config = if (inputStream != null) {
            YamlDocument.create(file, inputStream, generalSettings, loaderSettings, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT)
        } else {
            YamlDocument.create(file, generalSettings, loaderSettings, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT)
        }
    }

    fun load() {
        val actionsSection = config.getSection("actions")
        for (onActionTypeStr in actionsSection.getRoutesAsStrings(false)) {
            val onActionType = try {
                Punishment.Type.valueOf(onActionTypeStr)
            } catch (ex: IllegalArgumentException) {
                autoBan.logger.log(Level.WARNING, "Action $onActionTypeStr has invalid onActionType: $onActionTypeStr", ex)
                continue
            }
            val actionSection = actionsSection.getSection(onActionTypeStr)
            for (countKey in actionSection.getRoutesAsStrings(false)) {
                val count = try {
                    countKey.toInt()
                } catch (ex: NumberFormatException) {
                    autoBan.logger.warning("Action $onActionTypeStr has invalid count: $countKey")
                    continue
                }
                val action = actionSection.getString("$countKey.action")
                if (action == null) {
                    autoBan.logger.warning("Action $onActionTypeStr.$countKey has no action.")
                    continue
                }
                val punishmentType = try {
                    Punishment.Type.valueOf(action)
                } catch (ex: IllegalArgumentException) {
                    autoBan.logger.log(Level.WARNING, "Action $onActionTypeStr.$countKey has invalid actionType: $action", ex)
                    continue
                }
                var duration = -1L
                val durationStr = actionSection.getString("$countKey.duration")
                val reason = actionSection.getString("$countKey.reason", autoBan.networkManager.getMessage(Message.PUNISHMENT_NO_REASON))
                if (durationStr != null && punishmentType.isTemp) {
                    try {
                        duration = TimeUtils.toMilliSec(durationStr)
                    } catch (ex: IllegalArgumentException) {
                        autoBan.logger.log(Level.WARNING, "Action $onActionTypeStr.$countKey has invalid duration: $duration", ex)
                        continue
                    }
                }
                punishmentActions.add(PunishmentAction(onActionType, count, punishmentType, duration, reason))
            }
        }
    }

    fun reload() {
        try {
            config.reload()
            punishmentActions.clear()
            load()
        } catch (ex: IOException) {
            autoBan.logger.log(Level.SEVERE, "Something went wrong trying to reload the settings.yml configuration file", ex)
        }
    }
}