package tk.northpl.tab;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("ClassHasNoToStringMethod")
public final class Main extends JavaPlugin
{
    private static Main           instance;
    private        Logger         logger;
    private        TabListHandler tabListHandler;
    // Settings
    public int UPDATE_TIME;
    public String DEFAULT_HEAD;
    public int TABLIST_PING;
    public boolean DEBUG;

    @Override
    public void onEnable()
    {
        Main.instance = this;
        this.logger = Bukkit.getLogger();
        this.loadSettings();
        this.tabListHandler = new TabListHandler();
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        this.logInfo("Plugin started!");
    }

    private void loadSettings()
    {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.UPDATE_TIME = this.getConfig().getInt("update-time", 20);
        this.DEFAULT_HEAD = this.getConfig().getString("default-head", "Notch");
        this.TABLIST_PING = this.getConfig().getInt("tablist-ping", 100);
        this.DEBUG = this.getConfig().getBoolean("debug", false);
    }

    public TabListHandler getTabListHandler()
    {
        return this.tabListHandler;
    }

    public Logger getLog()
    {
        return this.logger;
    }

    public void logInfo(final String message)
    {
        this.logger.info("[NorthTab]" + message);
    }

    public void debug(final String debug)
    {
        if (this.DEBUG)
        {
            this.logInfo("[DEBUG]" + debug);
        }
    }

    public static Main getInstance()
    {
        return Main.instance;
    }
}
