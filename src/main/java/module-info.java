module com.wkyle.bankrecord {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.wkyle.bankrecord.application to javafx.fxml;
    exports com.wkyle.bankrecord.application;
    opens com.wkyle.bankrecord.controllers to javafx.fxml;
    exports com.wkyle.bankrecord.controllers;
    opens com.wkyle.bankrecord.models to javafx.fxml;
    exports com.wkyle.bankrecord.models;
}