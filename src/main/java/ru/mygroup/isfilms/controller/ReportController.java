package ru.mygroup.isfilms.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.Country;
import ru.mygroup.isfilms.model.Movie;
import ru.mygroup.isfilms.dao.CountryDAO;
import ru.mygroup.isfilms.service.MovieService;

import java.util.List;

public class ReportController {

    @FXML private Label totalMoviesLabel;
    @FXML private Label averageRatingLabel;
    @FXML private ListView<String> topMoviesList;

    @FXML private TextField yearField;
    @FXML private Label yearResultLabel;

    @FXML private ComboBox<Country> countryCombo;
    @FXML private Label countryResultLabel;

    private final MovieService movieService = new MovieService();
    private final CountryDAO countryDAO = new CountryDAO();

    @FXML
    public void initialize() {
        loadGeneralStats();
        countryCombo.setItems(FXCollections.observableArrayList(countryDAO.findAll()));
        countryCombo.setCellFactory(lv -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        countryCombo.setButtonCell(new ListCell<Country>() {
            @Override
            protected void updateItem(Country item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Выберите страну" : item.getName());
            }
        });
    }

    private void loadGeneralStats() {
        long count = movieService.getMoviesCount();
        totalMoviesLabel.setText("Всего фильмов: " + count);

        double avgRating = movieService.getAverageRating();
        averageRatingLabel.setText(String.format("Средний рейтинг: %.2f", avgRating));

        List<Movie> topMovies = movieService.getTopRatedMovies(10);
        topMoviesList.getItems().clear();
        for (int i = 0; i < topMovies.size(); i++) {
            Movie movie = topMovies.get(i);
            String rating = movie.getRating() != null ? String.format("%.1f", movie.getRating()) : "Нет";
            String line = String.format("%d. %s (%d) - Рейтинг: %s",
                    i + 1, movie.getTitle(), movie.getReleaseYear(), rating);
            topMoviesList.getItems().add(line);
        }
    }

    @FXML
    private void onShowByYear() {
        String yearText = yearField.getText();
        if (yearText == null || yearText.trim().isEmpty()) {
            yearResultLabel.setText("Введите год!");
            return;
        }

        try {
            int year = Integer.parseInt(yearText.trim());
            long count = movieService.getMoviesCountByYear(year);
            yearResultLabel.setText("Количество фильмов за " + year + " год: " + count);
        } catch (NumberFormatException e) {
            yearResultLabel.setText("Некорректный год!");
        }
    }

    @FXML
    private void onShowByCountry() {
        Country selected = countryCombo.getValue();
        if (selected == null) {
            countryResultLabel.setText("Выберите страну!");
            return;
        }

        long count = movieService.getMoviesCountByCountry(selected.getId());
        countryResultLabel.setText("Количество фильмов из " + selected.getName() + ": " + count);
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) totalMoviesLabel.getScene().getWindow();
        stage.close();
    }
}