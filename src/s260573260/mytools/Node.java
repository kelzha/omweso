package s260573260.mytools;

import java.util.ArrayList;
import java.util.List;

import omweso.CCMove;

/**Simple wrapper of move and value for vanilla alphabeta
 * Not actually a tree node
 */
public class Node {
	private int aValue;
	private CCMove aMove;
	
    public Node(int pValue, CCMove pMove) {
        aValue = pValue;
        aMove = pMove;
    }
    
    public CCMove getMove(){
    	return aMove;
    }
    
    public void setMove(CCMove pMove){
    	aMove = pMove;
    }
    
    public int getValue(){
    	return aValue;
    }
    
    public void setValue(int temp){
    	aValue = temp;
    }
}
