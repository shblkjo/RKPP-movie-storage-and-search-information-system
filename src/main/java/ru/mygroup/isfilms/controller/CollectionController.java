package ru.mygroup.isfilms.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.Collection;
import ru.mygroup.isfilms.model.Movie;
import ru.mygroup.isfilms.dao.CollectionDAO;
import ru.mygroup.isfilms.dao.MovieDAO;

import java.util.List;
import java.util.Optional;

public class CollectionController {

    @FXML private TextField collectionNameField;
    @FXML private ListView<Collection> collectionsList;
    @FXML private ListView<Movie> moviesInCollectionList;

    private final CollectionDAO collectionDAO = new CollectionDAO();
    private final MovieDAO movieDAO = new MovieDAO();
    private Movie movieToAdd; // Фильм, который добавляем в подборку (если передан из MovieDetailController)

    @FXML
    public void initialize() {
        loadCollections();

        // При выборе подборки показываем фильмы в ней
        collectionsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMoviesInCollection(newVal.getId());
            }
        });
    }

    public void setMovieToAdd(Movie movie) {
        this.movieToAdd = movie;
        if (movie != null) {
            // Если передан фильм, подсвечиваем подборки, где он уже есть
            highlightCollectionsWithMovie();
        }
    }

    private void loadCollections() {
        List<Collection> collections = collectionDAO.findAll();
        collectionsList.setItems(FXCollections.observableArrayList(collections));
    }

    private void loadMoviesInCollection(Integer collectionId) {
        List<Movie> movies = collectionDAO.getMoviesInCollection(collectionId);
        moviesInCollectionList.setItems(FXCollections.observableArrayList(movies));
    }

    private void highlightCollectionsWithMovie() {
        // Подсвечиваем подборки, в которых уже есть этот фильм
        for (Collection collection : collectionsList.getItems()) {
            if (collectionDAO.isMovieInCollection(collection.getId(), movieToAdd.getId())) {
                collectionsList.getSelectionModel().select(collection);
                break;
            }
        }
    }

    @FXML
    private void onCreateCollection() {
        String title = collectionNameField.getText();
        if (title == null || title.trim().isEmpty()) {
            showError("Введите название подборки");
            return;
        }

        Collection collection = new Collection();
        collection.setTitle(title.trim());
        collection.setDescription("");
        collection.setPublic(true);

        Collection saved = collectionDAO.save(collection);
        loadCollections();
        collectionNameField.clear();

        showInfo("Подборка \"" + saved.getTitle() + "\" создана");
    }

    @FXML
    private void onDeleteCollection() {
        Collection selected = collectionsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите подборку для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setContentText("Удалить подборку \"" + selected.getTitle() + "\"?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            collectionDAO.delete(selected.getId());
            loadCollections();
            moviesInCollectionList.getItems().clear();
            showInfo("Подборка удалена");
        }
    }

    @FXML
    private void onAddMovie() {
        if (movieToAdd != null) {
            // Если пришли из деталей фильма, добавляем этот фильм в выбранную подборку
            Collection selected = collectionsList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Выберите подборку");
                return;
            }

            if (collectionDAO.isMovieInCollection(selected.getId(), movieToAdd.getId())) {
                showError("Фильм уже есть в этой подборке");
                return;
            }

            collectionDAO.addMovieToCollection(selected.getId(), movieToAdd.getId());
            showInfo("Фильм добавлен в подборку \"" + selected.getTitle() + "\"");
            loadMoviesInCollection(selected.getId());

            // Закрываем окно
            Stage stage = (Stage) collectionNameField.getScene().getWindow();
            stage.close();

        } else {
            // Обычный режим - показываем диалог выбора фильма
            showMovieSelectionDialog();
        }
    }

    @FXML
    private void onRemoveMovie() {
        Collection selectedCollection = collectionsList.getSelectionModel().getSelectedItem();
        Movie selectedMovie = moviesInCollectionList.getSelectionModel().getSelectedItem();

        if (selectedCollection == null) {
            showError("Выберите подборку");
            return;
        }

        if (selectedMovie == null) {
            showError("Выберите фильм для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setContentText("Удалить фильм \"" + selectedMovie.getTitle() + "\" из подборки \"" + selectedCollection.getTitle() + "\"?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            collectionDAO.removeMovieFromCollection(selectedCollection.getId(), selectedMovie.getId());
            loadMoviesInCollection(selectedCollection.getId());
            showInfo("Фильм удален из подборки");
        }
    }

    private void showMovieSelectionDialog() {
        // Диалог выбора фильма для добавления в подборку
        List<Movie> allMovies = movieDAO.findAll();

        ChoiceDialog<Movie> dialog = new ChoiceDialog<>(null, allMovies);
        dialog.setTitle("Выбор фильма");
        dialog.setHeaderText("Выберите фильм для добавления в подборку");
        dialog.setContentText("Фильм:");

        Optional<Movie> result = dialog.showAndWait();
        if (result.isPresent()) {
            Movie movie = result.get();
            Collection selected = collectionsList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Выберите подборку");
                return;
            }

            if (collectionDAO.isMovieInCollection(selected.getId(), movie.getId())) {
                showError("Фильм уже есть в этой подборке");
                return;
            }

            collectionDAO.addMovieToCollection(selected.getId(), movie.getId());
            loadMoviesInCollection(selected.getId());
            showInfo("Фильм добавлен в подборку");
        }
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