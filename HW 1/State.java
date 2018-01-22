import java.util.ArrayList;
import java.util.Arrays;

public class State{
		
		int[][] state;
		ArrayList<State> history;
		
		public State(int[][] stateData, ArrayList<State> historyData) {
			this.state = stateData;
			this.history = new ArrayList(historyData);
		}
		
		/*
		 * State st = new State(numbers, new ArrayList<int[][]>());
		 * st.state
		 * st.history
		 */
		@Override
		public boolean equals(Object other) {
			if(other instanceof State) {
				State toCompare = (State) other;
				return Arrays.deepEquals(this.state, toCompare.state);
			}
			else {
				return false;
			}
		}
		public int hashCode() {
			return this.state.hashCode();
		}
	}