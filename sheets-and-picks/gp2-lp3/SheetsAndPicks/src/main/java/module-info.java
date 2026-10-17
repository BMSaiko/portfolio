module com.example.sheetsandpicks {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.sheetsandpicks to javafx.fxml;
    opens com.example.sheetsandpicks.Controllers;
    exports com.example.sheetsandpicks.Controllers;
    exports com.example.sheetsandpicks;

    exports com.example.sheetsandpicks.Models;
    opens com.example.sheetsandpicks.Models to javafx.fxml;

    exports com.example.sheetsandpicks.Controllers.Stock;
    opens com.example.sheetsandpicks.Controllers.Stock to javafx.fxml;

    exports com.example.sheetsandpicks.Controllers.app_dashboard;
    opens com.example.sheetsandpicks.Controllers.app_dashboard to javafx.fxml;

    exports com.example.sheetsandpicks.Controllers.app_login;
    opens com.example.sheetsandpicks.Controllers.app_login to javafx.fxml;

    exports com.example.sheetsandpicks.Controllers.Users;
    opens  com.example.sheetsandpicks.Controllers.Users to javafx.fxml;
}



