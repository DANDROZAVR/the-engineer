module the.engineer.main {
  requires javafx.base;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.fxml;
  requires com.google.gson;

  opens engineer.gui.javafx.menu to
      javafx.fxml;
  opens engineer.gui.javafx.game to
      javafx.fxml;

  exports engineer;
  opens engineer.gui.javafx to javafx.fxml;
  opens engineer.utils to javafx.fxml;
}
