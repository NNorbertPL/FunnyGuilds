package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.user.User;

public class PointsChangeEvent extends RankChangeEvent {

    public PointsChangeEvent(EventCause eventCause, Rank rank, User doer, int change) {
        super(eventCause, rank, doer, change);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Points change has been cancelled by the server!";
    }
    
}
