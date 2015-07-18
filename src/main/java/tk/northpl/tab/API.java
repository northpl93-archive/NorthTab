package tk.northpl.tab;

import org.bukkit.entity.Player;

public final class API
{
    private API()
    {
    }

    public static void setTabSlot(final int x, final int y, final String content)
    {
        Main.getInstance().getTabListHandler().getSlot(x, y).setGlobalText(content);
    }

    public static void setTabSlot(final Player p, final int x, final int y, final String content)
    {
        Main.getInstance().getTabListHandler().getSlot(x, y).setTextForPlayer(p.getName(), content);
    }

    public static void updateSkin(final int x, final int y, String newNick)
    {
        if (newNick == null)
        {
            newNick = Main.getInstance().DEFAULT_HEAD;
        }
        Main.getInstance().getTabListHandler().updateSkin(x, y, newNick);
    }

    public static void updateSkin(final Player p, final int x, final int y, String newSkin)
    {
        if (newSkin == null)
        {
            newSkin = Main.getInstance().DEFAULT_HEAD;
        }
        Main.getInstance().getTabListHandler().getSlot(x, y).setSkinForPlayer(p.getName(), newSkin);
    }
}
