package engineer.engine.board.logic;

public class Field {
    private FieldContent content;
    private final String background;

    private final boolean free;
//    private int numberOfMovesNeeded = 1;

    public Field(String background, boolean free) {
        this.background = background;
        this.free = free;
    }

    public String getBackground() { return background; }
    public FieldContent getContent() { return content; }
    public boolean isFree(){ return free; }
//    public int getNumberOfMovesNeeded(){ return numberOfMovesNeeded; }
    public void setContent(FieldContent content) { this.content = content; }
//    public void setBuildingEnabled(boolean buildingEnabled){ this.buildingEnabled = buildingEnabled; }
//    public void setNumberOfMovesNeeded(int numberOfMovesNeeded){ this.numberOfMovesNeeded = numberOfMovesNeeded; }
}
