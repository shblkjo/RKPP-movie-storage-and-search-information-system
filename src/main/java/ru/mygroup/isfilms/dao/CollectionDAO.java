package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.Collection;
import ru.mygroup.isfilms.model.Movie;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class CollectionDAO extends AbstractDAO<Collection, Integer> {

    private final MovieDAO movieDAO = new MovieDAO();

    @Override
    protected String getTableName() {
        return "cinema.collections";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected Collection mapResultSetToEntity(ResultSet rs) throws SQLException {
        Collection collection = new Collection();
        collection.setId(rs.getInt("id"));
        collection.setTitle(rs.getString("title"));
        collection.setDescription(rs.getString("description"));
        collection.setPublic(rs.getBoolean("is_public"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            collection.setCreatedAt(createdAt.toLocalDateTime());
        } else {
            collection.setCreatedAt(LocalDateTime.now());
        }
        return collection;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Collection collection, boolean includeId) throws SQLException {
        int index = 1;
        if (includeId) {
            stmt.setInt(index++, collection.getId());
        }
        stmt.setString(index++, collection.getTitle());
        stmt.setString(index++, collection.getDescription());
        stmt.setBoolean(index++, collection.isPublic());
        LocalDateTime createdAt = collection.getCreatedAt();
        if (createdAt != null) {
            stmt.setTimestamp(index, Timestamp.valueOf(createdAt));
        } else {
            stmt.setTimestamp(index, Timestamp.valueOf(LocalDateTime.now()));
        }
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO cinema.collections (title, description, is_public, created_at) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE cinema.collections SET title=?, description=?, is_public=?, created_at=? WHERE id=?";
    }

    @Override
    protected void setId(Collection collection, Object id) {
        collection.setId((Integer) id);
    }

    public Collection findByTitle(String title) {
        String sql = "SELECT * FROM cinema.collections WHERE LOWER(title) = LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска подборки по названию", e);
        }
    }

    public List<Collection> findPublicCollections() {
        List<Collection> collections = new ArrayList<>();
        String sql = "SELECT * FROM cinema.collections WHERE is_public = true ORDER BY created_at DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                collections.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения публичных подборок", e);
        }
        return collections;
    }

    public void addMovieToCollection(Integer collectionId, Integer movieId) {
        String sql = "INSERT INTO cinema.collection_movies (collection_id, movie_id) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления фильма в подборку", e);
        }
    }

    public void removeMovieFromCollection(Integer collectionId, Integer movieId) {
        String sql = "DELETE FROM cinema.collection_movies WHERE collection_id = ? AND movie_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления фильма из подборки", e);
        }
    }

    public List<Movie> getMoviesInCollection(Integer collectionId) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT m.* FROM cinema.movies m " +
                "JOIN cinema.collection_movies cm ON m.id = cm.movie_id " +
                "WHERE cm.collection_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(movieDAO.mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения фильмов в подборке", e);
        }
        return movies;
    }

    public boolean isMovieInCollection(Integer collectionId, Integer movieId) {
        String sql = "SELECT 1 FROM cinema.collection_movies WHERE collection_id = ? AND movie_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки фильма в подборке", e);
        }
    }

    public int getMoviesCountInCollection(Integer collectionId) {
        String sql = "SELECT COUNT(*) FROM cinema.collection_movies WHERE collection_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчета фильмов в подборке", e);
        }
    }
}