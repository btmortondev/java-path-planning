/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathplanning;

/**
 *
 * @author Brandon
 */
import java.util.Scanner;
//public class AstarJava {

/**
 *
 * @author John Sullins
 */
public class AStarJava {
    
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    
    private static final int FLOORCOST = 1;
    private static final int MOATCOST = 20;
    private static final int SWAMPCOST = 5;
    
    private static char[][] map = new char[][]{
            {'F','F','F','F','F','F','F','F','F','F'},
            {'F','W','W','W','W','W','W','W','W','F'},
            {'F','F','F','F','F','F','F','F','F','F'},
            {'T','F','F','F','F','F','F','F','F','T'},
            {'M','M','M','M','M','M','M','M','M','M'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'}};
        
    private static int t1x = 3;
    private static int t1y = 0;
    private static int t2x = 3;
    private static int t2y = 9;
    private static String characterType;
    private static int startX;
    private static int startY;
    private static int goalX;
    private static int goalY;
    private static int fringeCount;
    private static int treeCount;
    private static int heuristictype;
    
    
/**
 * This is the main method you will modify. It takes an (x, y)
 * coordinate and returns the estimated distance to the goal.
 * The default version uses a simple Manhattan distance.
 * Your custom version should 1) be admissible, and 2) perform
 * better in terms of nodes added to the fringe and tree.
 * Note that you can access the goal location using the global
 * variables goalX, goalY.
 * @param x
 * @param y
 * @return 
 */        
    public static double heuristic(int x, int y, int gx, int gy) {    
    
        int manhattan = Math.abs(gx - x) + Math.abs(gy - y);
        if (heuristictype == 0) {
            return manhattan;
        }
        
        // ******* Your heurstic goes here *******
        else {
            //variable to keep track of my estimated cost
            int estimated_cost = 0;
          //------------------TESTING FOR THE MOAT-------------------------------------------------------------
          //Checks if goal and current x are on opposite sides of the moat
          if((x < 4 && gx > 4) || (x > 4 && gx < 4)){
              //if goal and current node are on opposite sides of the moat it adds the cost of crossing the moat
              estimated_cost += MOATCOST;
          }
          
          //If the goal is on the moat and the current node is on the left side of the moat
           if (x < 4 && gx == 4) {
               //checks if the y value of the current value is not equal to the y value of the goal 
               if (y != gy) {
                   //adds the cost of traveling vertcally to the same level as the goal and then takes account for the moat cost too 
                   estimated_cost += Math.abs(gy - y) * FLOORCOST + MOATCOST;
                }
               //checks to see if current node is on the moat
                if (x == 4) {
                    //adds the cost of traveling along the moat
                    estimated_cost += Math.abs(gy - y) * MOATCOST;
                }
            }
          
          //---------------------TESTING FOR THE WALL------------------------------------------------------
          //check if current node and goal are on oppopsite sides of the wall
          if((x < 1 && gx > 1) || (x > 1 && gx < 1)){
              //checks if node is on the bottom half of the wall
              if(y >= 5){
                  //calculates the cost for going around the wall to the adjacent side
                  //adds the cost of going from starting node to bottom opening of the wall and the cost from the waypoint to the goal
                  estimated_cost += (Math.abs(1 - x) + Math.abs(9 - y)) + (Math.abs(gx - 1) + Math.abs(gy - 9));
                  
              }else{
                  //cost of traveling around top half of wall
                  //adds the cost of going from starting node to the top opening of the wall and the cost from that waypoint to the goal
                  estimated_cost += (Math.abs(1 - x) + Math.abs(0 - y)) + (Math.abs(gx - 1) + Math.abs(gy - 0));
              }
          }
          
          //-------------------------TESTING TELEPORTERS---------------------------------------------
            //Tests if node is on bottom half of the board
            if(y >= 5){
                //Compares if manhattan is less than the cost of taking the teleporter at the bottom of the map
                //and then adds the cost from the opposite teleporter to the goal
                if((Math.abs(gx - x) + Math.abs(gy - y)) < (Math.abs(3 - x) + Math.abs(9 - y) + (Math.abs(3 - gx) + Math.abs(0 - gy)))){
                    //If it's cheaper to travel to the goal without going through the teleporter than it just adds the cost of going to the goal directly
                    estimated_cost += (Math.abs(gx - x) + Math.abs(gy - y));
                }else{
                    //This adds the cost of going through the bottom teleporter  and going from the the opposite teleporter to the goal
                    estimated_cost += (Math.abs(3 - x) + Math.abs(9 - y) + (Math.abs(3 - gx) + Math.abs(0 - gy)));
                }
            }else{
                //This section handels if the node is at the top half of the map. This first if statement checks to see if it's cheaper to travel straight
                //to the goal than using the top teleporter and then traveling to the goal.
                if((Math.abs(gx - x) + Math.abs(gy - y)) < (Math.abs(3 - x) + Math.abs(0 - y) + (Math.abs(3 - gx) + Math.abs(9 - gy)))){
                    //If it's cheaper to travel straight to the goal, then it adds the heuristic of that move to the estimated cost
                    estimated_cost += (Math.abs(gx - x) + Math.abs(gy - y));
                }else{
                    //If it's cheaper to use the teleporter than it adds the cost of going to the top teleporter and then the cost from the teleporter
                    //to the goal node.
                    estimated_cost += (Math.abs(3 - x) + Math.abs(0 - y) + (Math.abs(3 - gx) + Math.abs(9 - gy)));
                }
            }
          
          //------------------TESTING FOR THE SWAMP----------------------------------------------
            //This checks if the current node is in the bottom half of the swamp
            if(x > 5 && y > 5){
                //Checks if it's cheaper to move horizontally out of the swamp toward the moat or if it's cheaper to move
                //up to the closest floor tile and then move over across the moat.
                if(((x - 5) * SWAMPCOST) < ((x - 5) + (SWAMPCOST * (y - 6)))){
                    //if it's cheaper to move in the swamp it adds the cost of moving through the swamp first
                    estimated_cost += (x - 5) * SWAMPCOST;
                    //then sets the x of the current node on the other side of the moat
                    int tempx = 3;
                    //then adds heuristic of the new current location to the goal node
                    estimated_cost += Math.abs(gx - tempx) + Math.abs(gy - y);
                }else{
                    //this adds the cost of moving up to the nearest floor tile and then moving toward the moat if it's cheaper than
                    //moving horizontally though the swamp
                    estimated_cost += ((x - 5) + SWAMPCOST * (y - 6));
                    //then adds the cost of moving from the new current/temp node to the goal
                    estimated_cost += Math.abs(gx - 3) + Math.abs(gy - 6);
                }
            }
            //tests top half of swamp
            if(x > 5 && y < 4){
                //Checks if it's cheaper to move horizontally through the swamp to the moat than moving to the closet floor tile
                //and then moving towards the moat
                if(((x - 5) * SWAMPCOST) < ((x - 5) + (SWAMPCOST * (3 - y)))){
                    //adds the cost of moving toward the moat through the swamp
                    estimated_cost += (x - 5) * SWAMPCOST;
                    //then adds the cost of moving from the position in line horizontally on the other side of the moat to the goal.
                    estimated_cost += Math.abs(gx - 3) + Math.abs(gy - y);
                }else{
                    //This adds the cost of moving down toward the closest floor tile from a postion in the top half of the swamp and then moving toward
                    //the moat.
                    estimated_cost += ((x - 5) + SWAMPCOST *(3 - y));
                    //adds the cost of moving from the node on the left side of the moat to the goal node.
                    estimated_cost += Math.abs(gx - 3) + Math.abs(gy - 4);
                }
            }
            //TESTING FOR NAVIGATING JUST THROUGH THE SWAMP
            //checks if the starting node is on the right side of the node 
            if (x > 4) {
                //adds the cost of traveling vertically through the swamp toward the y value of the goal
                estimated_cost += Math.abs(gy - y) * SWAMPCOST;
                
                //checks to see if the node is in the top half of the swamp and the goal is in the bottom half of the swamp
                if (y < 4 && gy > 5) {
                    //handels crossing over both rows of floor tiles from the top of the swamp to the bottom
                    estimated_cost -= 2 * (SWAMPCOST - FLOORCOST);
                }
                //This checks if the current node is on the bottom half of the swamp with the goal.
                if (y > 5 && gy < 5) {
                    //Handels crossing over both tile rows from the bottom half of the swamp to the top half of the swamp where the goal is
                    estimated_cost -= 2 * (SWAMPCOST - FLOORCOST);
                }
                //This checks if the current node is loctaed in the 5th row and if the goal is in the top half of the swamp
                if (y == 5 && gy < 5) {
                    //handels the cost of crossing over just one floor tile
                    estimated_cost -= SWAMPCOST - FLOORCOST;
                } 
                //This checks if the current node is located in the 4th row where the floor tiles are
                if (y == 4 && gy > 5) {
                    //handels crossing over just one row of floor tiles
                    estimated_cost -= SWAMPCOST - FLOORCOST;
                }
                //checks if the current node is in the 5th row where the floor tiles are and if the goal is on the bottom half of the swamp
                //or if the current node is in the 4th row where the floor tiles are and the goal is in the top half of the swamp
                if (y >= 5 && gy > 5 || y <= 4 && gy < 4) {
                    //adds the horizontal cost of moving through the swamp from the current node to the goal
                    estimated_cost += Math.abs(gx - x) * SWAMPCOST;
                }
            }
            
            return estimated_cost;
        }
    }
        
    public static class Tile {            
        private char tileType;
        private int x;
        private int y;
        private int treestate; 
        private int parentX;
        private int parentY;
        private double gDist;
        private boolean inPath;
            
        public Tile() {}

        public double getCost() {
            switch(tileType) {
                case 'F': 
                    return FLOORCOST;
                case 'T':
                    return FLOORCOST;
                case 'W':
                    return  10000.0;
                case 'M':
                    return MOATCOST;
                case 'S':
                    return SWAMPCOST;
                default:
                    return 0.0;
            }
        }
    }
        
    // Returns the tile in the fringe with the lowest f = g + h measure
    public static Tile getBest(Tile[][] graph) {
        double bestH = 100000;
        Tile bestTile = new Tile();
        bestTile.x = -1; // Hack to let caller know that nothing was in fringe (failure)
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (graph[i][j].treestate == 1) {   // Fringe node
                    double h = graph[i][j].gDist + heuristic(i, j, goalX, goalY);
                    if (h < bestH) {
                        bestH = h;
                        bestTile = graph[i][j];
                    }
                }
            }
        }
        return bestTile;
    }
    
        
    // When a node is expanded, we check each adjacent node. This method takes the graph,
    // the coordinates of the parent tile (the one expanded) and its child (the adjacent tile).
    public static void checkAdjacent(Tile[][] graph, int parentX, int parentY, int childX, int childY) {
    
        // If already in tree, exit
        if (graph[childX][childY].treestate == 2) {
            return;
        }
        // If unexplored, add to fringe with path from parent and distance based on
        // distance from start to parent and cost of entering the node.
        if (graph[childX][childY].treestate == 0) {
            graph[childX][childY].treestate = 1;
            graph[childX][childY].gDist = graph[parentX][parentY].gDist + graph[childX][childY].getCost();
            graph[childX][childY].parentX = parentX;
            graph[childX][childY].parentY = parentY;
        
            // Add to stats of nodes added to fringe
            fringeCount++;
            return;
        }       
        // If fringe, reevaluate based on new path
        if (graph[childX][childY].treestate == 1) {
            if (graph[parentX][parentY].gDist + graph[childX][childY].getCost() < graph[childX][childY].gDist) { 
                // Shorter path through parent, so change path and cost.
                graph[childX][childY].gDist = graph[parentX][parentY].gDist + graph[childX][childY].getCost();
                graph[childX][childY].parentX = parentX;
                graph[childX][childY].parentY = parentY;        
            }
            return;
        }
    }
        
