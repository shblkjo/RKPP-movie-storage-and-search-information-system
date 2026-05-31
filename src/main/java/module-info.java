module ru.mygroup.isfilms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.mygroup.isfilms to javafx.fxml;
    exports ru.mygroup.isfilms;
    exports ru.mygroup.isfilms.model;
    opens ru.mygroup.isfilms.model to javafx.fxml;
    exports ru.mygroup.isfilms.dao;
    opens ru.mygroup.isfilms.dao to javafx.fxml;
}