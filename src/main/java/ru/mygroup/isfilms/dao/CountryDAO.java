package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.Country;
import java.sql.*;

public class CountryDAO extends AbstractDAO<Country, Integer> {

    @Override
    protected String getTableName() {
        return "cinema.countries";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected Country mapResultSetToEntity(ResultSet rs) throws SQLException {
        Country country = new Country();
        country.setId(rs.getInt("id"));
        country.setName(rs.getString("name"));
        return country;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Country country, boolean includeId) throws SQLException {
        int index = 1;
        if (includeId) {
            stmt.setInt(index++, country.getId());
        }
        stmt.setString(index++, country.getName());
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO cinema.countries (name) VALUES (?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE cinema.countries SET name=? WHERE id=?";
    }

    @Override
    protected void setId(Country country, Object id) {
        country.setId((Integer) id);
    }

    public Country findByName(String name) {
        String sql = "SELECT * FROM cinema.countries WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска страны по имени", e);
        }
    }
}