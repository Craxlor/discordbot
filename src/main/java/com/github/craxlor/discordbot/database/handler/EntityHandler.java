package com.github.craxlor.discordbot.database.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.craxlor.discordbot.database.Database;

public interface EntityHandler<Entity> {
    final Logger logger = LoggerFactory.getLogger("database");

    public abstract boolean insert(Entity entity);

    public abstract Entity getEntity(long id);

    public abstract boolean update(Entity entity);

    public abstract boolean remove(long id);

    abstract Entity mapFrom(ResultSet rs) throws SQLException;

    default public List<Entity> mapToList(ResultSet rs) throws SQLException {
        List<Entity> entities = new ArrayList<>();
        while (rs.next()) {
            entities.add(mapFrom(rs));
        }
        return entities;
    }

    default public Entity map(ResultSet rs) throws SQLException {
        if (rs.isBeforeFirst() == false)
            return null;
        else
            return mapFrom(rs);
    }

    default PreparedStatement prepareStatement(String sql) throws SQLException {
        return Database.getInstance().getConnection().prepareStatement(sql);
    }
}
