package tk.northpl.tab;

import java.util.Iterator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener
{
    @EventHandler
    public void onLeave(final PlayerQuitEvent e)
    {
        this.cleanup(e.getPlayer().getName());
    }

    @EventHandler
    public void onLeave(final PlayerKickEvent e)
    {
        this.cleanup(e.getPlayer().getName());
    }

    private void cleanup(final String player)
    {
        for (final TablistSlot tablistSlot : Main.getInstance().getTabListHandler().getSlots().values())
        {
            //noinspection ForLoopReplaceableByForEach
            for (final Iterator<TablistSlot.CustomPlayerText> iterator = tablistSlot.getCustomPlayersTexts().iterator(); iterator.hasNext(); )
            {
                final TablistSlot.CustomPlayerText cpt = iterator.next();
                if (cpt.getPlayerNick().equals(player))
                {
                    tablistSlot.getCustomPlayersTexts().remove(cpt);
                }
            }
        }
    }
}
