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
			if(node.getValue() != 0)
				values.put(node.getValue(), value);
			map(node.getRight(), value + ONE);
		}
	}
	
	public HashMap<Integer, String> getMappings()
	{
		return (HashMap<Integer,String>)values;
	}
	

}
