package engineer.engine.board;

// TODO Implement this class
public class FieldFactory {
    // Needed for passing additional arguments
    private class SimpleFieldContentImp implements FieldContent {
        // Takes immutable arguments in constructor
        private final boolean canBuild;
        public SimpleFieldContentImp(boolean canBuild) {
            this.canBuild = canBuild;
        }
        public boolean canBuild() { return canBuild; }
    }
    FieldFactory() {}
    public Field getEmptyBuildableField(int row, int col) {
        // implementation class should be presented in arguments?
        return new Field(row, col, new SimpleFieldContentImp(true));
    }
    public Field getEmptyNotBuildableField(int row, int col) {
        // implementation class should be presented in arguments?
        return new Field(row, col, new SimpleFieldContentImp(false));
    }
}
