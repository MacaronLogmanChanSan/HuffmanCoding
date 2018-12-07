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
	private int bitSaved;
	private int numBitOutput;

	public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
		//throw new IOException("compress is not implemented");
		// implement force later!!!!!!!!!
		if(force || (!force && bitSaved >= 0)) {
			BitInputStream bis = new BitInputStream(in);
			BitOutputStream bos = new BitOutputStream(out);
			//int result = BITS_PER_INT * 2;
			// write the magic number
			bos.writeBits(BITS_PER_INT, MAGIC_NUMBER);
			// write the header format
			bos.writeBits(BITS_PER_INT, headerFormat);
			checkCompressionType(bos);
			// writing the actual compressed data
			int bit = bis.readBits(BITS_PER_WORD);
			while(bit != -1) {
				String code = tree.getCode(bit);
				for(int i = 0; i < code.length(); i++) {
					bos.writeBits(1, code.charAt(i));
				}
				bit = bis.readBits(BITS_PER_WORD);
			}
			// write code for eof
			String eofCode = tree.getCode(PSEUDO_EOF);
			for(int i = 0; i < eofCode.length(); i++) {
				bos.writeBits(1, eofCode.charAt(i));
			}
			bis.close();
			bos.close();
		}
		else {
			myViewer.showError("Compressed file has " + -bitSaved + " more bits than "
					+ "uncompressed file.\nSelect \"force compression\" option to compress.");
			return 0;
		}
		return numBitOutput;
	}
	
	private void checkCompressionType(BitOutputStream bos) {
		if(headerFormat == STORE_COUNTS) {
			for(int k = 0; k < IHuffConstants.ALPH_SIZE; k++) { 
				bos.writeBits(BITS_PER_INT, counts[k]); 
			} 
		}
		// if STF
		else if(headerFormat == STORE_TREE) {
			treeSize += tree.getSize() * (BITS_PER_WORD + 2); 
			bos.writeBits(BITS_PER_INT, treeSize);
			preorder(tree.getRoot(), bos);
		}
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
		
		numBitOutput = getNumBitOutput();
		bitSaved = result - numBitOutput;
		bis.close();
		return bitSaved;
	}

	/**
	 * returns the number of bits written to the compressed file
	 * @param in input stream to read from
	 * @return
	 * @throws IOException
	 */
	private int getNumBitOutput() throws IOException {
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
		for(int i = 0; i < counts.length; i++) {
			if(counts[i] != 0)
				result += counts[i] * tree.getCode(i).length();
		}
		// write code for eof
		String eofCode = tree.getCode(PSEUDO_EOF);
		result += eofCode.length();
		//bis.close();
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

			//holds number of bits tree is going to take up
			int size = bis.readBits(BITS_PER_INT);
			int[] count = new int[] {size};
			TreeNode root = restoreTree(null, count, bis);
			tree = new HuffTree(root);;
		}
		return decode(bis, bos);
	}


	private TreeNode restoreTree (TreeNode node, int[] count, BitInputStream in) throws IOException{
		if (count[0] < 0)
			throw new IOException("Error reading tree data.");
		if (count[0] == 0)
			return node;
		int bit = in.readBits(1);
		count[0]--;
		//node is a leaf with a value
		if (bit == 1) {
			count[0] -= BITS_PER_WORD + 1;
			bit = in.readBits(BITS_PER_WORD + 1);
			return new TreeNode(bit, 0);
		}
		//case for internal node
		else {
			TreeNode temp = new TreeNode(-1, 0);
			temp.setLeft(restoreTree(temp.getLeft(), count, in));
			temp.setRight(restoreTree(temp.getRight(), count, in));
			return temp;
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
	}

	private void showString(String s) {
		if(myViewer != null)
			myViewer.update(s);
	}
}
