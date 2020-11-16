package nl.chimpgamer.networkmanager.extensions.autoban.models

import nl.chimpgamer.networkmanager.api.models.punishments.Punishment

data class PunishmentAction(val onActionType: Punishment.Type, val count: Int = 0, val actionType: Punishment.Type, val duration: Long = 0, val reason: String)