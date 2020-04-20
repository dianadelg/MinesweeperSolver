package minesweeper2;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

/**
 *
 * @author Kyle Malabuyoc and Diana Del Gaudio
 */
public class AgentCost {
    
    //get the dim from the main class
    int dim = Minesweeper2.dim;
    
    //used for the creation of tuples
    List<AgentCell> Neighbors;
    
    //prev is a list of type Constraint
    List<Constraint>prev = new ArrayList<>();
    
    //agentBoard thats updated at the same time with actual board
    AgentCell[][] agentBoard = new AgentCell[dim][dim];

    //mineList holds the location of every mine we have either discovered or flagged
    List<Cell> definitelyMine = new ArrayList<>();

    //definitelySafe holds a list of definitely safe locations
    List<Cell> definitelySafe = new ArrayList<>();
    
    //to be used to organize and sort probabilities
    PriorityQueue<AgentCell> probabilities;
    PriorityQueue<AgentCell> bigProbabilities;
    
    int totalDecisions = 0;
    
  
    //main method to run the agent
    public void runAgent(Cell cell){
        
        //cell is no longer unknown
        agentBoard[cell.xcoor][cell.ycoor].UNKNOWN = false;
        agentBoard[cell.xcoor][cell.ycoor].CLUE = cell.CLUE;
      
        //prints the agent board
      //  System.out.println("Print the agent board...");
      //  printBoard();
        
        
        //TWO SCENARIOS: it was either a mine or it was safe
        //update each constraint inside of prev with the new knowledge we got from board
        
        //ONE: its either a MINE CELL
        if(agentBoard[cell.xcoor][cell.ycoor].CLUE == -1){
            
            agentBoard[cell.xcoor][cell.ycoor].MINE = true;
            
            
            //we found a mine in the real board, so add it to the mine list so we can update previous queries
            if(!(definitelyMine.contains(cell))){
                definitelyMine.add(cell);
            }
            
            //print whatever is in prev at that iteration
            System.out.println("Current query...");
            for(Constraint c: prev){
                System.out.println(c);
            }
            
            //after we add it to the mines list, we need to perform the updating of the constraint equations
            //this method will go through every constraint object in prev, updating those values to be definite mines
            updateConstraints(cell);
            
        }else{ //TWO: a SAFE CELL
            
            agentBoard[cell.xcoor][cell.ycoor].SAFE = true;
            
            //before we add this clue into the constraint equation, we first check to see if there are any surrounding open mines
            //if there are, itll subtract it from the clue, so the clue tells us how many mines we have left to look for
            agentBoard[cell.xcoor][cell.ycoor].CLUE =  agentBoard[cell.xcoor][cell.ycoor].CLUE - decreaseClueWhenMineFound(cell.xcoor, cell.ycoor); 
            
            //add this cell to the definitely Safe cells
            if(!(definitelySafe.contains(cell))){
                definitelySafe.add(cell);
            }
            
            //create the tuple based off of the cell you just pulled in
            //we should only be creating constraints if we found a safe cell, not if we found a mine.
            Constraint current = new Constraint(createTuples(cell), agentBoard[cell.xcoor][cell.ycoor].CLUE, false);
            //print the tuple we just created
            //System.out.println(current);


            //then add it into prev
            prev.add(current);

            //print whatever is in prev at that iteration
            System.out.println("Current query...");
            for(Constraint c: prev){
                System.out.println(c);
            }
            
            //we then to update the constrains in the previous queries with the new knowledge we have in our definitelySafe list
            updateConstraints(cell);
           
        }
        
        //cleans the prev list of any empty constrain equations
        List<Integer> cleanPrevQ = cleanPrev();
        for(Integer i : cleanPrevQ){
            int remove = (int)i;
            prev.remove(remove - 1);
        }
        
        System.out.println();
        //print method to check that the queries were updated correctly after everything
         for(Constraint c: prev){
             System.out.println("Updated query...");   
             System.out.println(c);
         }
       
         System.out.println();
       
        //calculate probablities here? for each constraint inside of prev
        calculateProb();
        reset();
        
        //pick next coordinate
        //will get called in minesweeper 2
        //System.out.println("Next cell to travel to: " + pickNextCoor());
        
        System.out.println();
         
         System.out.println("Def safes: " + definitelySafe);
         System.out.println("Def Mines: " + definitelyMine);
        
         System.out.println();
      
         printBoard(); //ADD THIS BACK IN IF YOU WANT TO PRINT AGENT BOARD AT EACH ITERATION
    	 System.out.println("--------------------------------------------------------------------------------------------------------");
        
    }
    
    
    //generate the Agent Board
    public void generateBoard(){
        
        for(int i = 0; i < dim; i++){
            for(int j = 0; j<dim; j++){
                agentBoard[i][j] = new AgentCell(false, false, false, true, -2, i, j, 0);
            }
        }
        
    }
    
    
    //print agent board
    public void printBoard(){
       
        //for proper formatting of float values
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
       
         for(int i = 0; i < dim; i++){ 

                 for(int j = 0; j < dim; j++){
                     
                   //print the agentboard
                   if(agentBoard[i][j].FLAGGEDMINE == true){
                       
                        System.out.print("[  " + "FM" + "   ]");
                        
                   }else if(agentBoard[i][j].SAFE == true && agentBoard[i][j].UNKNOWN == true){
                       
                       System.out.print("[  " + Float.valueOf(decimalFormat.format(agentBoard[i][j].PROBABILITY)) + " ]");
                       
                   }else if(agentBoard[i][j].PROBABILITY == 0.0 && agentBoard[i][j].UNKNOWN == true){
                       
                       System.out.print("[   " + "?" + "   ]");
                       
                   }else if(agentBoard[i][j].MINE == true && agentBoard[i][j].UNKNOWN == false){
                       
                       System.out.print("[  " + agentBoard[i][j].CLUE + "   ]");
                       
                   }else if(agentBoard[i][j].UNKNOWN == false){
                       
                       System.out.print("[   " + agentBoard[i][j].CLUE + "   ]");
                       
                   }else {
                        System.out.print("[  " + Float.valueOf(decimalFormat.format(agentBoard[i][j].PROBABILITY)) + " ]");
                   }
                 }

                 System.out.println();
             }
         
        System.out.println();
        
    }
    
    
    //getting valid neighbors of current cell
     public List<AgentCell> getValidNeighbors(int x, int y){
                
	        List<AgentCell> neighbors = new ArrayList<>();
	        int[] row = {-1,0,1,1,1,-1,-1,0};
	        int[] col = {-1,-1,-1,0,1,0,1,1};
	    
	        for(int d = 0; d<8; d++){
                    
	            int tempx = x+row[d];
	            int tempy = y+col[d]; 
                    
	            if((tempx >= dim || tempx < 0) || (tempy >=dim || tempy < 0)){
                        
                        
	            }else if(agentBoard[tempx][tempy].UNKNOWN == false){ //we dont add already visited nodes
                        
                        
                    }else if(agentBoard[tempx][tempy].FLAGGEDMINE == true){ // we dont add flagged mines
                        
                        
                    }else if(agentBoard[tempx][tempy].SAFE == true){ //we dont add def safe to neighbors because we dont incorporate those into our probability
                        
                        
                    }else{
	                neighbors.add(agentBoard[tempx][tempy]);
                    }
	             
                }
                
	        return neighbors;
                
      }
     
     
     
