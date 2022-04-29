package engineer.engine.board;

public class FieldContentImp implements FieldContent {
    // Takes immutable arguments in constructor
    private final boolean canBuild;
    public FieldContentImp(boolean canBuild) {
        this.canBuild = canBuild;
    }
    public boolean canBuild() { return canBuild; }
}
