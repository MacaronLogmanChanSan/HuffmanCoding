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

	public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
		//throw new IOException("compress is not implemented");
		// implement force later!!!!!!!!!
		BitInputStream bis = new BitInputStream(in);
		BitOutputStream bos = new BitOutputStream(out);
		int result = BITS_PER_INT * 2;
		// write the magic number
		bos.writeBits(BITS_PER_INT, MAGIC_NUMBER);
		// write the header format
		bos.writeBits(BITS_PER_INT, headerFormat);
		if(headerFormat == STORE_COUNTS) {
			for(int k = 0; k < IHuffConstants.ALPH_SIZE; k++) { 
				bos.writeBits(BITS_PER_INT, counts[k]); 
				result += BITS_PER_INT;
			} 
		}
		// if STF
		// blah blah
		// writing the actual compressed data
		int bit = bis.readBits(BITS_PER_WORD);
		while(bit != -1) {
			String code = tree.getCode(bit);
			for(int i = 0; i < code.length(); i++) {
				bos.writeBits(1, code.charAt(i));
				result++;
			}
			bit = bis.readBits(BITS_PER_WORD);
		}
		// write code for eof
		String eofCode = tree.getCode(PSEUDO_EOF);
		for(int i = 0; i < eofCode.length(); i++) {
			bos.writeBits(1, eofCode.charAt(i));
			result++;
		}
		return result;
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
		System.out.println(getNumBitOutPut(in));
		System.out.println(result - getNumBitOutPut(in));
		return result - getNumBitOutPut(in);
	}

	/**
	 * returns the number of bits written to the compressed file
	 * @param in input stream to read from
	 * @return
	 * @throws IOException
	 */
	private int getNumBitOutPut(InputStream in) throws IOException {
		in.reset();
		BitInputStream bis = new BitInputStream(in);
		int result = BITS_PER_INT * 2;
		// if scf
		result += BITS_PER_INT * ALPH_SIZE;

		// if STF
		// haven't implemented yet
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
		throw new IOException("uncompress not implemented");
		/*BitInputStream bis = new BitInputStream(in);
		// read magic number
		int magic = bis.readBits(BITS_PER_INT); 
		if (magic != MAGIC_NUMBER) {
		    myViewer.showError("Error reading compressed file. \n" +
		            "File did not start with the huff magic number.");
		    return -1;
		}
		// recreate counts
		for(int k=0; k < IHuffConstants.ALPH_SIZE; k++) { 
    		int bits = in.readBits(BITS_PER_INT); 
    		myCounts[k] = bits; 
		}	
		 */
		//return 0;
	}

	private void showString(String s){
		if(myViewer != null)
			myViewer.update(s);
	}
}
