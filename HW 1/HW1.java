import java.util.*;

public class HW1 {
	public static void main(String[] args) {
		//make initial state
		int[][] initialState = {{1,4,2},{5,3,0}};
		int[][] goalState = {{0, 1, 2}, {5, 4, 3}};
		//print2DArray(initialState);
		ArrayList<int[][]> myArrayList = adjacentNodes(initialState);
		//for(int i = 0; i < myArrayList.size(); i++ ) {
			//print2DArray(myArrayList.get(i));
			//System.out.println("------");
		//}
		dfs(initialState, goalState);
		//System.out.println(Arrays.toString(findZeroIndex(initialState)));
	}
	
	public static void print2DArray(int[][] initialState) {
		System.out.println(Arrays.toString(initialState[0]));
		System.out.println(Arrays.toString(initialState[1]));
	}
	//define movement
		//swap 0 with anything next to/above/below the 0 until you get to the goal state
	public static ArrayList<int[][]> adjacentNodes(int[][] currentState) {
		//keeps a record of states you can get to from currentState
		ArrayList<int[][]> validStates = new ArrayList<int[][]>();
		//find 0
		int[] zeroIndex = findZeroIndex(currentState);
		//check and see if you can go left and/or right
		if(zeroIndex[1] + 1 < currentState[0].length) { //right
			int[][] rightMovement = copy2D(currentState);
			//move non-empty number
			rightMovement[zeroIndex[0]][zeroIndex[1]] = rightMovement[zeroIndex[0]][zeroIndex[1]+1];
			//move empty space, represented as 0
			rightMovement[zeroIndex[0]][zeroIndex[1] + 1] = 0;
			//add to arrayList
			validStates.add(rightMovement);
		}
		if(zeroIndex[1] -1 >= 0 ) { //left
			int[][] leftMovement = copy2D(currentState);
			//move non-empty number
			leftMovement[zeroIndex[0]][zeroIndex[1]] = leftMovement[zeroIndex[0]][zeroIndex[1]-1];
			//move empty space, represented as 0
			leftMovement[zeroIndex[0]][zeroIndex[1] - 1] = 0;
			//add to arrayList
			validStates.add(leftMovement);
		}
		//check and see if you can go up and/or down
		if(zeroIndex[0] -1 >= 0) {// up
			int[][] upMovement = copy2D(currentState);
			//move non-empty number
			upMovement[zeroIndex[0]][zeroIndex[1]] = upMovement[zeroIndex[0]-1][zeroIndex[1]];
			//move empty space, represented as 0
			upMovement[zeroIndex[0] -1][zeroIndex[1]] = 0;
			//add to arrayList
			validStates.add(upMovement);
		}
		if(zeroIndex[0] + 1 < currentState.length) {// down
			int[][] downMovement = copy2D(currentState);
			//move non-empty number
			downMovement[zeroIndex[0]][zeroIndex[1]] = downMovement[zeroIndex[0]+1][zeroIndex[1]];
			//move empty space, represented as 0
			downMovement[zeroIndex[0] +1][zeroIndex[1]] = 0;
			//add to arrayList
			validStates.add(downMovement);
		}
		return validStates;
	}
	//find 0
	public static int[] findZeroIndex(int[][] currentState) {
		int i; int j; int[] zeroIndex = new int[2];
		for(i = 0; i < currentState.length; i++) {
			for(j = 0; j < currentState[0].length; j++) {
				if (currentState[i][j] == 0) {
					zeroIndex[0] = i;
					zeroIndex[1] = j;
					return zeroIndex;
				}
			}
		}
		return zeroIndex;
	}
	// copy a 2darray
	public static int[][] copy2D(int[][] currentState){
		int[][] movement = new int[currentState.length][];
		for(int i = 0; i < currentState.length; i++) {
			movement[i] = currentState[i].clone();
		}
		return movement;
	}
	//cost function
	//public int cost(int[][] currentState) { 
		
	//}
	//bfs
	// make an object to hold paths/objects and their histories
	
	public static void bfs(int[][] initialState, int[][] goalState) {
		ArrayList<State> bfsQueue = new ArrayList<State>();
		ArrayList<int[][]> seen = new ArrayList<int[][]>();
		ArrayList<State> empty = new ArrayList<State>();
		State initial = new State(initialState, empty);
		seen.add(initialState);
		bfsQueue.add(initial);
		while(!bfsQueue.isEmpty()) {
			//pop
			State frontOfQueue = bfsQueue.remove(0);
			if(Arrays.deepEquals(frontOfQueue.state, goalState)) {
				for(State st : frontOfQueue.history) {
					print2DArray(st.state);
					System.out.println("----");
				}
				print2DArray(goalState);
				break;
			}
			// check if adjacent states have been seen
			ArrayList<int[][]> neighbors = adjacentNodes(frontOfQueue.state);
			for(int i = 0; i < neighbors.size(); i++) {
				if(!seen.contains(neighbors.get(i))) { //if it hasn't been seen
					ArrayList<State> revisedHistory = new ArrayList<State>(frontOfQueue.history); //copy history of what's before it
					revisedHistory.add(frontOfQueue); //add to new history
					bfsQueue.add( new State(neighbors.get(i), revisedHistory));
					seen.add(neighbors.get(i));
				}
			}
		}
	}
	//uniform cost search
		// use a priority queue instead of a simple queue
		// insert nodes in the increasing order of the cost of the path so far
	//depth first search
	public static void dfs(int[][] initialState, int[][] goalState) {
		ArrayList<State> dfsQueue = new ArrayList<State>();
		ArrayList<State> seen = new ArrayList<State>();
		ArrayList<State> empty = new ArrayList<State>();
		State initial = new State(initialState, empty);
		seen.add(initial);
		dfsQueue.add(initial);
		while(!dfsQueue.isEmpty()) {
			//pop

			State endOfQueue = dfsQueue.remove(dfsQueue.size()-1);
			if(Arrays.deepEquals(endOfQueue.state, goalState)) {
				for(State st : endOfQueue.history) {
					print2DArray(st.state);
					System.out.println("----");
				}
				print2DArray(goalState);
				break;
			}
			// check if adjacent states have been seen
			ArrayList<int[][]> neighbors = adjacentNodes(endOfQueue.state);
			for(int i = 0; i < neighbors.size(); i++) {
				ArrayList<State> revisedHistory = new ArrayList<State>(endOfQueue.history); //copy history of what's before it
				revisedHistory.add(endOfQueue); //add to new history
				State neighborState = new State(neighbors.get(i), revisedHistory);
				if(!seen.contains(neighborState)) { //if it hasn't been seen
					dfsQueue.add(neighborState);
					seen.add(neighborState);
				}
			}
		}
	}
	//iterative deepening
		// search depth first, but terminate a path either if a goal state is found or if the maximum depth allowed is reached
}
