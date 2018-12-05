import java.util.Map;
import java.util.HashMap;

public class HuffTree 
{
	private Map<Integer, String> values;
	private TreeNode root;
	
	private static final String ZERO = "0";
	private static final String ONE = "1";
	
	public HuffTree(TreeNode node)
	{
		values = new HashMap<>();
		root = node;
		map(root, "");
	}
	
	private void map(TreeNode node, String value)
	{
		if(node != null)
		{
			map(node.getLeft(), value + ZERO);
			if(node.isLeaf())
				values.put(node.getValue(), value);
			map(node.getRight(), value + ONE);
		}
	}
	
	public TreeNode getRoot() {
		return root;
	}
	// returns the size of values
	public int getSize() {
		return values.size();
	}
	public HashMap<Integer, String> getMappings()
	{
		return (HashMap<Integer,String>)values;
	}
	
	public String getCode(Integer chunk) {
		return values.get(chunk);
	}
}
