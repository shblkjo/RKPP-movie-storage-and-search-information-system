package ru.mygroup.isfilms.service;

import ru.mygroup.isfilms.model.Movie;
import ru.mygroup.isfilms.model.Person;
import ru.mygroup.isfilms.model.Genre;
import ru.mygroup.isfilms.dao.MovieDAO;
import ru.mygroup.isfilms.dao.PersonDAO;
import ru.mygroup.isfilms.dao.GenreDAO;

import java.util.List;
import java.util.Optional;

public class MovieService {

    private final MovieDAO movieDAO;
    private final PersonDAO personDAO;
    private final GenreDAO genreDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
        this.personDAO = new PersonDAO();
        this.genreDAO = new GenreDAO();
    }
    public Movie addMovie(Movie movie) {
        validateMovie(movie);

        // Сохранение фильма
        Movie saved = movieDAO.save(movie);

        // сохранение связи с жанрами
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            for (Genre genre : movie.getGenres()) {
                movieDAO.addGenreToMovie(saved.getId(), genre.getId());
            }
        }

        // Сохранение связи с режиссерами
        if (movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
            for (Person director : movie.getDirectors()) {
                movieDAO.addPersonToMovie(saved.getId(), director.getId(), "DIRECTOR");
            }
        }

        // Сохранение связи с актерами
        if (movie.getActors() != null && !movie.getActors().isEmpty()) {
            for (Person actor : movie.getActors()) {
                movieDAO.addPersonToMovie(saved.getId(), actor.getId(), "ACTOR");
            }
        }

        return saved;
    }

    public Optional<Movie> getMovieById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректный ID фильма");
        }
        Optional<Movie> movieOpt = movieDAO.findById(id);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            // Загружаем связанные данные
            movie.setGenres(movieDAO.getGenresForMovie(id));
            movie.setDirectors(movieDAO.getDirectorsForMovie(id));
            movie.setActors(movieDAO.getActorsForMovie(id));
            return Optional.of(movie);
        }
        return Optional.empty();
    }
    public List<Movie> getAllMovies() {
        List<Movie> movies = movieDAO.findAll();
        // Для каждого фильма загружаем жанры
        for (Movie movie : movies) {
            movie.setGenres(movieDAO.getGenresForMovie(movie.getId()));
        }
        return movies;
    }

    public void updateMovie(Movie movie) {
        if (movie.getId() == null || movie.getId() <= 0) {
            throw new IllegalArgumentException("Невозможно обновить фильм без ID");
        }
        validateMovie(movie);

        // Обновляем основные данные фильма
        boolean updated = movieDAO.update(movie);
        if (!updated) {
            throw new RuntimeException("Фильм с ID " + movie.getId() + " не найден");
        }

        // Обновляем жанры (удалить старые, добавить новые)
        movieDAO.clearAllGenresFromMovie(movie.getId());
        if (movie.getGenres() != null) {
            for (Genre genre : movie.getGenres()) {
                movieDAO.addGenreToMovie(movie.getId(), genre.getId());
            }
        }

        // Обновляем режиссеров
        movieDAO.clearAllPeopleFromMovie(movie.getId(), "DIRECTOR");
        if (movie.getDirectors() != null) {
            for (Person director : movie.getDirectors()) {
                movieDAO.addPersonToMovie(movie.getId(), director.getId(), "DIRECTOR");
            }
        }

        // Обновляем актеров
        movieDAO.clearAllPeopleFromMovie(movie.getId(), "ACTOR");
        if (movie.getActors() != null) {
            for (Person actor : movie.getActors()) {
                movieDAO.addPersonToMovie(movie.getId(), actor.getId(), "ACTOR");
            }
        }
    }

    public void deleteMovie(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректный ID фильма");
        }

        boolean deleted = movieDAO.delete(id);
        if (!deleted) {
            throw new RuntimeException("Фильм с ID " + id + " не найден");
        }
    }
    //TODO  переделать с конкатенацией
    public List<Movie> searchMovies(String title, Integer year, Integer genreId, Integer countryId) {
        if ((title == null || title.trim().isEmpty()) && year == null && genreId == null && countryId == null) {
            return getAllMovies();
        }
        if (title != null && !title.trim().isEmpty()) {
            return movieDAO.findByTitle(title);
        }
        if (year != null) {
            return movieDAO.findByYear(year);
        }
        if (genreId != null) {
            return movieDAO.findByGenre(genreId);
        }
        if (countryId != null) {
            return movieDAO.findByCountry(countryId);
        }
        return getAllMovies();
    }

    public List<Movie> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllMovies();
        }
        return movieDAO.findByTitle(title);
    }

    public List<Movie> searchByYear(Integer year) {
        if (year == null) {
            return getAllMovies();
        }
        return movieDAO.findByYear(year);
    }

    public List<Movie> searchByGenre(Integer genreId) {
        if (genreId == null) {
            return getAllMovies();
        }
        return movieDAO.findByGenre(genreId);
    }

    // ОТЧЕТЫ

    public double getAverageRating() {
        List<Movie> movies = movieDAO.findAll();
        if (movies.isEmpty()) {
            return 0.0;
        }
        return movies.stream()
                .mapToDouble(m -> m.getRating() != null ? m.getRating() : 0.0)
                .average()
                .orElse(0.0);
    }

    public long getMoviesCount() {
        return movieDAO.count();
    }

    public long getMoviesCountByYear(Integer year) {
        if (year == null) {
            return 0;
        }
        return movieDAO.findByYear(year).size();
    }

    public long getMoviesCountByCountry(Integer countryId) {
        if (countryId == null) {
            return 0;
        }
        return movieDAO.findByCountry(countryId).size();
    }

    public List<Movie> getTopRatedMovies(int limit) {
        List<Movie> all = movieDAO.findAll();
        all.sort((a, b) -> {
            double ratingA = a.getRating() != null ? a.getRating() : 0;
            double ratingB = b.getRating() != null ? b.getRating() : 0;
            return Double.compare(ratingB, ratingA);
        });
        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
    }

    //  ВАЛИДАЦИЯ

    private void validateMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Фильм не может быть null");
        }

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название фильма не может быть пустым");
        }

        if (movie.getTitle().length() > 255) {
            throw new IllegalArgumentException("Название не может превышать 255 символов");
        }

        if (movie.getReleaseYear() == null) {
            throw new IllegalArgumentException("Год выпуска обязателен");
        }

        int currentYear = java.time.Year.now().getValue();
        if (movie.getReleaseYear() < 1888 || movie.getReleaseYear() > currentYear + 1) {
            throw new IllegalArgumentException("Год должен быть от 1888 до " + (currentYear + 1));
        }

        if (movie.getRating() != null && (movie.getRating() < 0 || movie.getRating() > 10)) {
            throw new IllegalArgumentException("Рейтинг должен быть от 0 до 10");
        }
    }
}