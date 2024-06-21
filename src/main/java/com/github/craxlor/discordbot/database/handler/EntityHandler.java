package com.github.craxlor.discordbot.database.handler;

import javax.annotation.Nullable;

import org.hibernate.Session;

public interface EntityHandler<Entity> {

    public default void insert(Session session, Entity entity) {
        session.merge(entity);
    }

    @Nullable
    public abstract Entity getEntity(Session session, long id);

    public default void update(Session session, Entity entity) {
        insert(session, entity);
    }

    public abstract void remove(Session session, long id);
}
