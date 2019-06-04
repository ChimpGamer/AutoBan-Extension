package nl.chimpgamer.networkmanager.extensions.autoban.configuration;

import com.google.common.collect.Sets;
import nl.chimpgamer.networkmanager.api.models.punishments.Punishment;
import nl.chimpgamer.networkmanager.api.utils.FileUtils;
import nl.chimpgamer.networkmanager.api.utils.TimeUtils;
import nl.chimpgamer.networkmanager.extensions.autoban.AutoBan;
import nl.chimpgamer.networkmanager.extensions.autoban.models.WarnAction;
import java.util.Set;

public class Settings extends FileUtils {
    private final AutoBan autoBan;

    public Settings(AutoBan autoBan) {
        super(autoBan.getDataFolder().getPath(), "settings.yml");
        if (this.getFile().length() == 0) {
            this.saveToFile(autoBan.getResource("settings.yml"));
            this.reload();
        }
        this.autoBan = autoBan;
    }

    public Set<WarnAction> getWarnActions() {
        Set<WarnAction> warnActions = Sets.newHashSet();
        for (String key : this.getConfig().getConfigurationSection("warnactions").getKeys(false)) {
            int count = -1;
            try {
                count = Integer.parseInt(key);
            } catch (NumberFormatException ex) {
                this.getAutoBan().getLogger().warning("WarnAction has invalid count: " + count);
                continue;
            }
            Punishment.Type action = Punishment.Type.valueOf(this.getString("warnactions." + key + ".action"));
            long duration = -1;
            String durationStr = this.getString("warnactions." + key + ".duration");
            String reason = this.getString("warnactions." + key + ".reason");
            if (durationStr != null && action.isTemp()) {
                try {
                    duration = TimeUtils.toMilliSec(durationStr);
                } catch (IllegalArgumentException ex) {
                    this.getAutoBan().getLogger().warning(ex.getMessage());
                    this.getAutoBan().getLogger().warning("WarnAction has invalid duration: " + count);
                }
            }
            warnActions.add(new WarnAction(count, action, duration, reason));
        }
        return warnActions;
    }

    private AutoBan getAutoBan() {
        return autoBan;
    }
}