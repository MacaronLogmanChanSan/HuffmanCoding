import java.util.ArrayList;

public class HuffQueue<E extends Comparable<? super E>> {
	private ArrayList<E> con;
	
	public HuffQueue (){
		con = new ArrayList<>();
	}
	
	public int size() {
		return con.size();
	}
	
	public void add (E val) {
		int index = search(val);
		if (index > con.size() || index < 0)
			System.out.println(index + ": " + con.size());
		con.add(search(val), val); 
	}
	
	private int search(E item) {
    	int low = 0;
    	int high = con.size() - 1; 
        while (low <= high) 
        { 
            int mid = low + (high - low) / 2;
            // Check if item is present at mid 
            if (con.get(mid).compareTo(item) == 0) {
            	boolean isEqual = true;
            	while (mid > 0 && isEqual) {
            		isEqual = con.get(mid).compareTo(con.get(mid - 1)) == 0;
            		mid--;
            	}
            	if (!isEqual)
            		return mid + 1;
            	return mid;	
            }
            // If item is smaller, ignore left half 
            if (con.get(mid).compareTo(item) > 0) 
                low = mid + 1; 
            // If item is greater, ignore right half 
            else
                high = mid - 1; 
        }
        return low;
    }
	
	public E dequeue() {
		if (con.size() == 0)
			return null;
		return con.remove(con.size() - 1);
	}
	
	public E peek() {
		if (con.size() == 0)
			return null;
		return con.get(con.size() - 1);
	}
	
	public String printList() {
		StringBuilder s = new StringBuilder();
		for (int i = con.size() - 1; i >= 0; i--) {
			s.append(con.get(i).toString());
			s.append("\n");
		}
		return s.toString();
	}
	
}
