package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.DBHelper;
import java.sql.*;
import java.util.*;

public abstract class AbstractDAO<T, ID> implements Dao<T, ID>  {

    protected Connection getConnection() {
        return DBHelper.getConnection();
    }

    protected abstract String getTableName();
    protected abstract String getIdColumnName();
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement stmt, T entity, boolean includeId) throws SQLException;
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();
    protected abstract void setId(T entity, Object id);

    public T save(T entity) {
        String sql = getInsertSql();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(stmt, entity, false);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                setId(entity, generatedKeys.getObject(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения в таблицу " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска в таблице " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " ORDER BY " + getIdColumnName();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка из таблицы " + getTableName() + ": " + e.getMessage(), e);
        }
        return entities;
    }

    public boolean update(T entity) {
        String sql = getUpdateSql();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            setInsertParameters(stmt, entity, true);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления в таблице " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    public boolean delete(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления из таблицы " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчета в таблице " + getTableName() + ": " + e.getMessage(), e);
        }
    }
}