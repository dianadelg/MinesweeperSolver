/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper2;

/**
 *
 * @author Malabooyah
 */
public class Cell {
    
    boolean MINE;
    boolean SAFE;
    boolean UNKNOWN;
    int CLUE;
    String logo;
    int xcoor;
    int ycoor;
    boolean safelyId;
        
    public Cell(){
        
    }
    
    public Cell(boolean MINE, boolean SAFE, 
    		boolean UNKNOWN, int CLUE, int xcoor, int ycoor, boolean safelyId){
        
        this.MINE = MINE;
        this.SAFE = SAFE;
        this.UNKNOWN = UNKNOWN;
        this.CLUE = CLUE;
        this.xcoor = xcoor;
        this.ycoor = ycoor;
        this.safelyId = safelyId;

        if(this.UNKNOWN == true){
            this.logo = "?";
        }
        
    }
    
    public Cell(int xcoor, int ycoor){
        this.xcoor = xcoor;
        this.ycoor = ycoor;
    }
    
    @Override
    public String toString() {
    	return (
                
                "(" + xcoor + "," + ycoor + ")"

                );
    }
    
    @Override
    public boolean equals(Object o){
        
        if(o == null || !(o instanceof Cell)){
            return false;
        }
        
        Cell temp = (Cell) o;
        
        return xcoor == temp.xcoor && ycoor == temp.ycoor;
    
    }
    
    
}
