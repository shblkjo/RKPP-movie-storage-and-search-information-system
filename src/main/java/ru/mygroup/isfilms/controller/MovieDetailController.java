package ru.mygroup.isfilms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.Movie;
import ru.mygroup.isfilms.model.Person;
import ru.mygroup.isfilms.model.Genre;
import ru.mygroup.isfilms.service.MovieService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class MovieDetailController {

    @FXML private ImageView posterImage;
    @FXML private Text titleText;
    @FXML private Text yearText;
    @FXML private Text countryText;
    @FXML private Text studioText;
    @FXML private Text ratingText;
    @FXML private Text durationText;
    @FXML private Text ageRatingText;
    @FXML private Text typeText;
    @FXML private TextArea descriptionArea;
    @FXML private FlowPane genresFlow;
    @FXML private ListView<Person> directorsList;
    @FXML private ListView<Person> actorsList;

    private Movie movie;
    private final MovieService movieService = new MovieService();

    public void setMovie(Movie movie) {
        this.movie = movie;

        // Основная информация
        titleText.setText(movie.getTitle());
        yearText.setText("Год: " + movie.getReleaseYear());

        if (movie.getCountry() != null) {
            countryText.setText("Страна: " + movie.getCountry().getName());
        } else {
            countryText.setText("Страна: не указана");
        }

        if (movie.getStudio() != null) {
            studioText.setText("Студия: " + movie.getStudio().getName());
        } else {
            studioText.setText("Студия: не указана");
        }

        if (movie.getRating() != null) {
            ratingText.setText("Рейтинг: " + movie.getRating());
        } else {
            ratingText.setText("Рейтинг: нет");
        }

        if (movie.getDuration() != null) {
            durationText.setText("Длительность: " + movie.getDuration() + " мин");
        } else {
            durationText.setText("Длительность: не указана");
        }

        ageRatingText.setText("Возрастной рейтинг: " + (movie.getAgeRating() != null ? movie.getAgeRating() : "не указан"));
        descriptionArea.setText(movie.getDescription() != null ? movie.getDescription() : "");

        // Постер
        if (movie.getPosterImage() != null && movie.getPosterImage().length > 0) {
            Image image = new Image(new ByteArrayInputStream(movie.getPosterImage()));
            posterImage.setImage(image);
        }

        // Жанры
        genresFlow.getChildren().clear();
        if (movie.getGenres() != null) {
            for (Genre genre : movie.getGenres()) {
                Text genreText = new Text(genre.getName());
                genreText.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 2 8 2 8; -fx-border-radius: 10;");
                genresFlow.getChildren().add(genreText);
            }
        }

        // Режиссеры
        if (movie.getDirectors() != null) {
            directorsList.getItems().setAll(movie.getDirectors());
        }

        // Актеры
        if (movie.getActors() != null) {
            actorsList.getItems().setAll(movie.getActors());
        }
    }

    @FXML
    private void onEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/movie-edit-view.fxml"));
            Parent root = loader.load();

            MovieEditController controller = loader.getController();
            controller.setMovie(movie);

            Stage stage = new Stage();
            stage.setTitle("Редактирование фильма");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(posterImage.getScene().getWindow());
            stage.showAndWait();

            // После закрытия окна редактирования обновляем данные
            refreshMovie();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть окно редактирования");
        }
    }

    private void refreshMovie() {
        Optional<Movie> refreshed = movieService.getMovieById(movie.getId());
        if (refreshed.isPresent()) {
            movie = refreshed.get();
            setMovie(movie);
        }
    }

    @FXML
    private void onDelete() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Вы уверены?");
        confirmAlert.setContentText("Удалить фильм \"" + movie.getTitle() + "\"?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                movieService.deleteMovie(movie.getId());
                showInfo("Фильм успешно удален");
                Stage stage = (Stage) posterImage.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                showError("Ошибка удаления: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onAddToCollection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/collection-view.fxml"));
            Parent root = loader.load();

            CollectionController controller = loader.getController();
            controller.setMovieToAdd(movie);

            Stage stage = new Stage();
            stage.setTitle("Добавить в подборку");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(posterImage.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть окно подборок");
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) posterImage.getScene().getWindow();
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