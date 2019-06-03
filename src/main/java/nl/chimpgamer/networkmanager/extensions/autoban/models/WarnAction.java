package nl.chimpgamer.networkmanager.extensions.autoban.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment;

@AllArgsConstructor
@Getter
public class WarnAction {
    private int count;
    private Punishment.Type action;
    private long duration = -1L;
    private String reason;
}