     //create the tuples to be put into prev
     public List<AgentCell> createTuples(Cell cell){
         
           //get neighbors of the cell we are at
           Neighbors = getValidNeighbors(cell.xcoor, cell.ycoor);

           return Neighbors;
           
     }
     
     
     //big boi method that is going to update the constraints
     public void updateConstraints(Cell cell){
       
         //we only pass in Cell because we need its x and y values
         //AgentCell ac = new AgentCell(cell.xcoor, cell.ycoor);
         List<AgentCell> newUpdates = new ArrayList<>();
         newUpdates.add(agentBoard[cell.xcoor][cell.ycoor]);
        
         List<Integer> removeIndexFromPrev = new ArrayList<>();
         
   
         //so, when we get a new cell value, we should parse queries FIRST and see if any updates can be made before we check for this new var
            for(Constraint c: prev){

                    //if the clue equals zero, all variables are definitely safe, and we remove the entire equation from the prev queries
                    if(c.clue == 0){ 

                        
                        //we need to update that in previous queries
                        for(AgentCell variable : c.constraintEquation){

                            //add these new variables that are all def safe into the newUPdates so we can update accordingly
                            //update in agent board that all of these variables are safe
                            agentBoard[variable.xcoor][variable.ycoor].SAFE = true;
                            
                            //the probability of this cell being a mine is zero
                            agentBoard[variable.xcoor][variable.ycoor].PROBABILITY = 0;
                            newUpdates.add(variable);


                            if(!(definitelySafe.contains(new Cell(variable.xcoor, variable.ycoor)))){
                                definitelySafe.add(new Cell(variable.xcoor, variable.ycoor));
                            }

                        }


                       removeIndexFromPrev.add(prev.lastIndexOf(c) + 1);
                 
                    }else if(c.clue == c.constraintEquation.size()){ //if the amount of variables equals the clue, we know they are all mines, FLAG THEM

                        //we need to update that in previous queries

                        for(AgentCell variable : c.constraintEquation){

                            //setting every variable (location) in the constraint equation to be a flagged mine
                              agentBoard[variable.xcoor][variable.ycoor].FLAGGEDMINE = true;
                              
                              //probability of this cell being a mine is 1
                              agentBoard[variable.xcoor][variable.ycoor].PROBABILITY = 1;
                              
                              newUpdates.add(variable);

                              if(!(definitelyMine.contains(new Cell(variable.xcoor, variable.ycoor)))){
                                 definitelyMine.add(new Cell(variable.xcoor, variable.ycoor));
                              }

                        }

                       removeIndexFromPrev.add(prev.lastIndexOf(c) + 1);
                    
                    }
                    
                    
            }
           
            //method to remove the indexes of prev that had either a clue of zero, or # vars = clue
            if(!(removeIndexFromPrev.isEmpty())){
                
                    int size = removeIndexFromPrev.size() - 1;
                    while(size >= 0){
                        
                        int remove = removeIndexFromPrev.get(size);
                        prev.remove(remove - 1);
                        size--;
                    }
                    
                }
    
         //INITIAL CHECK IS OVER
         //TWO SCENARIOS: WE HIT A MINE, OR WE HIT A SAFE CELL
         
         
         //while the list holding the updates we have to keep making is NOT empty
             //for every variable inside of newUpdates
             
             for(ListIterator<AgentCell> itr = newUpdates.listIterator(); itr.hasNext(); itr = newUpdates.listIterator()){
             
                 //grab the value at the front of the list (in the first ROUND, there are no updates to be made)
                 AgentCell var = itr.next();
                 itr.remove();
                // System.out.println("This is whats next up in the newUpdates list: " + var);
            
                 List<Integer> removeInPrev = new ArrayList<>();
                 
                if(var.CLUE == -1 || agentBoard[var.xcoor][var.ycoor].FLAGGEDMINE == true){ //if the variable inside of the updates list is a mine, perform these update
                    
                    //traverse the prev list holding all of the constrain equations and the variables we discovered to be mines to mine
                    for(Constraint c: prev){
                        
                        if(c.constraintEquation.contains(var)){

                           // System.out.println("WE FOUND A MINE IN AN EQUATION");

                            //we decrease the clue by one
                            c.clue--;

                            //we remove the variable from the equation
                            c.constraintEquation.remove(var);

                            //if the clue equals zero, all variables are definitely safe, and we remove the entire equation from the prev queries
                            if(c.clue == 0){ 
                               
                                //we need to update that in previous queries
                                for(AgentCell variable : c.constraintEquation){
                                    
                                    //setting every variable (location) in the constraint equation to be a flagged mine
                                    agentBoard[variable.xcoor][variable.ycoor].SAFE = true;
                              
                                    //probability of this cell being a mine is 1
                                    agentBoard[variable.xcoor][variable.ycoor].PROBABILITY = 0;
                                    
                                    //add these new variables that are all def safe into the newUPdates so we can update accordingly
                                  //  System.out.println("Adding: " + variable + " into newUpdates list.");
                                    itr.add(variable);
                                    
                                    
                                    if(!(definitelySafe.contains(new Cell(variable.xcoor, variable.ycoor)))){
                                        definitelySafe.add(new Cell(variable.xcoor, variable.ycoor));
                                    }
                                    
                                }
                             
                            removeInPrev.add(prev.lastIndexOf(c) + 1);

                            }else if(c.clue == c.constraintEquation.size()){ //if the amount of variables equals the clue, we know they are all mines, FLAG THEM

                                //we need to update that in previous queries

                                for(AgentCell variable : c.constraintEquation){
                                     
                                    //setting every variable (location) in the constraint equation to be a flagged mine
                                    agentBoard[variable.xcoor][variable.ycoor].FLAGGEDMINE = true;
                              
                                    //probability of this cell being a mine is 1
                                    agentBoard[variable.xcoor][variable.ycoor].PROBABILITY = 1;
                                    
                                  //  System.out.println("Adding: " + variable + " into newUpdates list.");
                                    itr.add(variable);

                                    if(!(definitelyMine.contains(new Cell(variable.xcoor, variable.ycoor)))){
                                      definitelyMine.add(new Cell(variable.xcoor, variable.ycoor));
                                    }
                                      
                                }

                                removeInPrev.add(prev.lastIndexOf(c) + 1);

                            }

                        }
                        
                      
                   }

                  
                }else{ //we didnt hit a mine, SAFE CELL OTHER SCENARIO

                    
                    //traverse the prev list holding all of the constrain equations and the variables we discovered to be safe to safe
                    for(Constraint c: prev){

                        
                        if(c.constraintEquation.contains(var)){
                            
                            
                          //  System.out.println("WE FOUND A SAFE SPOT IN AN EQUATION");

                            //we remove the variable from the equation
                            c.constraintEquation.remove(var);

                           if(c.clue == c.constraintEquation.size()){ //if the amount of variables equals the clue, we know they are all mines, FLAG THEM
                             
                                //we need to update that in previous queries
                                for(AgentCell variable : c.constraintEquation){
                                    
                                    //setting every variable (location) in the constraint equation to be a flagged mine
                                      agentBoard[variable.xcoor][variable.ycoor].FLAGGEDMINE = true;
                                    
                                     // System.out.println("Adding: " + variable + " into newUpdates list.");
                                      
                                      itr.add(variable);
                                      
                                      if(!(definitelyMine.contains(new Cell(variable.xcoor, variable.ycoor)))){
                                        
                                        definitelyMine.add(new Cell(variable.xcoor, variable.ycoor));
                                      }
                                      
                                }

                                removeInPrev.add(prev.lastIndexOf(c) + 1);
                        
                            }

                        }
                        

                    }
                 
                    
                }
                
                
                //method to remove the indexes of prev that had either a clue of zero, or # vars = clue
                if(!(removeInPrev.isEmpty())){
                    int size = removeInPrev.size() - 1;
                    while(size >= 0){
                        
                        int remove = removeInPrev.get(size);
                        prev.remove(remove - 1);
                        size--;
                        
                    }
                }
                
                
             }
             
            // System.out.println("should be empty: " + newUpdates);
             
          }
        
     
     //when we are discovering new clues, if we find a clue that already has a neighbor thats an open mine, we need to decrease the clue
     // by how many neighbors mines there are that are already open so the we know how many more remaining mines we need to look for
     public int decreaseClueWhenMineFound(int x, int y){
         
         int totalMinesFound = 0;
        
        int[] row = {-1,0,1,1,1,-1,-1,0};
        int[] col = {-1,-1,-1,0,1,0,1,1};

        for(int d = 0; d<8; d++){

            int tempx = x+row[d];
            int tempy = y+col[d]; 

            if((tempx >= dim || tempx < 0) || (tempy >=dim || tempy < 0)){


            }else if((agentBoard[tempx][tempy].UNKNOWN == false && agentBoard[tempx][tempy].MINE == true) || agentBoard[tempx][tempy].FLAGGEDMINE == true){
                totalMinesFound++;
            }

        }
         
         return totalMinesFound;
     }
     
