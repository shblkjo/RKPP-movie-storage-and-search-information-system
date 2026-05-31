package ru.mygroup.isfilms.dao;

import ru.mygroup.isfilms.model.Person;
import ru.mygroup.isfilms.model.Country;
import java.sql.*;
import java.util.*;
import java.sql.Date;

public class PersonDAO extends AbstractDAO<Person, Integer> {

    @Override
    protected String getTableName() {
        return "cinema.people";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected Person mapResultSetToEntity(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getInt("id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        person.setMiddleName(rs.getString("middle_name"));

        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            person.setBirthDate(birthDate.toLocalDate());
        }

        person.setProfession(rs.getString("profession"));
        person.setBiography(rs.getString("biography"));
        person.setPhotoImage(rs.getBytes("photo_image"));
        return person;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Person person, boolean includeId) throws SQLException {
        int index = 1;
        if (includeId) {
            stmt.setInt(index++, person.getId());
        }
        stmt.setString(index++, person.getFirstName());
        stmt.setString(index++, person.getLastName());
        stmt.setString(index++, person.getMiddleName());
        stmt.setObject(index++, person.getBirthDate() != null ? Date.valueOf(person.getBirthDate()) : null, Types.DATE);
        stmt.setString(index++, person.getProfession());
        stmt.setString(index++, person.getBiography());
        stmt.setBytes(index++, person.getPhotoImage());
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO cinema.people (first_name, last_name, middle_name, birth_date, profession, biography, photo_image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE cinema.people SET first_name=?, last_name=?, middle_name=?, birth_date=?, " +
                "profession=?, biography=?, photo_image=? WHERE id=?";
    }

    @Override
    protected void setId(Person person, Object id) {
        person.setId((Integer) id);
    }

    public List<Person> findByLastName(String lastName) {
        List<Person> people = new ArrayList<>();
        String sql = "SELECT * FROM cinema.people WHERE LOWER(last_name) LIKE LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + lastName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                people.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по фамилии", e);
        }
        return people;
    }

    public List<Person> findByProfession(String profession) {
        List<Person> people = new ArrayList<>();
        String sql = "SELECT * FROM cinema.people WHERE LOWER(profession) LIKE LOWER(?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + profession + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                people.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по профессии", e);
        }
        return people;
    }

    public List<Person> getPeopleForMovie(Integer movieId, String roleType) {
        List<Person> people = new ArrayList<>();
        String sql = "SELECT p.* FROM cinema.people p " +
                "JOIN cinema.movie_people mp ON p.id = mp.person_id " +
                "WHERE mp.movie_id = ? AND mp.role_type = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setString(2, roleType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                people.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения людей для фильма", e);
        }
        return people;
    }

    public void addCountryToPerson(Integer personId, Integer countryId) {
        String sql = "INSERT INTO cinema.person_countries (person_id, country_id) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, personId);
            stmt.setInt(2, countryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления страны человеку", e);
        }
    }

    public List<Country> getCountriesForPerson(Integer personId) {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT c.* FROM cinema.countries c " +
                "JOIN cinema.person_countries pc ON c.id = pc.country_id " +
                "WHERE pc.person_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, personId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Country country = new Country();
                country.setId(rs.getInt("id"));
                country.setName(rs.getString("name"));
                countries.add(country);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения стран для человека", e);
        }
        return countries;
    }
}