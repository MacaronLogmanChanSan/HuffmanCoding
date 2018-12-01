import java.util.ArrayList;

public class HuffQueue
{
	private ArrayList<TreeNode> con;
	
	public HuffQueue()
	{
		con = new ArrayList<>();
	}
	
	public void enqueue(TreeNode val)
	{
		if(con.isEmpty())
			con.add(val);
		else
		{
			con.add(search(val), val);
		}
	}
	
    /**
     * O(log(N))
     * binary search through the elements for an element <br>
     * @param item the item to search for
     * @param location whether or not the user wants to know the desired location
     * @return the location of the element, the desired location if location is true, or -1 if not found
     */ 
    private int search(TreeNode item)
    {
    	//the lowest index
       	int low = 0;
       	
       	//the highest index
       	int high = con.size() - 1;
       	
       	//middle index
       	int middle = 0;
       	
       	//while the low and high don't cross values
       	while (low <= high)
       	{
       		middle = (low + high) / 2;
       		int difference = item.compareTo(con.get(middle));
       		
       		//item is found
        	if(difference == 0)
       		{
        		if(item.getValue() - con.get(middle).getValue() >= 0)
        			while(item.getValue() - con.get(middle).getValue() >= 0 && middle > 0 
        				&& item.compareTo(con.get(middle - 1)) == 0)
        				middle--;
        		else 			
		       		while(item.getValue() - con.get(middle).getValue() < 0 && middle < con.size()
		       			&& item.compareTo(con.get(middle)) == 0)
		       			middle++;        			
       			return middle;
       		}
        	//current element is greater than item
       		else if (difference < 0)
       	    	low = middle + 1;
        	//current element is smaller than item
       		else        		
        		high = middle - 1;
       	}

       	//item not found
       	return low;
    }
	
    public TreeNode dequeue()
    {
    	if(con.isEmpty())
    		return null;
    	return con.remove(con.size() - 1);
    }
    
    public TreeNode peek()
    {
    	if(con.isEmpty())
    		return null;
    	return con.get(con.size() - 1);
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
}
