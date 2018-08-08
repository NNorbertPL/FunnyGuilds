package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Locale;

public class ExcInfo implements Executor {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();
        String tag = null;

        if (args.length > 0) {
            tag = args[0];
        } else if (sender instanceof Player) {
            User user = User.get((Player) sender);

            if (user.hasGuild()) {
                tag = user.getGuild().getTag();
            }
        }

        if (tag == null || tag.isEmpty()) {
            sender.sendMessage(messages.infoTag);
            return;
        }

        if (!GuildUtils.tagExists(tag)) {
            sender.sendMessage(messages.infoExists);
            return;
        }

        Guild guild = GuildUtils.getByTag(tag);

        if (guild == null) {
            sender.sendMessage(messages.infoExists);
            return;
        }

        String validity = config.dateFormat.format(new Date(guild.getValidity()));
        
        long now = System.currentTimeMillis();
        long protectionEndTime = guild.getProtectionEndTime();
        long additionalProtectionEndTime = guild.getAdditionalProtectionEndTime();

        for (String messageLine : messages.infoList) {
            messageLine = StringUtils.replace(messageLine, "{GUILD}", guild.getName());
            messageLine = StringUtils.replace(messageLine, "{TAG}", guild.getTag());
            messageLine = StringUtils.replace(messageLine, "{OWNER}", guild.getOwner().getName());
            messageLine = StringUtils.replace(messageLine, "{MEMBERS-ONLINE}", String.valueOf(guild.getOnlineMembers().size()));
            messageLine = StringUtils.replace(messageLine, "{MEMBERS-ALL}", String.valueOf(guild.getMembers().size()));
            messageLine = StringUtils.replace(messageLine, "{MEMBERS}", ChatUtils.toString(UserUtils.getOnlineNames(guild.getMembers()), true));
            messageLine = StringUtils.replace(messageLine, "{DEPUTIES}", ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), true));
            messageLine = StringUtils.replace(messageLine, "{REGION-SIZE}", config.regionsEnabled ? String.valueOf(guild.getRegion().getSize()) : messages.gRegionSizeNoValue);
            messageLine = StringUtils.replace(messageLine, "{GUILD-PROTECTION}", protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now));
            messageLine = StringUtils.replace(messageLine, "{GUILD-ADDITIONAL-PROTECTION}", additionalProtectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(additionalProtectionEndTime - now));
            
            Rank rank = guild.getRank();
            messageLine = StringUtils.replace(messageLine, "{POINTS-FORMAT}", IntegerRange.inRange(rank.getPoints(), config.pointsFormat, "POINTS"));
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(rank.getPoints()));
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(rank.getKills()));
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(rank.getDeaths()));
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()));
            messageLine = StringUtils.replace(messageLine, "{VALIDITY}", validity);
            messageLine = StringUtils.replace(messageLine, "{LIVES}", Integer.toString(guild.getLives()));
            
            if (guild.getMembers().size() >= config.minMembersToInclude) {
                messageLine = StringUtils.replace(messageLine, "{RANK}", String.valueOf(rank.getPosition()));
            } else {
                messageLine = StringUtils.replace(messageLine, "{RANK}", messages.minMembersToIncludeNoValue);
            }
            
            if (!guild.getAllies().isEmpty()) {
                messageLine = StringUtils.replace(messageLine, "{ALLIES}", ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), true));
            } else {
                messageLine = StringUtils.replace(messageLine, "{ALLIES}", messages.alliesNoValue);
            }
            
            if (messageLine.contains("<online>")) {
                String color = ChatColor.getLastColors(messageLine.split("<online>")[0]);
                messageLine = StringUtils.replace(messageLine, "<online>", ChatColor.GREEN + "");
                messageLine = StringUtils.replace(messageLine, "</online>", color);
            }
            
            sender.sendMessage(messageLine);
        }
    }

}
