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
import ru.mygroup.isfilms.model.FilterDTO;
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

            List<Movie> found = movieService.searchByTitle(query);
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

    private FilterDTO currentFilter = null;
    @FXML
    private void onFilters() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/filter-view.fxml"));
            Parent root = loader.load();

            FilterController controller = loader.getController();
            controller.setFilter(currentFilter);
            controller.setOnSearchCallback(() -> {
                currentFilter = controller.getFilter();
                applyFilters();
            });

            Stage stage = new Stage();
            stage.setTitle("Фильтры");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(filmContainer.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть окно фильтров");
        }
    }

    private void applyFilters() {
        if (currentFilter != null && !currentFilter.isEmpty()) {
            List<Movie> filtered = movieService.searchByFilter(currentFilter);
            displayMovies(filtered);
        } else {
            loadAllMovies();
        }
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

    @FXML
    public void onReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/mygroup/isfilms/report-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Сводные отчеты");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(filmContainer.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Не удалось открыть окно отчетов");
        }
    }
}