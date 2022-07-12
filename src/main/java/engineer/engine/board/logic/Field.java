package engineer.engine.board.logic;

public class Field {
    private FieldContent content;
    private final String background;
    private boolean buildingEnabled = true;
    private int numberOfMovesNeeded = 1;
    public Field(String background) {
        this.background = background;
    }


    public String getBackground() { return background; }

    public FieldContent getContent() { return content; }
    public void setContent(FieldContent content) { this.content = content; }
    public boolean getBuildingEnabled(){ return buildingEnabled; }
    public int getNumberOfMovesNeeded(){ return numberOfMovesNeeded; }
    public void setBuildingEnabled(boolean buildingEnabled){ this.buildingEnabled = buildingEnabled; }
    public void setNumberOfMovesNeeded(int numberOfMovesNeeded){ this.numberOfMovesNeeded = numberOfMovesNeeded; }
}
