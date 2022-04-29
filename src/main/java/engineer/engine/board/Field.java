package engineer.engine.board;

// TODO Implement this class
public class Field {
    // Store information about field
    // Position, Background Texture, Field Content

    private final int row, column;
    FieldContent content;
    public Field(int row, int column, FieldContent content) {
        this.row = row;
        this.column = column;
        this.content = content;
    }
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }
    public FieldContent getContent() {
        return content;
    }
    public boolean canBuild() { return content.canBuild(); }
}
