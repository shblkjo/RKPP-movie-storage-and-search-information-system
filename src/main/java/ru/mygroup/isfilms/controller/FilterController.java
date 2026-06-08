package ru.mygroup.isfilms.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mygroup.isfilms.model.*;
import ru.mygroup.isfilms.dao.*;

public class FilterController {

    @FXML private TextField titleField;
    @FXML private TextField yearFromField;
    @FXML private TextField yearToField;
    @FXML private ComboBox<Genre> genreCombo;
    @FXML private ComboBox<Country> countryCombo;
    @FXML private Slider ratingFromSlider;
    @FXML private Slider ratingToSlider;
    @FXML private Label ratingFromLabel;
    @FXML private Label ratingToLabel;

    private FilterDTO filter;
    private Runnable onSearchCallback;

    @FXML
    public void initialize() {
        ratingFromSlider.valueProperty().addListener((obs, old, val) ->
                ratingFromLabel.setText(String.format("от %.1f", val.doubleValue()))
        );
        ratingToSlider.valueProperty().addListener((obs, old, val) ->
                ratingToLabel.setText(String.format("до %.1f", val.doubleValue()))
        );

        genreCombo.setItems(FXCollections.observableArrayList(new GenreDAO().findAll()));
        countryCombo.setItems(FXCollections.observableArrayList(new CountryDAO().findAll()));

        genreCombo.getItems().add(0, null);
        countryCombo.getItems().add(0, null);

        genreCombo.setCellFactory(lv -> new ListCell<Genre>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        countryCombo.setCellFactory(lv -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    public void setFilter(FilterDTO filter) {
        this.filter = filter;
        if (filter != null) {
            titleField.setText(filter.getTitle());
            if (filter.getYearFrom() != null) yearFromField.setText(String.valueOf(filter.getYearFrom()));
            if (filter.getYearTo() != null) yearToField.setText(String.valueOf(filter.getYearTo()));
            if (filter.getRatingFrom() != null) ratingFromSlider.setValue(filter.getRatingFrom());
            if (filter.getRatingTo() != null) ratingToSlider.setValue(filter.getRatingTo());

            if (filter.getGenreId() != null) {
                genreCombo.setValue(findGenreById(filter.getGenreId()));
            }
            if (filter.getCountryId() != null) {
                countryCombo.setValue(findCountryById(filter.getCountryId()));
            }
        }
    }

    public void setOnSearchCallback(Runnable callback) {
        this.onSearchCallback = callback;
    }

    @FXML
    private void onSearch() {
        FilterDTO result = new FilterDTO();

        String title = titleField.getText();
        if (title != null && !title.trim().isEmpty()) {
            result.setTitle(title.trim());
        }
        String yearFrom = yearFromField.getText();
        if (yearFrom != null && !yearFrom.trim().isEmpty()) {
            try {
                result.setYearFrom(Integer.parseInt(yearFrom.trim()));
            } catch (NumberFormatException e) {}
        }
        String yearTo = yearToField.getText();
        if (yearTo != null && !yearTo.trim().isEmpty()) {
            try {
                result.setYearTo(Integer.parseInt(yearTo.trim()));
            } catch (NumberFormatException e) {}
        }
        if (genreCombo.getValue() != null) {
            result.setGenreId(genreCombo.getValue().getId());
        }
        if (countryCombo.getValue() != null) {
            result.setCountryId(countryCombo.getValue().getId());
        }
        if (ratingFromSlider.getValue() > 0) {
            result.setRatingFrom(ratingFromSlider.getValue());
        }
        if (ratingToSlider.getValue() < 10) {
            result.setRatingTo(ratingToSlider.getValue());
        }

        filter = result;

        if (onSearchCallback != null) {
            onSearchCallback.run();
        }

        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onReset() {
        titleField.clear();
        yearFromField.clear();
        yearToField.clear();
        genreCombo.setValue(null);
        countryCombo.setValue(null);
        ratingFromSlider.setValue(0);
        ratingToSlider.setValue(10);
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private Genre findGenreById(Integer id) {
        return new GenreDAO().findById(id).orElse(null);
    }

    private Country findCountryById(Integer id) {
        return new CountryDAO().findById(id).orElse(null);
    }

    public FilterDTO getFilter() {
        return filter;
    }
}