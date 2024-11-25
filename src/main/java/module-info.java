module icedcoffee.automatatheory {
    requires javafx.controls;
    requires javafx.fxml;


    opens icedcoffee.automatatheory to javafx.fxml;
    exports icedcoffee.automatatheory;
}