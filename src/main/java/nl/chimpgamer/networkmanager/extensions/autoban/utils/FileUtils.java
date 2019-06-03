package nl.chimpgamer.networkmanager.extensions.autoban.utils;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.*;

public class FileUtils {
    private final File file;
    private final FileConfiguration config;

    public FileUtils(String path, String file) {
        this.file = new File(path, file);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileUtils addDefault(String path, Object value) {
        this.config.addDefault(path, value);
        return this;
    }

    public FileUtils saveToFile(InputStream in) {
        try (OutputStream out = new FileOutputStream(getFile())) {
            byte[] buf = new byte[1024];

            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public FileUtils copyDefaults(boolean copyDefaults) {
        this.config.options().copyDefaults(copyDefaults);
        return this;
    }

    public FileUtils set(String path, Object value) {
        this.config.set(path, value);
        return this;
    }

    public String getString(String path) {
        return this.getConfig().getString(path);
    }

    public FileUtils save() {
        try {
            this.config.save(this.file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public File getFile() {
        return this.file;
    }

    public void reload() {
        try {
            this.config.load(this.file);
        } catch (InvalidConfigurationException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void delete() {
        this.file.delete();
    }
}