package engineer.engine.board.logic;

public class Field {
    private FieldContent content = null;
    private final String background;

    public Field(String background) { this.background = background; }

    public String getBackground() { return background; }

    public FieldContent getContent() { return content; }
    public void setContent(FieldContent content) { this.content = content; }
}
