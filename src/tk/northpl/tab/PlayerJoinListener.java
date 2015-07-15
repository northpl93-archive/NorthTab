package tk.northpl.tab;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent e)
    {
        // TODO don't create new list every player join
        final PacketContainer writeTab = Main.getInstance().getTabListHandler().getProtocol().createPacket(PacketType.Play.Server.PLAYER_INFO);
        writeTab.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        final List<PlayerInfoData> virtualPlayersToWrite = new ArrayList<>(80);
        for (final TablistSlot wgp : Main.getInstance().getTabListHandler().getSlots().values())
        {
            virtualPlayersToWrite.add(new PlayerInfoData(wgp.getVirtualPlayer(), Main.getInstance().TABLIST_PING, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(" ")));
        }
        writeTab.getPlayerInfoDataLists().write(0, virtualPlayersToWrite);
        try
        {
            Main.getInstance().getTabListHandler().getProtocol().sendServerPacket(e.getPlayer(), writeTab);
        }
        catch (final InvocationTargetException e1)
        {
            Main.getInstance().logInfo("Problem podczas wysylania pakietu PlayServerPlayerInfo do gracze: " + e.getPlayer().getName());
        }
    }
}
