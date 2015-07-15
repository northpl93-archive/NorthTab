package tk.northpl.tab;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class Utils
{
    private static final Map<String, Collection<WrappedSignedProperty>> propertiesCache = new HashMap<>(20);
    private static Object getSessionService;
    private static Method getFillMethod;

    private Utils()
    {
    }

    public static Collection<WrappedSignedProperty> getSkinProperties(final String playerName)
    {
        if (propertiesCache.keySet().contains(playerName))
        {
            return propertiesCache.get(playerName);
        }
        final Player onlinePlayer = Bukkit.getPlayerExact(playerName);
        final WrappedGameProfile profile;
        if (onlinePlayer != null)
        {
            profile = WrappedGameProfile.fromPlayer(onlinePlayer);
        }
        else
        {
            profile = WrappedGameProfile.fromOfflinePlayer(Bukkit.getOfflinePlayer(playerName));
        }
        final Object handle = profile.getHandle();
        final Object sessionService = Utils.getSessionService();
        try
        {
            getFillMethod(sessionService).invoke(sessionService, handle, true);
        }
        catch (final IllegalAccessException | InvocationTargetException ex)
        {
            ex.printStackTrace();
            return null;
        }
        final Collection<WrappedSignedProperty> temp = WrappedGameProfile.fromHandle(handle).getProperties().get("textures");
        propertiesCache.put(playerName, temp);
        return temp;
    }

    public static Object getSessionService()
    {
        if (getSessionService != null)
        {
            return getSessionService;
        }

        final Server server = Bukkit.getServer();
        try
        {
            final Object mcServer = server.getClass().getDeclaredMethod("getServer").invoke(server);
            for (final Method m : mcServer.getClass().getMethods())
            {
                if (m.getReturnType().getSimpleName().equalsIgnoreCase("MinecraftSessionService"))
                {
                    final Object temp = m.invoke(mcServer);
                    getSessionService = temp;
                    return temp;
                }
            }
        }
        catch (final Exception ex)
        {
            throw new IllegalStateException("An error occurred while trying to get the session service", ex);
        }
        throw new IllegalStateException("No session service found :o");
    }

    public static Method getFillMethod(final Object sessionService)
    {
        if (getFillMethod != null)
        {
            return getFillMethod;
        }

        for(final Method m : sessionService.getClass().getDeclaredMethods())
        {
            if(m.getName().equals("fillProfileProperties"))
            {
                getFillMethod = m;
                return m;
            }
        }
        throw new IllegalStateException("No fillProfileProperties method found in the session service :o");
    }
}
