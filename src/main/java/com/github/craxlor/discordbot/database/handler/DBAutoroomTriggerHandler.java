package com.github.craxlor.discordbot.database.handler;

import org.hibernate.Session;

import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;

public class DBAutoroomTriggerHandler implements EntityHandler<AutoroomTrigger> {

    @Override
    public AutoroomTrigger getEntity(Session session,long id) {
        AutoroomTrigger autoroomTrigger = session.get(AutoroomTrigger.class, id);
        return autoroomTrigger;
    }

    @Override
    public void remove(Session session,long id) {
        AutoroomTrigger autoroomTrigger = (AutoroomTrigger) session.get(AutoroomTrigger.class, id);
        session.remove(autoroomTrigger);
    }

}
