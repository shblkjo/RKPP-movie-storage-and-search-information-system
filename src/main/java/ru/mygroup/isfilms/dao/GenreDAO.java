package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.Genre;
import java.sql.*;

public class GenreDAO extends AbstractDAO<Genre, Integer> {

    @Override
    protected String getTableName() {
        return "cinema.genres";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected Genre mapResultSetToEntity(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        genre.setDescription(rs.getString("description"));
        return genre;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Genre genre, boolean includeId) throws SQLException {
        int index = 1;
        if (includeId) {
            stmt.setInt(index++, genre.getId());
        }
        stmt.setString(index++, genre.getName());
        stmt.setString(index++, genre.getDescription());
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO cinema.genres (name, description) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE cinema.genres SET name=?, description=? WHERE id=?";
    }

    @Override
    protected void setId(Genre genre, Object id) {
        genre.setId((Integer) id);
    }

    public Genre findByName(String name) {
        String sql = "SELECT * FROM cinema.genres WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска жанра по имени", e);
        }
    }
}