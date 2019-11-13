import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
public class BaseballElimination {
	private final int number;
	private SeparateChainingHashST<String, Integer> st;
	private int[][] data;
	private SeparateChainingHashST<String, ArrayList<String>> setR;
	private boolean[] isEliminated;
	private String[] slist;
	public BaseballElimination(String filename) {
		In in = new In(filename);
		number = Integer.parseInt(in.readLine());
		st = new SeparateChainingHashST<>();
		data = new int[number][number + 3];
		setR = new SeparateChainingHashST<String, ArrayList<String>>();
		isEliminated = new boolean[number];
		slist = new String[number];
		
		for (int i = 0; i < number; i++) {
			String s = in.readString();
			st.put(s, i);
			slist[i] = s;
			for (int j = 0; j < number + 3; j++)
				data[i][j] = (in.readInt());
		}
		int gameVertex = (number  - 1) * (number - 2) / 2;
		int teamVertex = number - 1;
		FlowNetwork net;
		FordFulkerson ford;		
		for (String s : slist) {
			net = new FlowNetwork(gameVertex + teamVertex + 2);
			int x = 1, current = st.get(s);
			if (isTrivial(current))
				continue;
			//handle game vertex
			for (int i = 0, i1 = 0; i < number - 1; i++) {
				if (i != current) {
					for (int j = i + 1, j1 = i1 + 1; j < number; j++) {
						if (j != current) {
							net.addEdge(new FlowEdge(0, x, data[i][j + 3]));
							net.addEdge(new FlowEdge(x, i1 + gameVertex + 1, Double.MAX_VALUE));
							net.addEdge(new FlowEdge(x, j1 + gameVertex + 1, Double.MAX_VALUE));
							x++;
							j1++;
						}
					}
					i1++;
				}
			}
			
			// handle team vertex
			ArrayList<String> bag = new ArrayList<>();
			for (int i = 0, i1 = 0; i < number; i++)
				if (i != current) {
					double bottle = Math.max(0.0, data[current][0] + data[current][2] - data[i][0]);
					net.addEdge(new FlowEdge(gameVertex + 1 + i1, net.V() - 1, bottle));
					bag.add(slist[i]);
					i1++;
				}			
			
			//handle isEliminated
			ford = new FordFulkerson(net, 0, gameVertex + teamVertex + 1);
			for (FlowEdge e : net.adj(0))
				if (e.flow() != e.capacity()) {
					isEliminated[current] = true;
					break;
				}
			
			// case2 handle certification
			if (isEliminated[current]) {
				for (int i = gameVertex + 1, i1 = 0; i < gameVertex + teamVertex + 1; i++, i1++) {
					if (i1 == current)
						i1++;
					if (!ford.inCut(i))
						bag.remove(slist[i1]);						
				}
				setR.put(s, bag);
			}
			else
				setR.put(s, null);
		}
	}
	private boolean isTrivial(int current) { 		
		ArrayList<String> bag = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			if (i != current && data[i][0] > data[current][0] + data[current][2]) {
				isEliminated[current] = true;
				bag.add(slist[i]);
				setR.put(slist[current], bag);
				return true;
			}
		}
		return false;
	}
	public int numberOfTeams() {
		return number;
	}
	public Iterable<String> teams() {
		return st.keys();
	}
	public int wins(String team) {
		if (team == null || !st.contains(team))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		return data[st.get(team)][0];
	}
	public int losses(String team) {
		if (team == null || !st.contains(team))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		return data[st.get(team)][1];
	}
	public int remaining(String team) {
		if (team == null || !st.contains(team))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		return data[st.get(team)][2];
	}
	public int against(String team1, String team2) {
		if (team1 == null || !st.contains(team1))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		if (team2 == null || !st.contains(team2))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		return data[st.get(team1)][st.get(team2) + 3];
	}
	public boolean isEliminated(String team) {
		if (team == null || !st.contains(team))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		return isEliminated[st.get(team)];
	}
	public Iterable<String> certificateOfElimination(String team) {
		if (team == null || !st.contains(team))
			throw new java.lang.IllegalArgumentException("Illegal !!!");
		return setR.get(team);
	}
	/*public static void main(String[] args) {
	    BaseballElimination division = new BaseballElimination(args[0]);
	    for (String team : division.teams()) {
	        if (division.isEliminated(team)) {
	            StdOut.print(team + " is eliminated by the subset R = { ");
	            for (String t : division.certificateOfElimination(team)) {
	                StdOut.print(t + " ");
	            }
	            StdOut.println("}");
	        }
	        else {
	            StdOut.println(team + " is not eliminated");
	        }
	    }
	}*/
}
