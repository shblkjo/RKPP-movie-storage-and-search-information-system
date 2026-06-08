package ru.mygroup.isfilms.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.*;
import ru.mygroup.isfilms.dao.*;
import ru.mygroup.isfilms.service.MovieService;

import java.util.List;

public class MovieEditController {

    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private TextField durationField;
    @FXML private Slider ratingSlider;
    @FXML private Label ratingLabel;
    @FXML private ComboBox<String> ageRatingCombo;
    @FXML private ComboBox<Country> countryCombo;
    @FXML private ComboBox<Studio> studioCombo;
    @FXML private ListView<Genre> genresList;
    @FXML private ListView<Person> directorsList;
    @FXML private ListView<Person> actorsList;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView posterPreview;

    private Movie movie;
    private byte[] posterBytes;

    private final MovieService movieService = new MovieService();
    private final GenreDAO genreDAO = new GenreDAO();
    private final CountryDAO countryDAO = new CountryDAO();
    private final StudioDAO studioDAO = new StudioDAO();
    private final PersonDAO personDAO = new PersonDAO();

    @FXML
    public void initialize() {
        // Настройка слайдера рейтинга
        ratingSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            ratingLabel.setText(String.format("Рейтинг: %.1f", newVal.doubleValue()));
        });

        // Заполняем выпадающие списки
        countryCombo.setItems(FXCollections.observableArrayList(countryDAO.findAll()));
        studioCombo.setItems(FXCollections.observableArrayList(studioDAO.findAll()));
        ageRatingCombo.setItems(FXCollections.observableArrayList("0+", "6+", "12+", "16+", "18+"));

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
            durationField.setText(movie.getDuration() != null ? String.valueOf(movie.getDuration()) : "");

            // Slider
            double rating = movie.getRating() != null ? movie.getRating() : 0.0;
            ratingSlider.setValue(rating);
            ratingLabel.setText(String.format("Рейтинг: %.1f", rating));

            // ComboBox
            ageRatingCombo.setValue(movie.getAgeRating());
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

            // Постер
            if (movie.getPosterImage() != null && movie.getPosterImage().length > 0) {
                posterBytes = movie.getPosterImage();
                javafx.scene.image.Image image = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(posterBytes));
                posterPreview.setImage(image);
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

            // Создаем фильм
            Movie newMovie = new Movie();
            newMovie.setTitle(titleField.getText().trim());
            newMovie.setReleaseYear(year);

            // Необязательные поля
            if (!durationField.getText().trim().isEmpty()) {
                try {
                    newMovie.setDuration(Integer.parseInt(durationField.getText()));
                } catch (NumberFormatException e) {
                    showError("Длительность должна быть числом");
                    return;
                }
            }

            // Рейтинг из Slider
            newMovie.setRating(ratingSlider.getValue());

            newMovie.setAgeRating(ageRatingCombo.getValue());
            newMovie.setDescription(descriptionArea.getText());
            newMovie.setCountry(countryCombo.getValue());
            newMovie.setStudio(studioCombo.getValue());
            newMovie.setGenres(genresList.getSelectionModel().getSelectedItems());
            newMovie.setDirectors(directorsList.getSelectionModel().getSelectedItems());
            newMovie.setActors(actorsList.getSelectionModel().getSelectedItems());
            newMovie.setPosterImage(posterBytes);

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

    @FXML
    private void onChooseImage(ActionEvent actionEvent) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Выберите постер");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg")
        );
        java.io.File file = fileChooser.showOpenDialog(posterPreview.getScene().getWindow());
        if (file != null) {
            try {
                posterBytes = java.nio.file.Files.readAllBytes(file.toPath());
                javafx.scene.image.Image image = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(posterBytes));
                posterPreview.setImage(image);
            } catch (java.io.IOException e) {
                showError("Не удалось загрузить изображение");
            }
        }
    }

    @FXML
    private void onClearImage(ActionEvent actionEvent) {
        posterBytes = null;
        posterPreview.setImage(null);
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