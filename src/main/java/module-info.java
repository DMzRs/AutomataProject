module icedcoffee.automatatheory {
    requires javafx.controls;
    requires javafx.fxml;


    opens automatatheory to javafx.fxml;
    exports automatatheory;
}