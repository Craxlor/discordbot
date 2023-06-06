package com.github.craxlor.discordbot.database.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;

public class DBAutoroomTriggerHandler implements EntityHandler<AutoroomTrigger>{

    @Override
    public boolean insert(AutoroomTrigger entity) {
        String sql = "INSERT INTO autoroomTriggers(trigger_id, category_id, naming_pattern, inheritance) VALUES(?,?,?,?)";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, entity.getTrigger_id());
            preparedStatement.setLong(2, entity.getCategory_id());
            preparedStatement.setString(3, entity.getNaming_pattern());
            preparedStatement.setString(4, entity.getInheritance());
            preparedStatement.executeUpdate();
            logger.info("inserted new autoroomTrigger");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public AutoroomTrigger getEntity(long id) {
        String sql = "SELECT * FROM autoroomTriggers WHERE trigger_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return map(resultSet);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(AutoroomTrigger entity) {
        String sql_c = "UPDATE autoroomTriggers SET category_id = ? WHERE trigger_id = ?";
        String sql_n = "UPDATE autoroomTriggers SET naming_pattern = ? WHERE trigger_id = ?";
        String sql_i = "UPDATE autoroomTriggers SET inheritance = ? WHERE trigger_id = ?";
        try {
            PreparedStatement preparedStatement;
            if (entity.getCategory_id() > -1) {
                preparedStatement = prepareStatement(sql_c);
                preparedStatement.setLong(1, entity.getCategory_id());
                preparedStatement.executeUpdate();
            }
            if (entity.getNaming_pattern() != null) {
                preparedStatement = prepareStatement(sql_n);
                preparedStatement.setString(1, entity.getNaming_pattern());
                preparedStatement.executeUpdate();
            }
            if (entity.getInheritance() != null) {
                preparedStatement = prepareStatement(sql_i);
                preparedStatement.setString(1, entity.getInheritance());
                preparedStatement.executeUpdate();
            }
            logger.info("updated guild");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean remove(long id) {
        String sql = "DELETE FROM autoroomTriggers WHERE trigger_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            logger.info("deleted an autoroomTrigger");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    
    }

    @Override
    public AutoroomTrigger mapFrom(ResultSet rs) throws SQLException {
        AutoroomTrigger autoroomTrigger = new AutoroomTrigger();
        autoroomTrigger.setCategory_id(rs.getLong("category_id"));
        autoroomTrigger.setInheritance(rs.getString("inheritance"));
        autoroomTrigger.setNaming_pattern(rs.getString("naming_pattern"));
        autoroomTrigger.setTrigger_id(rs.getLong("trigger_id"));
        return autoroomTrigger;
    }
    
}
