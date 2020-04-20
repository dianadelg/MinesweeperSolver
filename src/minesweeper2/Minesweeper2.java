
package minesweeper2;

import java.util.Scanner;

/**
 *
 * @author Kyle Malabuyoc and Diana Del Gaudio
 */
public class Minesweeper2 {
    
    static int dim;
    static int totalMines;
  
    
    //if this value is given 1, run the basic agent, if this value is given 2, run advanced agent
    static int agentRun = 0;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       //generate our environment
       Cell[][] board = generatedBoard();
       //set our starting point to always be 0,0 and it is NOT a MINE
       
       //method to print board before we run agent
       showBoard(board);
       System.out.println();
       
       
       //#CSP ONLY
       
           
           System.out.println("Running Minimizing cost agent: ");
           System.out.println();
           AgentCost a = new AgentCost();
           
            a.generateBoard();
       
            //starting point is 0,0
            a.runAgent(board[0][0]);
            board[0][0].UNKNOWN = false;

            //while we havent opened all mines
            while(a.definitelyMine.size()!=totalMines) {

               // printBoard(board);
                Cell c = a.pickNextCoor();
                System.out.println("Next cell is : " + c);

                //pass that new cell into our runAgent method that handles all cell actions
                a.runAgent(board[c.xcoor][c.ycoor]);
                board[c.xcoor][c.ycoor].UNKNOWN = false;

            }

            //once here, game ends
            int safeMines = 0;
            int openedMines = 0;
            for(Cell mine : a.definitelyMine) {
                if(a.agentBoard[mine.xcoor][mine.ycoor].FLAGGEDMINE) {
                        safeMines++;
                }else {
                        openedMines++;
                }
            }

            //game finished
            System.out.println("F I N A L  R E S U L T S for Minimizing Cost: ");
            System.out.println();
            System.out.println("Total mines in game: " + a.definitelyMine.size());
            System.out.println("Total mines flagged : " + safeMines);
            System.out.println("Total mines exploded: " + openedMines);
            System.out.println("Total decisions made: " + a.totalDecisions);
         
            printFinalBoard(board, a.agentBoard);

            
      
       
       
    }
    
    
    
    
    
    //generate our environment
    public static Cell[][] generatedBoard(){
       
        System.out.println("WELCOME TO MINESWEEPER");
       // System.out.println("*Disclaimer* Actual board is unknown to the agent"+ "\n");
        
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        
        System.out.println("Enter number of mines: ");
        String mines = myObj.nextLine();  // Read user input
        int mine = Integer.parseInt(mines);
        totalMines = mine;
        //System.out.println("Number of mines is: " + totalMines);  // Output user input
        
        System.out.println("Enter size of minesweeper board: ");
        String d = myObj.nextLine();
        dim = Integer.parseInt(d);
        //System.out.println("Size of the minesweeper board: " + dim);
        
   
    
        
        Cell[][] board = new Cell[dim][dim];
        
        for(int i = 0; i < dim; i++){ 
           
                for(int j = 0; j < dim; j++){
                    board[i][j] = new Cell(false,false,true,-1,i,j, false);
                }  
        }
        
        while(mine != 0){
          
           double randomX = Math.random();
           randomX = randomX * (dim);
           int xCoor = (int) randomX;

           double randomY = Math.random();
           randomY = randomY * (dim);
           int yCoor = (int) randomY;
            
           if((xCoor >= dim || xCoor < 0) || (yCoor >=dim || yCoor < 0)){
               //System.out.println("Out of bounds");
               
           }else if(board[xCoor][yCoor].MINE == true || (xCoor == 0 && yCoor == 0)){
              // System.out.println("Mine is already here at "+ xCoor + "," + yCoor);
               
           }else{
              // System.out.println("Mine is not here at "+ xCoor + "," + yCoor + ". Adding");
                board[xCoor][yCoor].xcoor=xCoor;
                board[xCoor][yCoor].ycoor=yCoor;
                board[xCoor][yCoor].MINE=true;
                board[xCoor][yCoor].CLUE = -1;
                mine--;
                
           }
            
        }
        
        
          for(int i = 0; i < dim; i++){ 
           
                for(int j = 0; j < dim; j++){
                    
                    if(board[i][j].MINE == true){
                       
                    }else{
                        board[i][j] = new Cell(false,false,true,clueCount(board, board[i][j]),i,j, false);
                    }
                }  
           }
        
            myObj.close();
            return board;
        
    }
    
    
    public static int clueCount(Cell[][] board, Cell currentCell){
        
        int clue = 0;
        int x = currentCell.xcoor;
        int y = currentCell.ycoor;
        int[] row = {-1,0,1,1,1,-1,-1,0};
        int[] col = {-1,-1,-1,0,1,0,1,1};
        
        for(int d = 0; d<8; d++){
            //iterate through all neighbors
            int tempx = x+row[d];
            int tempy = y+col[d]; 
            
            //the temp values to be examined
            //first check if neighbor is even valid
            if((tempx >= dim || tempx < 0) || (tempy >=dim || tempy < 0)){
               //if invalid, continnue
               continue;
            }
          
            //if we get here, it means this is a valid neighbor. so check if mine
            if(board[tempx][tempy].MINE==true){
                clue++;
            }
            
        }
        
        //once here, done examining neighbor      
       return clue;
    }
    
    
     public static void printBoard(Cell[][] board){
        
        for(int i = 0; i < dim; i++){ 

                 for(int j = 0; j < dim; j++){
                     
                    if(board[i][j].UNKNOWN == false){
                       if(board[i][j].CLUE == -1){
                             System.out.print("[" + board[i][j].CLUE+ "]");
                         }else{
                             System.out.print("[" + board[i][j].CLUE+ "]");
                         }
                     }else if(board[i][j].UNKNOWN == false && board[i][j].MINE == true){
                      
                        //System.out.print("[" + "M" + "]");
                     }else{
                         System.out.print("[" + "?" + "]");
                     }
                 }

                 System.out.println();
             }
        System.out.println();
    }
     
     
     public static void printFinalBoard(Cell[][] board, AgentCell[][] agentBoard){
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println("Final Agent Board");
       
    	 for(int i = 0; i < dim; i++){ 

             for(int j = 0; j < dim; j++){
                 	if(agentBoard[i][j].FLAGGEDMINE == true){
                            System.out.print("[  " + "FM" + "  ]");
                        }else{
                            System.out.print("[  " + board[i][j].CLUE+ "  ]");
                        }
                 	
             }
             System.out.println();
    	 }
    	 
         
    	 System.out.println();
    	 System.out.println("--------------------------------------------------------------------------------------------------------");
         System.out.println("Final Game Board");

    	
         for(int i = 0; i < dim; i++){ 

                  for(int j = 0; j < dim; j++){
                      	if(agentBoard[i][j].FLAGGEDMINE == true){
                            System.out.print("[" + "FM" + "]");
                        }else{
                            System.out.print("[" + board[i][j].CLUE+ "]");
                        }
                  }
                  System.out.println();
         }
    	 System.out.println("--------------------------------------------------------------------------------------------------------");

         
     }
     
    public static void showBoard(Cell[][] board){
        
        System.out.println();
        System.out.println("Board we are running our Agent on...");
        
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                System.out.print("[" + board[i][j].CLUE + "]");
            }
            System.out.println();
        }
        
    }
    
}
