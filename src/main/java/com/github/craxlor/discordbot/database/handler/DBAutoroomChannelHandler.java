package com.github.craxlor.discordbot.database.handler;

import java.util.List;

import javax.annotation.Nullable;

import org.hibernate.Session;

import com.github.craxlor.discordbot.database.entity.AutoroomChannel;
import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;
import com.github.craxlor.discordbot.database.entity.Guild;

public class DBAutoroomChannelHandler implements EntityHandler<AutoroomChannel> {

    @Override
    public AutoroomChannel getEntity(Session session, long id) {
        return session.get(AutoroomChannel.class, id);
    }

    @Override
    public void remove(Session session, long id) {
        AutoroomChannel autoroomChannel = (AutoroomChannel) session.get(AutoroomChannel.class, id);
        session.remove(autoroomChannel);
    }

    public List<AutoroomChannel> getAutoroomChannelsByAutoroomTrigger(Session session,long trigger_id) {
        AutoroomTrigger autoroomTrigger = (AutoroomTrigger) session.get(AutoroomTrigger.class, trigger_id);
        return autoroomTrigger != null ? autoroomTrigger.getAutoroomChannels() : null;
    }

    @Nullable
    public List<AutoroomChannel> getAutoroomChannelsByGuild(Session session,long guild_id) {
        Guild guild = (Guild) session.get(Guild.class, guild_id);
        return guild != null ? guild.getAutoroomChannels() : null;
    }

}
