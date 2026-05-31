package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class FilmDao {
    private final List<Movie> films = new ArrayList<>();

    @Override
    public List<Movie> getAll() {
        return films;
    }
}
