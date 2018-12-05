/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> and <NAME2), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID:
 *  email address:
 *  Grader name:
 *
 *  Student 2
 *  UTEID:
 *  email address:
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SimpleHuffProcessor implements IHuffProcessor {

	private IHuffViewer myViewer;
	private int[] counts;
	private HuffTree tree;
	private int headerFormat;
	private int treeSize;

	public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
		//throw new IOException("compress is not implemented");
		// implement force later!!!!!!!!!
		BitInputStream bis = new BitInputStream(in);
		BitOutputStream bos = new BitOutputStream(out);
		//int result = BITS_PER_INT * 2;
		// write the magic number
		bos.writeBits(BITS_PER_INT, MAGIC_NUMBER);
		// write the header format
		bos.writeBits(BITS_PER_INT, headerFormat);
		if(headerFormat == STORE_COUNTS) {
			for(int k = 0; k < IHuffConstants.ALPH_SIZE; k++) { 
				bos.writeBits(BITS_PER_INT, counts[k]); 
				//result += BITS_PER_INT;
			} 
		}
		// if STF
		// need to implement
		if(headerFormat == STORE_TREE) {
			treeSize += tree.getSize() * (BITS_PER_WORD + 2); 
			bos.writeBits(BITS_PER_INT, treeSize);
			//result += BITS_PER_INT;
			preorder(tree.getRoot(), bos);
			//result += bitCount(tree.getRoot());
		}
		// writing the actual compressed data
		int bit = bis.readBits(BITS_PER_WORD);
		while(bit != -1) {
			String code = tree.getCode(bit);
			for(int i = 0; i < code.length(); i++) {
				bos.writeBits(1, code.charAt(i));
				//result++;
			}
			bit = bis.readBits(BITS_PER_WORD);
		}
		// write code for eof
		String eofCode = tree.getCode(PSEUDO_EOF);
		for(int i = 0; i < eofCode.length(); i++) {
			bos.writeBits(1, eofCode.charAt(i));
			//result++;
		}
		bis.close();
		bos.close();
		return getNumBitOutput(in);
	}

	private void preorder(TreeNode node, BitOutputStream bos) {
		if(node != null) {
			if(!node.isLeaf()) {
				bos.writeBits(1, 0);
			}
			else {
				bos.writeBits(1, 1);
				bos.writeBits(BITS_PER_WORD + 1, node.getValue());
			}
			preorder(node.getLeft(), bos);
			preorder(node.getRight(), bos);
		}
	}

	private int bitCount(TreeNode node) {
		if(node != null) {
			if(!node.isLeaf()) {
				return 1 + bitCount(node.getLeft()) + bitCount(node.getRight());
			}
			else {
				return BITS_PER_WORD + 2;
			}
		}
		return 0;
	}
	
	
	public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
		//showString("Not working yet");
		//myViewer.update("Still not working");
		//throw new IOException("preprocess not implemented");
		this.headerFormat = headerFormat;
		counts = new int[ALPH_SIZE];
		BitInputStream bis = new BitInputStream(in);
		int bit = bis.readBits(BITS_PER_WORD);
		int result = 0;
		while(bit != -1) {
			result += BITS_PER_WORD;
			counts[bit]++;
			bit = bis.readBits(BITS_PER_WORD);
		}
		HuffQueue<TreeNode> hq = new HuffQueue<>();
		for(int i = 0; i < counts.length; i++) {
			if(counts[i] != 0) {				
				hq.enqueue(new TreeNode(i, counts[i]));
			}
		}
		hq.enqueue(new TreeNode(PSEUDO_EOF, 1));
		tree = new HuffTree(hq.makeTree());
		treeSize = hq.getNumInternalNode();
		//System.out.println(getNumBitOutPut(in));
		//System.out.println(result - getNumBitOutPut(in));
		bis.close();
		return result - getNumBitOutput(in);
	}

	/**
	 * returns the number of bits written to the compressed file
	 * @param in input stream to read from
	 * @return
	 * @throws IOException
	 */
	private int getNumBitOutput(InputStream in) throws IOException {
		in.reset();
		BitInputStream bis = new BitInputStream(in);
		int result = BITS_PER_INT * 2;
		// if scf
		if(headerFormat == STORE_COUNTS)
			result += BITS_PER_INT * ALPH_SIZE;

		// if STF
		// haven't implemented yet
		else {
			result += (BITS_PER_INT + bitCount(tree.getRoot()));
		}
		// writing the actual compressed data
		int bit = bis.readBits(BITS_PER_WORD);
		while(bit != -1) {
			String code = tree.getCode(bit);
			result += code.length();
			bit = bis.readBits(BITS_PER_WORD);
		}
		// write code for eof
		String eofCode = tree.getCode(PSEUDO_EOF);
		result += eofCode.length();
		return result;
	}

	public void setViewer(IHuffViewer viewer) {
		myViewer = viewer;
	}

	public int uncompress(InputStream in, OutputStream out) throws IOException {
		//throw new IOException("uncompress not implemented");
		BitInputStream bis = new BitInputStream(in);
		BitOutputStream bos = new BitOutputStream(out);
		// read magic number
		int magic = bis.readBits(BITS_PER_INT); 
		if (magic != MAGIC_NUMBER) {
			myViewer.showError("Error reading compressed file. \n" +
					"File did not start with the huff magic number.");
			return -1;
		}
		int store = bis.readBits(BITS_PER_INT);
		// can make a helper method with store as parameter
		// recreate counts
		if(store == STORE_COUNTS) {
			counts = new int[ALPH_SIZE];
			// recreate counts
			for(int k = 0; k < IHuffConstants.ALPH_SIZE; k++) { 
				int bits = bis.readBits(BITS_PER_INT); 
				counts[k] = bits; 
			}
			// make tree again from counts
			
			HuffQueue<TreeNode> hq = new HuffQueue<>();
			for(int i = 0; i < counts.length; i++) {
				if(counts[i] != 0) {				
					hq.enqueue(new TreeNode(i, counts[i]));
				}
			}
			hq.enqueue(new TreeNode(PSEUDO_EOF, 1));
			tree = new HuffTree(hq.makeTree());
		}
		else {
			//maybe can improve
			int size = bis.readBits(BITS_PER_INT);
			TreeNode node = null;
			while(size > 0) {
				int bits = bis.readBits(1);
				//System.out.println(bits);
				size--;
				if(bits == 0)
					node = restoreTree(node, -1);
				else {
					bits = bis.readBits(BITS_PER_WORD + 1);
					//System.out.println(bits);
					node = restoreTree(node, bits);
					size -= (BITS_PER_WORD + 1);
				}
				//System.out.println("size " + size);
			}
			tree = new HuffTree(node);
		}
		return decode(bis, bos);
	}

	private TreeNode restoreTree(TreeNode node, int value) throws IOException
	{
		if(node == null) {
			return new TreeNode(value, 0);
		}
		if(node.getValue() > 0)
			return node;
		else {
			if(!restoreTree(node.getLeft(), value).isLeaf()) {
					node.setLeft(restoreTree(node.getLeft(), value));
			}
			else {
				if(!restoreTree(node.getRight(), value).isLeaf())
					node.setRight(restoreTree(node.getRight(), value));
			}
			return node;
		}
	}
	
	
	private int decode(BitInputStream bis, BitOutputStream bos) throws IOException {
		boolean done = false;
		// get ready to walk tree, start at root
		TreeNode current = tree.getRoot();
		int result = 0;
		while(!done) {
			int bit = bis.readBits(1);
			// didn't find eof
			if(bit == -1)
				throw new IOException("Error reading compressed file. \n" +
						"unexpected end of input. No PSEUDO_EOF value.");
			else {
				System.out.println(current);
				// move left in tree if bit is 0
				if(bit == 0)
					current = current.getLeft();
				// else move right 
				else
					current = current.getRight();
				// if current is a leaf node
				if(current.isLeaf()) {
					// if value is eof value, we are done
					if(current.getValue() == PSEUDO_EOF)
						done = true;
				// else, write out value in leaf and go back to root of tree
					else {
						bos.writeBits(BITS_PER_WORD, current.getValue());
						result += BITS_PER_WORD;
						current = tree.getRoot();
					}
				}
			}
		}
		bis.close();
		bos.close();
		return result;
		/*get ready to walk tree, start at root
    	boolean done = false;
    	while(!done) 
        int bit = bitsIn.readBits(1);
        if(bit == -1)
            throw new IOException("Error reading compressed file. \n" +
                "unexpected end of input. No PSEUDO_EOF value.");
        else 
            move left or right in tree based on value of bit
            (move left if bit is 0, move right if bit is 1)
            if(reached a leaf node) {
                if(val is the pseudo end of file value)
                    done = true;
                else 
                    write out value in leaf to output
                    get back to root of tree*/
	}

	private void showString(String s){
		if(myViewer != null)
			myViewer.update(s);
	}
}
