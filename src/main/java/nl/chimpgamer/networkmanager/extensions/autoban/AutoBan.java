package nl.chimpgamer.networkmanager.extensions.autoban;

import lombok.Getter;
import nl.chimpgamer.networkmanager.api.extensions.NMExtension;
import nl.chimpgamer.networkmanager.api.utils.PlatformType;
import nl.chimpgamer.networkmanager.extensions.autoban.configuration.Settings;
import nl.chimpgamer.networkmanager.extensions.autoban.listeners.PunishmentListener;

@Getter
public class AutoBan extends NMExtension {
    private Settings settings;

    @Override
    protected void onEnable() {
        if (this.getNetworkManager().getPlatformType() != PlatformType.BUNGEECORD) {
            this.getLogger().severe("Hey, this NetworkManager extension is for BungeeCord only!");
            return;
        }
        this.settings = new Settings(this);

        this.getNetworkManager().registerListener(new PunishmentListener(this));
    }

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onConfigsReload() {
        this.getSettings().reload();
    }
}