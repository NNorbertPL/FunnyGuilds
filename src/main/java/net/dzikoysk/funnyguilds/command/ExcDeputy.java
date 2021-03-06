package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDeputy implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User owner = User.get(player);

        if (!owner.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!owner.isOwner()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.generalNoNickGiven);
            return;
        }

        String name = args[0];
        
        if (!UserUtils.playedBefore(name)) {
            player.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        User deputyUser = User.get(name);
        
        if (owner.equals(deputyUser)) {
            player.sendMessage(messages.deputyMustBeDifferent);
            return;
        }

        Guild guild = owner.getGuild();
        Player deputyPlayer = deputyUser.getPlayer();

        if (!guild.getMembers().contains(deputyUser)) {
            player.sendMessage(messages.generalIsNotMember);
            return;
        }

        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(EventCause.USER, owner, guild, deputyUser))) {
            return;
        }
        
        if (deputyUser.isDeputy()) {
            guild.removeDeputy(deputyUser);;
            player.sendMessage(messages.deputyRemove);
            
            if (deputyPlayer != null) {
                deputyPlayer.sendMessage(messages.deputyMember);
            }

            return;
        }

        guild.addDeputy(deputyUser);
        player.sendMessage(messages.deputySet);
        
        if (deputyPlayer != null) {
            deputyPlayer.sendMessage(messages.deputyOwner);
        }
    }

}
