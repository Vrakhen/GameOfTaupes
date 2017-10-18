package fr.vraken.gameoftaupes;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title
{
  public static void sendTitle(Player p, String title, String subtitle)
  {
    IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\"\",\"extra\":[{\"text\":\"" + title + "\",\"color\":\"gold\",\"underlined\":\"true\"}]}");
    IChatBaseComponent subTitle = ChatSerializer.a("{\"text\":\"\",\"extra\":[{\"text\":\"" + subtitle + "\",\"color\":\"drak_blue\"}]}");
    PacketPlayOutTitle titles = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle, 1, 2, 0);
    PacketPlayOutTitle subtitles = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subTitle);
    PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
    connection.sendPacket(subtitles);
    connection.sendPacket(titles);
  }
}
