package nl.chimpgamer.networkmanager.extensions.autoban.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment;

@AllArgsConstructor
@Getter
public class PunishmentAction {
    private Punishment.Type onActionType;
    private int count;
    private Punishment.Type actionType;
    private long duration;
    private String reason;
}