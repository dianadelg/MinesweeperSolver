
package minesweeper2;
import java.util.List;

/**
 *
 * @author Kyle Malabuyoc and Diana Del Gaudio
 */
public class Constraint {
    
    //A + B + C
    List<AgentCell> constraintEquation;
    
    // = 2
    int clue;
    
    //for finding subset equations later on
    boolean alreadyUpdated;
    
    //constructor for when we first initialize a constraint equation
    public Constraint(List<AgentCell> constraintEquation, int clue, boolean alreadyUpdated){
        
        this.constraintEquation = constraintEquation;
        this.clue = clue;
        this.alreadyUpdated = alreadyUpdated;
       
    }
    
    public Constraint(){
        
    }
    
    @Override
    public String toString(){
     
        for(int i = 0; i < constraintEquation.size(); i++){
            System.out.print("(" + constraintEquation.get(i).xcoor + "," + constraintEquation.get(i).ycoor  + ")");
            if(i != constraintEquation.size() - 1){
                System.out.print(" + ");
            }
        }
     
        System.out.println(" = " + clue);
        
        return "";
    }
    
    
    
}
