
package minesweeper2;

public class AgentCell {
    
    boolean MINE;
    boolean SAFE;
    boolean UNKNOWN; 
    boolean FLAGGEDMINE;
    
    int CLUE;
    String logo;
    int xcoor;
    int ycoor;
    float PROBABILITY;
    
    float RISK;
    
    boolean alreadyUpdated = false;
    
    public AgentCell(boolean MINE, boolean FLAGGEDMINE, boolean SAFE, boolean UNKNOWN, int CLUE, int xcoor, int ycoor, float PROBABILITY){
        
        this.MINE = MINE;
        this.FLAGGEDMINE = FLAGGEDMINE;
        this.SAFE = SAFE;
        this.UNKNOWN = UNKNOWN;
        this.CLUE = CLUE;
        this.xcoor = xcoor;
        this.ycoor = ycoor;
        this.PROBABILITY = PROBABILITY;
    
        if(this.UNKNOWN == true){
            this.logo = "?";
        }
        
    }
    
    public AgentCell(int xcoor, int ycoor){
        this.xcoor = xcoor;
        this.ycoor = ycoor;
    }
    
    //method that allows us to set probability of this cell straight up
    public void setProb (float PROBABILITY){
        this.PROBABILITY = PROBABILITY;
    }
    
    @Override
    public String toString() {
    	return (
                
                "(" + xcoor + "," + ycoor + ")"

                );
    }
    
    //override equals method for parsing through constraint equations
    @Override
    public boolean equals(Object o){
        
        if(o == null || !(o instanceof AgentCell)){
            return false;
        }
        
        AgentCell temp = (AgentCell) o;
        
        return xcoor == temp.xcoor && ycoor == temp.ycoor;
    
    }
    
    
    
    
}
