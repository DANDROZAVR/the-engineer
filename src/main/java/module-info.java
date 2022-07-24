module the.engineer.main {
  requires javafx.base;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.fxml;

  opens engineer to
      javafx.fxml;

  exports engineer;
}