     //method for removing empty queries in prev
     public List<Integer> cleanPrev(){
      
         List<Integer> removeTheseQueries = new ArrayList<>();
         
         //for every constraint inside of prev
         for(Constraint c : prev){
             if(c.constraintEquation.isEmpty()){
                 //add the current empty location to the queries we gotta remove
                 
                 removeTheseQueries.add(prev.lastIndexOf(c) + 1);
             }
         }
         
         return removeTheseQueries;
     }
     
     //calculate the probability of each constraint equation and set the probabilities inside the agent board
     public void calculateProb(){
    	 
    	 probabilities = new PriorityQueue<AgentCell>(new Comparator<AgentCell>() {
    			//must override compare to make sure add each add of priority queue, min is always the one with lowest f
    	        	public int compare(AgentCell a1, AgentCell a2){
    	                  return Float.compare((a1.PROBABILITY),(a2.PROBABILITY));
    	              }
    	           });
    	 
         
         //for every constraint inside of prev
         for(Constraint c : prev){
            
            float clue = (float) c.clue;
            float size = (float) c.constraintEquation.size();
            float probability = (clue / size);
             
            //for every contraint equation inside of the constraint object, update their probabilities
            for(AgentCell ac : c.constraintEquation){
                
                //if the variables we are trying to update have already been updated, we dont want to update them
                if(ac.alreadyUpdated == true){
                    //do nothing
                }else{
                    ac.PROBABILITY = probability;
                    ac.alreadyUpdated = true;
                  //  System.out.println("Probability of: " + ac + " is " + ac.PROBABILITY);
                    probabilities.add(ac);
                }
                
            }
            
         }
         
      }
     
