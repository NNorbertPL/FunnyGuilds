package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcTeleport implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;

        if (!Settings.getConfig().regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (args.length < 1) {
            player.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            player.sendMessage(messages.generalNoGuildFound);
            return;
        }

        Region region = guild.getRegion();

        if (region == null || region.getCenter() == null) {
            player.sendMessage(messages.adminNoRegionFound);
            return;
        }

        player.sendMessage(messages.baseTeleport);
        player.teleport(region.getCenter());
    }

}
