module me.nvm.MainApp {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.base;

    requires org.jfxtras.styles.jmetro;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens me.nvm.MainApp;
    exports me.nvm.MainApp;
}