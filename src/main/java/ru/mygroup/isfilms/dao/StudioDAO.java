package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.Studio;
import ru.mygroup.isfilms.model.Country;
import java.sql.*;

public class StudioDAO extends AbstractDAO<Studio, Integer> {

    @Override
    protected String getTableName() {
        return "cinema.studios";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected Studio mapResultSetToEntity(ResultSet rs) throws SQLException {
        Studio studio = new Studio();
        studio.setId(rs.getInt("id"));
        studio.setName(rs.getString("name"));
        studio.setFoundedYear(rs.getInt("founded_year"));
        studio.setAddress(rs.getString("address"));

        if (rs.getObject("country_id") != null) {
            Country country = new Country();
            country.setId(rs.getInt("country_id"));
            studio.setCountry(country);
        }
        return studio;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Studio studio, boolean includeId) throws SQLException {
        int index = 1;
        if (includeId) {
            stmt.setInt(index++, studio.getId());
        }
        stmt.setString(index++, studio.getName());
        stmt.setObject(index++, studio.getCountry() != null ? studio.getCountry().getId() : null, Types.INTEGER);
        stmt.setObject(index++, studio.getFoundedYear(), Types.INTEGER);
        stmt.setString(index++, studio.getAddress());
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO cinema.studios (name, country_id, founded_year, address) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE cinema.studios SET name=?, country_id=?, founded_year=?, address=? WHERE id=?";
    }

    @Override
    protected void setId(Studio studio, Object id) {
        studio.setId((Integer) id);
    }

    public Studio findByName(String name) {
        String sql = "SELECT * FROM cinema.studios WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска студии по имени", e);
        }
    }
}