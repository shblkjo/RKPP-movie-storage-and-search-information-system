module ru.mygroup.isfilms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.mygroup.isfilms to javafx.fxml;
    exports ru.mygroup.isfilms;
}