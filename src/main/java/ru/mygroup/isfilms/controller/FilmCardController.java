package ru.mygroup.isfilms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.Movie;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FilmCardController {

    @FXML
    private ImageView posterImage;

    @FXML
    private Text titleText;

    @FXML
    private Text genreText;

    @FXML
    private Text yearText;

    @FXML
    private Text ratingText;

    private Movie movie;

    public void setMovie(Movie movie) {
        this.movie = movie;
        titleText.setText(movie.getTitle());
        yearText.setText("Год: " + movie.getReleaseYear());

        if (movie.getRating() != null) {
            ratingText.setText("Рейтинг: " + movie.getRating());
        } else {
            ratingText.setText("Рейтинг: нет");
        }

        // Жанры
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            StringBuilder genres = new StringBuilder();
            for (int i = 0; i < movie.getGenres().size() && i < 3; i++) {
                if (i > 0) genres.append(", ");
                genres.append(movie.getGenres().get(i).getName());
            }
            genreText.setText(genres.toString());
        } else {
            genreText.setText("Жанр не указан");
        }

        // Постер
        if (movie.getPosterImage() != null && movie.getPosterImage().length > 0) {
            Image image = new Image(new ByteArrayInputStream(movie.getPosterImage()));
            posterImage.setImage(image);
        }
    }

    @FXML
    private void onMoreDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/movie-detail-view.fxml"));
            Parent root = loader.load();

            MovieDetailController controller = loader.getController();
            controller.setMovie(movie);

            Stage stage = new Stage();
            stage.setTitle("Детали фильма: " + movie.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(posterImage.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть детали фильма");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}