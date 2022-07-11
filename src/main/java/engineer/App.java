package engineer;

import engineer.gui.javafx.Gui;
import javafx.application.Platform;

public class App {
    public static void main(String[] args) {
        Gui gui = new Gui();
        Platform.startup(gui::start);
    }

}
