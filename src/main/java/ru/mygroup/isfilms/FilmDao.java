package ru.mygroup.isfilms;

import java.util.ArrayList;
import java.util.List;

public class FilmDao {
    private final List<Film> films = new ArrayList<>();

    @Override
    public List<Film> getAll() {
        return films;
    }
}
