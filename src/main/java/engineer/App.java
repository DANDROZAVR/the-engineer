package engineer;

import engineer.gui.javafx.Gui;
import javafx.application.Platform;

public class App {
    private static Gui gui;
    public static void main(String[] args) {
        Platform.startup( () -> {
            gui = new Gui();
            gui.start();
        });
    }

}