      //method to reset all alreadyUpdated fields back to false;
      public void reset(){
          
          //for every Constraint object inside of prev
          for(Constraint c : prev){
              
              //for every constraint equation inside of the Constraint object
              for(AgentCell ac: c.constraintEquation){
                  
                  //if the locations alreadyUpdated field is true, set it back to false
                  if(ac.alreadyUpdated == true){
                      ac.alreadyUpdated = false;
                  }
                  
              }
              
          }
          
      }
      
      public Cell pickNextCoor() {
          
    	 //first check if any safes with 0 prob that have not been visited
    	  for(Cell c : definitelySafe) {
    		  if(agentBoard[c.xcoor][c.ycoor].UNKNOWN) {
    			  //means was not visited
    			  agentBoard[c.xcoor][c.ycoor].UNKNOWN = false;
    			  return c;
    		  }
    	  }
          
    	  //if here, no safes
    	  //so get from prob
    	  
    	  bigProbabilities = new PriorityQueue<AgentCell>(new Comparator<AgentCell>() {
   			//must override compare to make sure add each add of priority queue, min is always the one with lowest f
   	        	public int compare(AgentCell a1, AgentCell a2){
   	                  return Float.compare((a1.PROBABILITY),(a2.PROBABILITY));
   	              }
                        
   	           });
    	  
    	  while(probabilities.size()!=0) {
              
    		  AgentCell picked = probabilities.poll();
    		 // System.out.println("Probability of : " + picked + " is:" + picked.PROBABILITY);
    		  if(picked.PROBABILITY <= .5) {
    			  Cell cell = new Cell (picked.xcoor, picked.ycoor);
                          totalDecisions++;
    			  return cell;
    		  }
    		  bigProbabilities.add(picked);
    	  }
    	  
    	  //if here, it means there were no probabilities in the list <= 0.5
    	  //so pick random
    	  for(int i = 0; i < dim; i++) {
    		  for (int j = 0; j < dim; j++) {
    			  if(agentBoard[i][j].UNKNOWN && agentBoard[i][j].PROBABILITY<=.5) {
    				  return new Cell(i, j);
    			  }
    		  }
    	  }
    	  
          //we didnt land in any of the cases above, so we simply pick the first element inside of the bigProbabilities list
    	  AgentCell hold = bigProbabilities.poll();
    	  return new Cell(hold.xcoor, hold.ycoor);

      }
     
      
}
