package tk.northpl.tab;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("ClassHasNoToStringMethod")
public final class TabListHandler
{
    private static final int    MAX_TAB_SLOTS     = 80;
    private static final int    TAB_COLUMN_HEIGHT = 20;
    private static final char[] alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'};
    private final ProtocolManager               protocol;
    private final HashMap<Integer, TablistSlot> slots;

    public TabListHandler()
    {
        this.protocol = ProtocolLibrary.getProtocolManager();

        this.slots = new HashMap<>(MAX_TAB_SLOTS);

        final Collection<WrappedSignedProperty> properties = Utils.getSkinProperties(Main.getInstance().DEFAULT_HEAD);

        for (int i = 0; i < MAX_TAB_SLOTS; i++)
        {
            final WrappedGameProfile wgm = new WrappedGameProfile(UUID.randomUUID(), "!@#AAAAAAAAAAA" + this.getCharId(i));
            wgm.getProperties().removeAll("textures");
            assert properties != null;
            wgm.getProperties().putAll("textures", properties);
            this.slots.put(i, new TablistSlot(wgm, Main.getInstance().DEFAULT_HEAD, " "));
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new TabListHandlerTask(), Main.getInstance().UPDATE_TIME, Main.getInstance().UPDATE_TIME);
    }

    @SuppressWarnings("MagicNumber")
    private String getCharId(final int id)
    {
        if (id < 20)
        {
            return "A" + alphabet[id];
        }
        if (id < 40)
        {
            return "B" + alphabet[id - 20];
        }
        if (id < 60)
        {
            return "C" + alphabet[id - 40];
        }
        if (id < 80)
        {
            return "D" + alphabet[id - 60];
        }
        return "";
    }

    public TablistSlot getSlot(final int x, final int y)
    {
        return this.slots.get(this.xyToPlayerId(x, y));
    }

    private int xyToPlayerId(final int x, final int y)
    {
        return (x * TAB_COLUMN_HEIGHT) + y;
    }

    public void updateSkin(final int x, final int y, final String newSkinNick)
    {
        this.slots.get(this.xyToPlayerId(x, y)).setSkinNick(newSkinNick);
    }

    public ProtocolManager getProtocol()
    {
        return this.protocol;
    }

    public HashMap<Integer, TablistSlot> getSlots()
    {
        return this.slots;
    }

    private final class TabListHandlerTask implements Runnable
    {
        @Override
        public void run()
        {
            final Map<String, List<PlayerInfoData>> cache = new HashMap<>(50);

            for (final TablistSlot slot : TabListHandler.this.slots.values())
            {
                if (slot.isSkinNickDirty())
                { // globalny skin nie zostal zaktualizowany
                    final PacketContainer add = TabListHandler.this.protocol.createPacket(PacketType.Play.Server.PLAYER_INFO);
                    add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

                    for (final Player p : Bukkit.getOnlinePlayers())
                    {
                        try
                        {
                            final PlayerInfoData pid = new PlayerInfoData(slot.getVirtualPlayer(), Main.getInstance().TABLIST_PING, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(slot.getTextForPlayer(p.getName())));
                            add.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));
                            TabListHandler.this.protocol.sendServerPacket(p, add);
                        }
                        catch (final InvocationTargetException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    slot.setIsSkinNickDirty(false);
                } // koniec aktualizacji globalnego skina na tabie

                if (slot.isGlobalTextDirty())
                { // globalny tekst nie zostal zaktualizowany
                    Main.getInstance().debug("Global text is dirty! (" + slot.getGlobalText() + ")");
                    final PlayerInfoData pid = new PlayerInfoData(slot.getVirtualPlayer(), Main.getInstance().TABLIST_PING, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(slot.getGlobalText()));

                    List<PlayerInfoData> collsToUpdate = cache.get("!@#GLOBAL$%^");
                    if (collsToUpdate == null)
                    {
                        collsToUpdate = new ArrayList<>(10);
                        cache.put("!@#GLOBAL$%^", collsToUpdate);
                    }
                    collsToUpdate.add(pid);
                    slot.setIsGlobalTextDirty(false);
                    continue;
                } // koniec aktualizacji globalnego tekstu

                for (final TablistSlot.CustomPlayer cpt : slot.getCustomPlayersOptions())
                {
                    if (cpt.isSkinDirty())
                    {
                        Main.getInstance().debug("Player (" + cpt.getPlayerNick() + ") skin is dirty! (" + cpt.getSkin() + ")");
                        final Player p = Bukkit.getPlayerExact(cpt.getPlayerNick());
                        final PacketContainer add = TabListHandler.this.protocol.createPacket(PacketType.Play.Server.PLAYER_INFO);

                        final WrappedGameProfile gameP = new WrappedGameProfile(slot.getVirtualPlayer().getUUID(), slot.getVirtualPlayer().getName());
                        gameP.getProperties().removeAll("textures");
                        //noinspection ConstantConditions
                        gameP.getProperties().putAll("textures", Utils.getSkinProperties(cpt.getSkin()));

                        add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                        final PlayerInfoData pid = new PlayerInfoData(gameP, Main.getInstance().TABLIST_PING, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(slot.getTextForPlayer(p.getName())));
                        add.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));

                        try
                        {
                            TabListHandler.this.protocol.sendServerPacket(Bukkit.getPlayerExact(cpt.getPlayerNick()), add);
                        } catch (final InvocationTargetException e)
                        {
                            e.printStackTrace();
                        }

                        cpt.setIsSkinDirty(false);
                    }

                    if (cpt.isTextDirty())
                    {
                        Main.getInstance().debug("Player (" + cpt.getPlayerNick() + ") text is dirty! (" + cpt.getText() + ")");
                        final PlayerInfoData pid = new PlayerInfoData(slot.getVirtualPlayer(), Main.getInstance().TABLIST_PING, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(cpt.getText()));

                        List<PlayerInfoData> collsToUpdate = cache.get(cpt.getPlayerNick());
                        if (collsToUpdate == null)
                        {
                            collsToUpdate = new ArrayList<>(10);
                            cache.put(cpt.getPlayerNick(), collsToUpdate);
                        }
                        collsToUpdate.add(pid);
                        cpt.setTextDirty(false);
                    }
                }
            }

            for (final Map.Entry<String, List<PlayerInfoData>> p : cache.entrySet())
            {
                final boolean isGlobal = p.getKey().equals("!@#GLOBAL$%^");

                final PacketContainer writeTab = TabListHandler.this.protocol.createPacket(PacketType.Play.Server.PLAYER_INFO);

                writeTab.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
                writeTab.getPlayerInfoDataLists().write(0, p.getValue());

                if (isGlobal)
                {
                    for (final Player player : Bukkit.getOnlinePlayers())
                    {
                        try
                        {
                            TabListHandler.this.protocol.sendServerPacket(player, writeTab);
                        }
                        catch (final InvocationTargetException e)
                        {
                            Main.getInstance().logInfo("Failed to send packet: " + e.getMessage());
                        }
                    }
                    continue;
                }
                try
                {
                    TabListHandler.this.protocol.sendServerPacket(Bukkit.getPlayerExact(p.getKey()), writeTab);
                }
                catch (final InvocationTargetException e)
                {
                    Main.getInstance().logInfo("Failed to send packet: " + e.getMessage());
                }
            }
        }
    }
}
