import java.util.ArrayList;

public class HuffQueue<E extends Comparable<? super E>>
{
	private ArrayList<TreeNode> con;

	public HuffQueue()
	{
		con = new ArrayList<>();
	}

	public void enqueue(TreeNode val) {
		
		int index = con.size() - 1;
		while(index >= 0 && val.compareTo(con.get(index)) >= 0)
			index--;
		con.add(index + 1, val);
	}

	public TreeNode dequeue()
	{
		if(con.isEmpty())
			return null;
		return con.remove(con.size() - 1);
	}

	/*public E peek()
	{
		if(con.isEmpty())
			return null;
		return con.get(con.size() - 1);
	}*/

	public TreeNode makeTree() {
		while(con.size() > 1) {
			TreeNode left = dequeue();
			TreeNode right = dequeue();
			enqueue(new TreeNode(left, left.getFrequency() + right.getFrequency(), right));	
		}
		return con.get(0);
	}
	
	public String toString()
	{
		final char OPEN = '[';
		final char CLOSE = ']';
		final String COMMA = ", ";

		StringBuilder result = new StringBuilder();
		result.append(OPEN);
		for(int i = con.size() - 1; i >= 0; i--)
		{
			result.append(con.get(i));
			if(i != 0)
				result.append(COMMA);
		}
		result.append(CLOSE);
		return result.toString();
	}



	public void printQueue() {
		System.out.println(con);
	}



}