    // Once the goal has been found, we need the path found to the goal. This method
    // works backward from the goal through the parents of each tile. It also totals
    // up the cost of the path and returns it.
    public static double finalPath(Tile[][] graph) {
        double cost = 0;
    
        // Start at goal
        int x = goalX;
        int y = goalY;
    
        // Loop until start reached
        while (x != startX || y != startY) {
        
            // Add node to path and add to cost
            graph[x][y].inPath = true;
            cost += graph[x][y].getCost();
        
            // Work backward to parent and continue
            int tempx = graph[x][y].parentX;
            int tempy = graph[x][y].parentY;
            x = tempx;
            y = tempy;
        }
        graph[startX][startY].inPath = true;
        return cost;
    }

    // This method prints the map at the end. Each tile contains the tile type, 
    // its tree status (0=unexplored, 1=fringe, 2=tree), and a * if that tile 
    // was in the final path.
    public static void printGraph(Tile[][] graph) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.out.print(graph[j][i].tileType);
                System.out.print(graph[j][i].treestate);
                if (graph[j][i].inPath) {
                    System.out.print("*");
                }
                else {
                    System.out.print(" ");
                }
                System.out.print("  ");
            }
            System.out.print("\n");
        }    
    }

    /** 
     * Get inputs from user
     */
    public static void setProperties() {
        Scanner scanner = new Scanner(System.in);
        
        // Where does character start and end
        System.out.print("X coordinate of start: ");
        startX = scanner.nextInt();
        System.out.print("Y coordinate of start: ");
        startY = scanner.nextInt();
        System.out.print("X coordinate of end: ");
        goalX = scanner.nextInt();
        System.out.print("Y coordinate of end: ");
        goalY = scanner.nextInt();
        
        // What heuristic to use        
        System.out.print("Manhattan (0) or Custom (1) heuristc: ");
        heuristictype = scanner.nextInt();
    }   
        
    public static void makeGraph(Tile[][] graph) { 
        // Construct and initialize the tiles. 
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Tile t = new Tile();
                graph[i][j] = t;
                graph[i][j].tileType = map[i][j];   // Tile type based on above map
                graph[i][j].treestate = 0; // Initially unexplored
                graph[i][j].x = i;         // Each tile knows its location in array
                graph[i][j].y = j;
                graph[i][j].parentX = -1;  // Initially no parents on path fro start
                graph[i][j].parentY = -1;
                graph[i][j].inPath = false;    // Initially not in path from start
            }
        }
    }   
    

    // Main A* search method. Takes graph as parameter, and returns whether search successful
    public static int search(Tile[][] graph) {
        fringeCount = 1;
        boolean goalFound = false;
    
        // Add start state to tree, path cost = 0
        graph[startX][startY].treestate = 1;
        graph[startX][startY].gDist = 0;    
    
        // Loop until goal added to tree
        while (!goalFound) {
        
            // Get the best tile in the fringe
            Tile bestTile = getBest(graph);
            if (bestTile.x == -1) {
                // The default tile was returned, which means the fringe was empty.
                // This means the search has failed.
                System.out.print("Search failed!!!!!!\n");
                printGraph(graph);
                return 0;
            }
        
            // Otherwise, add that best tile to the tree (removing it from fringe)
            int x = bestTile.x;
            int y = bestTile.y;
            graph[x][y].treestate = 2;
            treeCount++;
        
            // If it is a goal, done!
            if (x == goalX && y == goalY) {
                goalFound = true;
                System.out.print("Found the goal!!!!!\n");
            
                // Compute the path taken and its cost, printing the explored graph,
                // the path  cost, and the number of tiles explored (which should be
                // as small as possible!)
                double cost = finalPath(graph);
                printGraph(graph);
                System.out.print("Path cost: " + cost+ "\n");
                System.out.print(treeCount + " tiles added to tree\n");
                System.out.print(fringeCount + " tiles added to fringe\n");
            
                return 1;
            }
        
            // Otherwise, we look at the 4 adjacent tiles to the one just added
            // to the tree (making sure each  is in the graph!) and either add it
            // to the tree or recheck its path.
            
            // Special cases for teleport pads
            if (x == t1x && y == t1y) {
                System.out.println("Teleport checked");
                checkAdjacent(graph, x, y, t2x, t2y);
            }
            if (x == t2x && y == t2y) {
                checkAdjacent(graph, x, y, t1x, t1y);
            }
                
            
            if (x > 0) { // Tile to left
                checkAdjacent(graph, x, y, x-1, y);
            }
            if (x < WIDTH-1) { // Tile to right
                checkAdjacent(graph, x, y, x+1, y);
            }
            if (y > 0) { // Tile above
                checkAdjacent(graph, x, y, x, y-1);
            }
            if (y < HEIGHT-1) { // Tile below
                checkAdjacent(graph, x, y, x, y+1);
            }
        
        }
        return 1;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        Tile[][] graph = new Tile[WIDTH][HEIGHT];
        makeGraph(graph);
        setProperties();
        search(graph);
  
    }
    
}
//}
