package tk.northpl.tab;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

public final class TablistSlot
{
    private final WrappedGameProfile     virtualPlayer;
    private       String                 skinNick;
    private       boolean                isSkinNickDirty;
    private       String                 globalText;
    private       boolean                isGlobalTextDirty;
    private final List<CustomPlayer>     customPlayersOptions;

    public TablistSlot(final WrappedGameProfile virtualPlayer, final String skinNick, final String globalText)
    {
        this.virtualPlayer = virtualPlayer;
        this.skinNick = skinNick;
        this.globalText = globalText;

        this.customPlayersOptions = new ArrayList<>(0);
    }

    void setSkinNick(final String skinNick)
    {
        if (! this.skinNick.equals(skinNick))
        {
            this.isSkinNickDirty = true;
        }
        this.skinNick = skinNick;
        this.virtualPlayer.getProperties().removeAll("textures");
        //noinspection ConstantConditions
        this.virtualPlayer.getProperties().putAll("textures", Utils.getSkinProperties(skinNick));
    }

    void setGlobalText(final String globalText)
    {
        if (! this.globalText.equals(globalText))
        {
            this.isGlobalTextDirty = true;
        }
        this.globalText = globalText;
    }

    void setIsSkinNickDirty(final boolean isSkinNickDirty)
    {
        this.isSkinNickDirty = isSkinNickDirty;
    }

    void setIsGlobalTextDirty(final boolean isGlobalTextDirty)
    {
        this.isGlobalTextDirty = isGlobalTextDirty;
    }

    public String getSkinNick()
    {
        return this.skinNick;
    }

    public WrappedGameProfile getVirtualPlayer()
    {
        return this.virtualPlayer;
    }

    public String getGlobalText()
    {
        return this.globalText;
    }

    public List<CustomPlayer> getCustomPlayersOptions()
    {
        return this.customPlayersOptions;
    }

    public boolean isGlobalTextDirty()
    {
        return this.isGlobalTextDirty;
    }

    public boolean isSkinNickDirty()
    {
        return this.isSkinNickDirty;
    }

    public String getTextForPlayer(final String player)
    {
        for (final CustomPlayer ctt : this.customPlayersOptions)
        {
            if (ctt.getPlayerNick().equals(player))
            {
                if (ctt.getText() != null)
                {
                    return ctt.getText();
                }
                else
                {
                    break;
                }
            }
        }
        return this.globalText;
    }

    public void setTextForPlayer(final String player, final String text)
    {
        for (final CustomPlayer ctt : this.customPlayersOptions)
        {
            if (ctt.getPlayerNick().equals(player))
            {
                if (! text.equals(ctt.getText()))
                {
                    ctt.setTextDirty(true);
                }
                ctt.setText(text);
                return;
            }
        }
        this.customPlayersOptions.add(new CustomPlayer(player, text, null));
    }

    public String getSkinForPlayer(final String player)
    {
        for (final CustomPlayer ctt : this.customPlayersOptions)
        {
            if (ctt.getPlayerNick().equals(player))
            {
                if (ctt.getSkin() != null)
                {
                    return ctt.getSkin();
                }
                else
                {
                    break;
                }
            }
        }
        return this.skinNick;
    }

    public void setSkinForPlayer(final String player, final String skin)
    {
        for (final CustomPlayer ctt : this.customPlayersOptions)
        {
            if (ctt.getPlayerNick().equals(player))
            {
                if (! skin.equals(ctt.getSkin()))
                {
                    ctt.setIsSkinDirty(true);
                }
                ctt.setSkin(skin);
                return;
            }
        }
        this.customPlayersOptions.add(new CustomPlayer(player, null, skin));
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("TablistSlot{");
        sb.append("virtualPlayer=").append(this.virtualPlayer);
        sb.append(", skinNick='").append(this.skinNick).append('\'');
        sb.append(", isSkinNickDirty=").append(this.isSkinNickDirty);
        sb.append(", globalText='").append(this.globalText).append('\'');
        sb.append(", isGlobalTextDirty=").append(this.isGlobalTextDirty);
        sb.append(", customPlayersOptions=").append(this.customPlayersOptions);
        sb.append('}');
        return sb.toString();
    }

    public static final class CustomPlayer
    {
        private final String  playerNick;

        private       String  text;
        private       boolean isTextDirty;

        private       String  skin;
        private       boolean isSkinDirty;

        public CustomPlayer(final String playerNick, final String text, final String skin)
        {
            this.playerNick = playerNick;
            this.text = text;
            this.skin = skin;

            this.isTextDirty = text != null;
            this.isSkinDirty = skin != null;
        }

        public String getPlayerNick()
        {
            return this.playerNick;
        }

        public String getText()
        {
            return this.text;
        }

        void setText(final String text)
        {
            this.text = text;
        }

        public boolean isTextDirty()
        {
            return this.isTextDirty;
        }

        void setTextDirty(final boolean dirty)
        {
            this.isTextDirty = dirty;
        }

        public String getSkin()
        {
            return this.skin;
        }

        public void setSkin(final String skin)
        {
            this.skin = skin;
        }

        public boolean isSkinDirty()
        {
            return this.isSkinDirty;
        }

        public void setIsSkinDirty(final boolean isSkinDirty)
        {
            this.isSkinDirty = isSkinDirty;
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("CustomPlayer{");
            sb.append("playerNick='").append(this.playerNick).append('\'');
            sb.append(", text='").append(this.text).append('\'');
            sb.append(", isTextDirty=").append(this.isTextDirty);
            sb.append(", skin='").append(this.skin).append('\'');
            sb.append(", isSkinDirty=").append(this.isSkinDirty);
            sb.append('}');
            return sb.toString();
        }
    }
}
