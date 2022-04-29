package engineer.engine.board;

// TODO Implement this class
public class FieldFactory {
    // Needed for passing additional arguments
    FieldFactory() {}
    public Field getEmptyBuildableField(int row, int col) {
        // implementation class should be presented in arguments?
        return new Field(row, col, new FieldContentImp(true));
    }
    public Field getEmptyNotBuildableField(int row, int col) {
        // implementation class should be presented in arguments?
        return new Field(row, col, new FieldContentImp(false));
    }
}
