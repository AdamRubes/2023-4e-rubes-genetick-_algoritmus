module me.nvm.MainApp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.jfxtras.styles.jmetro;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens me.nvm.MainApp to javafx.fxml;
    exports me.nvm.MainApp;
}