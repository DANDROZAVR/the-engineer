package engineer.utility;
/*
import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.Field;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonHelper {
    public static Board readBoard(String pathName) throws IOException {
        JsonReaderFactory readerFactory = Json.createReaderFactory(Collections.emptyMap());
        Path path = Paths.get(pathName);
        byte[] data = Files.readAllBytes(path);
        try (JsonReader jsonReader = readerFactory.createReader(new ByteArrayInputStream(data))) {
            JsonObject jsonObject = jsonReader.readObject();
            int rows = jsonObject.getInt("rows");
            int columns = jsonObject.getInt("columns");
            List<Field> fieldsList = new ArrayList<>();
            JsonArray jsonRows = jsonObject.getJsonArray("content");
            for (int r = 0; r < rows; ++r) {
                JsonArray jsonColumns = jsonRows.getJsonArray(r);
                for (int c = 0; c < columns; ++c) {
                    JsonObject jsonField = jsonColumns.getJsonObject(c);
                    boolean canBuild = jsonField.getBoolean("can_build");
                    JsonArray jsonBuilding = jsonField.getJsonArray("buildings");
                    // TODO: buildings
                    JsonArray jsonResources = jsonField.getJsonArray("resources");
                    if (jsonResources != null) {
                        for (int item = 0; item < jsonResources.size(); ++item) {
                            JsonObject resource = jsonResources.getJsonObject(item);
                            String resourceName = resource.getString("name");
                            int resourceProduction = resource.getInt("production");
                            // creating resource instant
                        }
                    }
                    // creating field instant
                }
                // adding elements to fieldsList
            }
            // returning the parses Board
        }
        return null;
    }
    public static void main(String[] args) {
        try {
            readBoard("src/main/resources/boardsExample/simple1.json");
        } catch (Exception e) {
            e.printStackTrace();
    }
}
}*/