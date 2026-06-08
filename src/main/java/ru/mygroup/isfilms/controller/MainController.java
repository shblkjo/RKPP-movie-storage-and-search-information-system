package ru.mygroup.isfilms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.Movie;
import ru.mygroup.isfilms.service.MovieService;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private TextField SearchTF;

    @FXML
    private FlowPane filmContainer;

    private MovieService movieService;
    private List<Movie> allMovies;

    @FXML
    public void initialize() {
        movieService = new MovieService();
        loadAllMovies();
    }

    private void loadAllMovies() {
        allMovies = movieService.getAllMovies();
        displayMovies(allMovies);
    }

    private void displayMovies(List<Movie> movies) {
        filmContainer.getChildren().clear();

        for (Movie movie : movies) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/film-view.fxml"));
                Parent card = loader.load();

                FilmCardController cardController = loader.getController();
                cardController.setMovie(movie);

                filmContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
                showError("Ошибка загрузки карточки фильма");
            }
        }
    }

    @FXML
    private void onSearch() {
        String query = SearchTF.getText();
        if (query == null || query.trim().isEmpty()) {
            loadAllMovies();
        } else {

            List<Movie> found = movieService.searchMovies(query, null, null, null);
            displayMovies(found);
        }
    }

    @FXML
    private void onUpdate() {
        loadAllMovies();
    }

    @FXML
    private void onMyCollections() {
        openCollectionWindow();
    }

    @FXML
    private void onFilters() {
        showFilterDialog();
    }

    @FXML
    private void onExit() {
        Stage stage = (Stage) filmContainer.getScene().getWindow();
        stage.close();
    }

    private void openCollectionWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/collection-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Мои подборки");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(filmContainer.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть окно подборок");
        }
    }

    private void showFilterDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Фильтры");
        alert.setHeaderText("Фильтрация");
        alert.setContentText("Введите текст для поиска в поле выше");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onAddMovie(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/movie-edit-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Добавление фильма");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(filmContainer.getScene().getWindow());
            stage.showAndWait();

            loadAllMovies(); // обновляем список
        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть окно добавления");
        }
    }
}