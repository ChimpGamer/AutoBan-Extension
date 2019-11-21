package nl.chimpgamer.networkmanager.extensions.autoban.configuration;

import com.google.common.collect.Sets;
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment;
import nl.chimpgamer.networkmanager.api.utils.FileUtils;
import nl.chimpgamer.networkmanager.api.utils.TimeUtils;
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan;
import nl.chimpgamer.networkmanager.extensions.autoban.models.PunishmentAction;

import java.util.Set;

public class Settings extends FileUtils {
    private final AutoBan autoBan;
    private final Set<PunishmentAction> punishmentActions;

    public Settings(AutoBan autoBan) {
        super(autoBan.getDataFolder().getPath(), "settings.yml");
        this.autoBan = autoBan;
        if (this.getFile().length() == 0) {
            this.saveToFile(autoBan.getResource("settings.yml"));
            this.reload();
        }
        this.punishmentActions = Sets.newHashSet();
    }

    public void load() {
        for (String onActionTypeStr : this.getConfig().getConfigurationSection("actions").getKeys(false)) {
            Punishment.Type onActionType = Punishment.Type.valueOf(onActionTypeStr);
            for (String countKey : this.getConfig().getConfigurationSection("actions." + onActionTypeStr).getKeys(false)) {
                int count = -1;
                try {
                    count = Integer.parseInt(countKey);
                } catch (NumberFormatException ex) {
                    this.getAutoBan().getLogger().warning("Action " + onActionTypeStr + " has invalid count: " + count);
                    continue;
                }

                Punishment.Type action = Punishment.Type.valueOf(this.getString("actions." + onActionTypeStr + "." + countKey + ".action"));
                long duration = -1;
                String durationStr = this.getString("actions." + onActionTypeStr + "." + countKey + ".duration");
                String reason = this.getString("actions." + onActionTypeStr + "." + countKey + ".reason");
                if (durationStr != null && action.isTemp()) {
                    try {
                        duration = TimeUtils.toMilliSec(durationStr);
                    } catch (IllegalArgumentException ex) {
                        this.getAutoBan().getLogger().warning(ex.getMessage());
                        this.getAutoBan().getLogger().warning("Action " + onActionTypeStr + " has invalid duration: " + count);
                    }
                }
                this.getPunishmentActions().add(new PunishmentAction(onActionType, count, action, duration, reason));
            }
        }
    }

    @Override
    public void reload() {
        super.reload();
        this.getPunishmentActions().clear();
        this.load();
    }

    public Set<PunishmentAction> getPunishmentActions() {
        return punishmentActions;
    }

    private AutoBan getAutoBan() {
        return autoBan;
    }
}