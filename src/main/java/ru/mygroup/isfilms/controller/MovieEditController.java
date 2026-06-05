package ru.mygroup.isfilms.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.*;
import ru.mygroup.isfilms.dao.*;
import ru.mygroup.isfilms.service.MovieService;

import java.util.List;

public class MovieEditController {

    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField durationField;
    @FXML private TextField ratingField;
    @FXML private TextField ageRatingField;
    @FXML private ComboBox<Country> countryCombo;
    @FXML private ComboBox<Studio> studioCombo;
    @FXML private ListView<Genre> genresList;
    @FXML private ListView<Person> directorsList;
    @FXML private ListView<Person> actorsList;
    @FXML private TextArea descriptionArea;

    private Movie movie;
    private final MovieService movieService = new MovieService();
    private final GenreDAO genreDAO = new GenreDAO();
    private final CountryDAO countryDAO = new CountryDAO();
    private final StudioDAO studioDAO = new StudioDAO();
    private final PersonDAO personDAO = new PersonDAO();

    @FXML
    public void initialize() {
        // Заполняем выпадающие списки
        typeCombo.setItems(FXCollections.observableArrayList("movie", "series"));
        countryCombo.setItems(FXCollections.observableArrayList(countryDAO.findAll()));
        studioCombo.setItems(FXCollections.observableArrayList(studioDAO.findAll()));

        // Заполняем списки с возможностью множественного выбора
        genresList.setItems(FXCollections.observableArrayList(genreDAO.findAll()));
        directorsList.setItems(FXCollections.observableArrayList(personDAO.findAll()));
        actorsList.setItems(FXCollections.observableArrayList(personDAO.findAll()));

        // Включаем множественный выбор
        genresList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        directorsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        actorsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        if (movie != null) {
            titleField.setText(movie.getTitle());
            yearField.setText(String.valueOf(movie.getReleaseYear()));
            typeCombo.setValue(movie.getType());
            durationField.setText(movie.getDuration() != null ? String.valueOf(movie.getDuration()) : "");
            ratingField.setText(movie.getRating() != null ? String.valueOf(movie.getRating()) : "");
            ageRatingField.setText(movie.getAgeRating());
            descriptionArea.setText(movie.getDescription());
            countryCombo.setValue(movie.getCountry());
            studioCombo.setValue(movie.getStudio());

            // Выделяем выбранные жанры
            if (movie.getGenres() != null) {
                for (Genre genre : movie.getGenres()) {
                    genresList.getSelectionModel().select(genre);
                }
            }

            // Выделяем выбранных режиссеров
            if (movie.getDirectors() != null) {
                for (Person director : movie.getDirectors()) {
                    directorsList.getSelectionModel().select(director);
                }
            }

            // Выделяем выбранных актеров
            if (movie.getActors() != null) {
                for (Person actor : movie.getActors()) {
                    actorsList.getSelectionModel().select(actor);
                }
            }
        }
    }

    @FXML
    private void onSave() {
        try {
            // Проверка обязательных полей
            if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
                showError("Введите название фильма");
                return;
            }
            if (yearField.getText() == null || yearField.getText().trim().isEmpty()) {
                showError("Введите год выпуска");
                return;
            }
            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch (NumberFormatException e) {
                showError("Год должен быть числом");
                return;
            }
            if (typeCombo.getValue() == null) {
                showError("Выберите тип фильма");
                return;
            }
            // Создаем фильм
            Movie newMovie = new Movie();
            newMovie.setTitle(titleField.getText().trim());
            newMovie.setReleaseYear(year);
            newMovie.setType(typeCombo.getValue());
            // Необязательные поля
            if (!durationField.getText().trim().isEmpty()) {
                try {
                    newMovie.setDuration(Integer.parseInt(durationField.getText()));
                } catch (NumberFormatException e) {
                    showError("Длительность должна быть числом");
                    return;
                }
            }

            if (!ratingField.getText().trim().isEmpty()) {
                try {
                    double rating = Double.parseDouble(ratingField.getText());
                    if (rating < 0 || rating > 10) {
                        showError("Рейтинг должен быть от 0 до 10");
                        return;
                    }
                    newMovie.setRating(rating);
                } catch (NumberFormatException e) {
                    showError("Рейтинг должен быть числом");
                    return;
                }
            }

            newMovie.setAgeRating(ageRatingField.getText());
            newMovie.setDescription(descriptionArea.getText());
            newMovie.setCountry(countryCombo.getValue());
            newMovie.setStudio(studioCombo.getValue());
            newMovie.setGenres(genresList.getSelectionModel().getSelectedItems());
            newMovie.setDirectors(directorsList.getSelectionModel().getSelectedItems());
            newMovie.setActors(actorsList.getSelectionModel().getSelectedItems());

            if (movie == null) {
                // Создание нового фильма
                movieService.addMovie(newMovie);
                showInfo("Фильм успешно добавлен");
            } else {
                // Обновление существующего
                newMovie.setId(movie.getId());
                movieService.updateMovie(newMovie);
                showInfo("Фильм успешно обновлен");
            }

            // Закрываем окно
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showError("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}