package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.*;
import java.sql.*;
import java.util.*;
import java.sql.Date;

public class MovieDAO extends AbstractDAO<Movie, Integer> {

    @Override
    protected String getTableName() {
        return "cinema.movies";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected Movie mapResultSetToEntity(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setReleaseYear(rs.getInt("release_year"));
        movie.setDescription(rs.getString("description"));
        movie.setDuration(rs.getInt("duration"));
        movie.setRating(rs.getDouble("rating"));
        movie.setAgeRating(rs.getString("age_rating"));
        movie.setPosterImage(rs.getBytes("poster_image"));
        return movie;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Movie movie, boolean includeId) throws SQLException {
        int index = 1;
        if (includeId) {
            stmt.setInt(index++, movie.getId());
        }
        stmt.setString(index++, movie.getTitle());
        stmt.setInt(index++, movie.getReleaseYear());
        stmt.setString(index++, movie.getDescription());
        stmt.setObject(index++, movie.getDuration(), Types.INTEGER);
        stmt.setObject(index++, movie.getCountry() != null ? movie.getCountry().getId() : null, Types.INTEGER);
        stmt.setObject(index++, movie.getStudio() != null ? movie.getStudio().getId() : null, Types.INTEGER);
        stmt.setDouble(index++, movie.getRating() != null ? movie.getRating() : 0.0);
        stmt.setString(index++, movie.getAgeRating());
        stmt.setBytes(index++, movie.getPosterImage());
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO cinema.movies (title, release_year, description, duration, " +
                "country_id, studio_id, rating, age_rating, poster_image) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE cinema.movies SET title=?, release_year=?, description=?, duration=?, " +
                "country_id=?, studio_id=?, rating=?, age_rating=?, poster_image=? " +
                "WHERE id=?";
    }

    @Override
    protected void setId(Movie movie, Object id) {
        movie.setId((Integer) id);
    }

    public List<Movie> findByTitle(String title) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM cinema.movies WHERE LOWER(title) LIKE LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по названию", e);
        }
        return movies;
    }

    public List<Movie> findByYear(Integer year) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM cinema.movies WHERE release_year = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по году", e);
        }
        return movies;
    }

    public List<Movie> findByGenre(Integer genreId) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT m.* FROM cinema.movies m " +
                "JOIN cinema.movie_genres mg ON m.id = mg.movie_id " +
                "WHERE mg.genre_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по жанру", e);
        }
        return movies;
    }

    public List<Movie> findByCountry(Integer countryId) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM cinema.movies WHERE country_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, countryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по стране", e);
        }
        return movies;
    }

    public List<Person> getActorsForMovie(Integer movieId) {
        List<Person> actors = new ArrayList<>();
        String sql = "SELECT p.* FROM cinema.people p " +
                "JOIN cinema.movie_people mp ON p.id = mp.person_id " +
                "WHERE mp.movie_id = ? AND mp.role_type = 'ACTOR'";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Person person = new Person();
                person.setId(rs.getInt("id"));
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                person.setMiddleName(rs.getString("middle_name"));
                Date birthDate = rs.getDate("birth_date");
                if (birthDate != null) {
                    person.setBirthDate(birthDate.toLocalDate());
                }
                person.setProfession(rs.getString("profession"));
                person.setBiography(rs.getString("biography"));
                actors.add(person);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки актеров", e);
        }
        return actors;
    }

    public List<Person> getDirectorsForMovie(Integer movieId) {
        List<Person> directors = new ArrayList<>();
        String sql = "SELECT p.* FROM cinema.people p " +
                "JOIN cinema.movie_people mp ON p.id = mp.person_id " +
                "WHERE mp.movie_id = ? AND mp.role_type = 'DIRECTOR'";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Person person = new Person();
                person.setId(rs.getInt("id"));
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                person.setMiddleName(rs.getString("middle_name"));
                Date birthDate = rs.getDate("birth_date");
                if (birthDate != null) {
                    person.setBirthDate(birthDate.toLocalDate());
                }
                person.setProfession(rs.getString("profession"));
                person.setBiography(rs.getString("biography"));
                directors.add(person);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки режиссеров", e);
        }
        return directors;
    }

    public List<Genre> getGenresForMovie(Integer movieId) {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT g.* FROM cinema.genres g " +
                "JOIN cinema.movie_genres mg ON g.id = mg.genre_id " +
                "WHERE mg.movie_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки жанров", e);
        }
        return genres;
    }

    public void addGenreToMovie(Integer movieId, Integer genreId) {
        String sql = "INSERT INTO cinema.movie_genres (movie_id, genre_id) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления жанра к фильму", e);
        }
    }

    public void addPersonToMovie(Integer movieId, Integer personId, String roleType) {
        String sql = "INSERT INTO cinema.movie_people (movie_id, person_id, role_type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, personId);
            stmt.setString(3, roleType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления человека к фильму", e);
        }

    }

    public void clearAllGenresFromMovie(Integer movieId) {
        String sql = "DELETE FROM cinema.movie_genres WHERE movie_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки жанров", e);
        }
    }

    public void clearAllPeopleFromMovie(Integer movieId, String roleType) {
        String sql = "DELETE FROM cinema.movie_people WHERE movie_id = ? AND role_type = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setString(2, roleType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки людей", e);
        }
    }

    @Override
    public boolean update(Movie movie) {
        String sql = getUpdateSql();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            int index = 1;
            stmt.setString(index++, movie.getTitle());
            stmt.setInt(index++, movie.getReleaseYear());
            stmt.setString(index++, movie.getDescription());
            stmt.setObject(index++, movie.getDuration(), Types.INTEGER);
            stmt.setObject(index++, movie.getCountry() != null ? movie.getCountry().getId() : null, Types.INTEGER);
            stmt.setObject(index++, movie.getStudio() != null ? movie.getStudio().getId() : null, Types.INTEGER);
            stmt.setDouble(index++, movie.getRating() != null ? movie.getRating() : 0.0);
            stmt.setString(index++, movie.getAgeRating());
            stmt.setBytes(index++, movie.getPosterImage());
            stmt.setInt(index, movie.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления фильма: " + e.getMessage(), e);
        }
    }
}