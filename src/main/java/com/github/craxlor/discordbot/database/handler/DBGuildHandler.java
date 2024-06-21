package com.github.craxlor.discordbot.database.handler;

import org.hibernate.Session;

import com.github.craxlor.discordbot.database.entity.Guild;

public class DBGuildHandler implements EntityHandler<Guild> {

    @Override
    public Guild getEntity(Session session,long id) {
        Guild guild = session.get(Guild.class, id);
        return guild;
    }

    @Override
    public void remove(Session session,long id) {
        Guild guild = (Guild) session.get(Guild.class, id);
        session.remove(guild);
    }

}
