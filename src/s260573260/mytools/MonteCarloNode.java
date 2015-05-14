package s260573260.mytools;

import java.util.ArrayList;
import java.util.List;

import omweso.CCBoardState;
import omweso.CCMove;

public class MonteCarloNode {
	private int aValue; /**The value of the node from minimax*/
	private int aSimulTotal = 0; /**The total simulations*/
	private int aSimulSuccess = 0; /**The number of successful simulations*/
	private CCMove aArrivalMove; /**The move that this node's parent took to get here*/
	private CCMove aMoveToTake = null; /**The move this node will need to take for alphabeta*/
	private CCBoardState aBoardState; /**State of the board currently*/
	MonteCarloNode aParent; /**Parent node*/
	private ArrayList<MonteCarloNode> aChildren = new ArrayList<MonteCarloNode>(); /**Children nodes*/
	
	/**The rest of the functions here are all getters and setters*/
    public MonteCarloNode(int pValue, CCBoardState pBoardState, CCMove pArrivalMove, MonteCarloNode pParent) {
        aValue = pValue;
        aArrivalMove = pArrivalMove;
        aBoardState = pBoardState;
        aParent = pParent;
    }
    
    public void addChildren(MonteCarloNode pChild){
    	aChildren.add(pChild);
    }
    
    public ArrayList<MonteCarloNode> getChildren(){
    	return aChildren;
    }
    
    public MonteCarloNode getParent(){
    	return aParent;
    }
    
    public CCBoardState getBoardState(){
    	return aBoardState;
    }
    
    public CCMove getMove(){
    	return aMoveToTake;
    }
    
    public void setMove(CCMove pMove){
    	aMoveToTake = pMove;
    }
    
    public CCMove getArrival(){
    	return aArrivalMove;
    }
    
    public int getValue(){
    	return aValue;
    }
    
    public void setValue(int temp){
    	aValue = temp;
    }
    
    public int getSimulationTotal(){
    	return aSimulTotal;
    }
    
    public void setSimulationTotal(int pTotal){
    	aSimulTotal = pTotal;
    }
    
    public int getSimulationSuccess(){
    	return aSimulSuccess;
    }
    
    public void setSimulationSuccess(int pSuccess){
    	aSimulSuccess = pSuccess;
    }
}
