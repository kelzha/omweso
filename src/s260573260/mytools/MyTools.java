package s260573260.mytools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import boardgame.BoardState;
import omweso.CCBoard;
import omweso.CCBoardState;
import omweso.CCMove;
import omweso.CCMove.MoveType;

public class MyTools {
	/**Helper function to just get the total number of seeds on a side of the board*/
	public static int getTotalSeeds(int[] pits){
    	int num_seeds = 0;
    	int pit = 0;
    	for(int j = 0; j < 2 * CCBoardState.SIZE; j++){
            num_seeds += pits[j];
        }
    	return num_seeds;
    }
    
	/**Simulates from the current state until a player wins and returns 1 if the board owner wins*/
    public static int simulate(CCBoardState current){
    	while(!current.gameOver()){
    		ArrayList<CCMove> moves = current.getLegalMoves();
        	Random rand = new Random();
			int random = rand .nextInt(moves.size());
        	current.move(moves.get(random));
    	}
    	if(current.getWinner() == 0){
    		return 1;
    	} else{
    		return 0;
    	}
    }
    
    /**The full montecarlo algorithm.*/
    public static void monteCarloTraversal(MonteCarloNode root, int numberOfSimulations){
    	
    	//When we hit the root, we simulate the given number of simulations using the above simulate function
    	//We then save the number of successes and total trials into the node and propagate it up to the root with the while loop below.
    	if(root.getChildren().size() == 0){
    		int numberOfSuccess = 0;
    		for(int i = 0; i < numberOfSimulations; i++){
    			CCBoardState current = (CCBoardState) root.getBoardState().clone();
    			if(simulate(current) == 1){
    				numberOfSuccess++;
    			}
    		}
    		root.setSimulationSuccess(root.getSimulationSuccess() + numberOfSuccess);
    		root.setSimulationTotal(root.getSimulationTotal() + numberOfSimulations);
    		while(root.getParent() != null){
    			root = root.getParent();
    			root.setSimulationSuccess(root.getSimulationSuccess() + numberOfSuccess);
    			root.setSimulationTotal(root.getSimulationTotal() + numberOfSimulations);
    		}
    		return;
    	}
    	
    	//Recursively loops over all children until the root
    	for(MonteCarloNode node : root.getChildren()){
    		monteCarloTraversal(node, numberOfSimulations);
    	}
    }
    
    /**Function call that the agent would use if it were to be used
     *After many trials, I found that alpha-beta to depth 10 performed better than montecarlo
     *since montecarlo was too slow after around depth 4 and 80 simulations.  Ultimately
     * montecarlo didn't give enough information and failed to do better.
     */
    public static MonteCarloNode montecarlo(CCBoardState bs, int maxdepth){
    	MonteCarloNode root = new MonteCarloNode(-200, bs, new CCMove(), null);
    	root = montealphabeta(bs, root, -200, 200, true, maxdepth);
    	monteCarloTraversal(root, 80);
    	return root;
    }
    
    /**Trial heuristic function I tried, but ended up performing worse than the simple one
     *Right after a capture, the spaces on the opposing side of the board are unoccupied
     *so for states like these, we add double the weight since we just captured their stones.
     */
    public static int evaluate1(int myboard[], int yourboard[]){
    	int size = CCBoardState.SIZE;
    	int value = 0;
    	value += getTotalSeeds(myboard) - getTotalSeeds(yourboard);
    	
    	for(int i = 0; i < CCBoardState.SIZE; i++){
    		if(myboard[i] == 0  && myboard[i + size] != 0 && yourboard[i] == 0 && yourboard[i + size] == 0){
    			value += myboard[i + size];
    		} else if(myboard[i] != 0  && myboard[i + size] != 0 && yourboard[i] == 0 && yourboard[i + size] == 0){
    			value += myboard[i] + myboard[i + size];
    		} else if(myboard[i] == 0  && myboard[i + size] == 0 && yourboard[i] != 0 && yourboard[i + size] == 0){
    			value -= yourboard[i];
    		} else if(myboard[i] == 0  && myboard[i + size] == 0 && yourboard[i] != 0 && yourboard[i + size] != 0){
    			value -= yourboard[i] + yourboard[i + size];
    		}
    	}
    	
		return value;
    }
    
    /**Simple heuristic function that is myseeds - opponentsseeds that ended up outperforming others*/
    public static int evaluate2(int myboard[], int yourboard[]){
    	return getTotalSeeds(myboard) - getTotalSeeds(yourboard);
    }
    
    /**Main recursive alphabeta/minimax algorithm
     *Currently tested to reliably go to depth 10 without going over time (On a fully filled board)
     *The flag p2t1 at the end is to indicate the bug of player 2 not updating the turn properly on his/her first normal turn.
     *In this case, we use our own extended boardstate that fixes the bug to read moves accurately and significantly boosted
     *win% as player2.  Finally, this returns a Node wrapper that contains the next move to take.
     */
    public static Node alphabeta(CCBoardState bs, int alpha, int beta, boolean minmax, int maxdepth, boolean p2t1){
    	//Terminating condition, runs evaluation function above
    	if(maxdepth == 0 || bs.gameOver()){
    		return new Node(MyTools.evaluate2(bs.getBoard()[0], bs.getBoard()[1]), new CCMove());
    	}
    	
    	//Finds legal moves to loop over
        ArrayList<CCMove> moves = bs.getLegalMoves();
        
        //minimax = value at current iteration that is propagated up
        int minimax;
        if(minmax){
        	minimax = -10*bs.NUM_INITIAL_SEEDS;
        } else{
        	minimax = 10*bs.NUM_INITIAL_SEEDS;
        }
        
        //The move to take to get to the best choice in the next part of the tree
        //Needed so that ultimately at the root, we can simply take this
        Node moveToMake = new Node(-200, new CCMove());
        
        //Loops over all possible moves
        for(CCMove move : moves){
        	CCBoardState bstemp;
        	
        	//Clones MyBoardState if it's player 2 turn 1 and otherwise CCBoardState
        	if(p2t1){
        		bstemp = (MyBoardState) bs.clone();
        	} else{
        		bstemp = (CCBoardState) bs.clone();
        	}
        	
        	//Main minimax traversal.  If minmax = 1, then max otherwise min
        	if(minmax){
        		
        		//Make a move then recursively search through the children states
        		bstemp.move(move);
        		int temp = alphabeta(bstemp, alpha, beta, !minmax, maxdepth - 1, p2t1).getValue();
        		
        		//Update the value of minimax if the above call gives something greater since we are in max 
        		if(temp > minimax){
        			minimax = temp;
        			moveToMake.setValue(temp);
        			moveToMake.setMove(move);
        		}
        		
        		//Pruning happens here
        		alpha = Math.max(minimax, alpha);
        		if(beta <= alpha){
        			break;
        		}
        	} else{
        		//Basically the same thing as above here, except with min
        		bstemp.move(move);
        		int temp = alphabeta(bstemp, alpha, beta, !minmax, maxdepth - 1, p2t1).getValue();
        		if(temp < minimax){
        			minimax = temp;
        			moveToMake.setValue(temp);
        			moveToMake.setMove(move);
        		}
        		beta = Math.min(beta, minimax);
        		if(beta <= alpha){
        			break;
        		}
        	}
        	
        	//If somehow moveToMake doesn't get updated, we take a random move
        	if(moveToMake.getMove().getMoveType() == MoveType.NOTHING){
        		Random rand = new Random();
				int randomMove = rand .nextInt(moves.size());
        		moveToMake.setMove(moves.get(randomMove));
        	}
        }
        return moveToMake;
    }
    
    /**This function was used for montecarlosearch since we needed to maintain the tree structure for montecarlo
     * Ultimately, creating that many node objects had a huge huge overhead in time cost so montecarlo was quite inefficient.
     * Probably could have been implemented in a more time efficient way.
     * The algorithm here is the same as above, except we save everything into a MonteCarloNode which we keep recursively passing
     * down and updating with relevant values.
     */
    public static MonteCarloNode montealphabeta(CCBoardState bs, MonteCarloNode parent, int alpha, int beta, boolean minmax, int maxdepth){
    	if(maxdepth == 0 || bs.gameOver()){
    		return new MonteCarloNode(evaluate2(bs.getBoard()[0], bs.getBoard()[1]), bs, new CCMove(), parent);
    	}
        ArrayList<CCMove> moves = bs.getLegalMoves();
        int minimax;
        if(minmax){
        	minimax = -200;
        } else{
        	minimax = 200;
        }
        for(CCMove move : moves){
        	CCBoardState bstemp = (CCBoardState) bs.clone();
        	if(minmax){
        		bstemp.move(move);
        		MonteCarloNode nextMove = new MonteCarloNode(-200, bs, move, parent);
        		int temp = montealphabeta(bstemp, nextMove, alpha, beta, !minmax, maxdepth - 1).getValue();
        		nextMove.setValue(temp);
        		parent.addChildren(nextMove);
        		if(temp > minimax){
        			minimax = temp;
        			parent.setValue(temp);
        			parent.setMove(move);
        		}
        		alpha = Math.max(minimax, alpha);
        		if(beta <= alpha){
        			break;
        		}
        	} else{
        		bstemp.move(move);
        		MonteCarloNode nextMove = new MonteCarloNode(-200, bs, move, parent);
        		int temp = montealphabeta(bstemp, nextMove, alpha, beta, !minmax, maxdepth - 1).getValue();
        		nextMove.setValue(temp);
        		parent.addChildren(nextMove);
        		if(temp < minimax){
        			minimax = temp;
        			parent.setValue(temp);
        			parent.setMove(move);
        		}
        		beta = Math.min(beta, minimax);
        		if(beta <= alpha){
        			break;
        		}
        	}
        	if(parent.getMove().getMoveType() == MoveType.NOTHING){
        		Random rand = new Random();
        		int randomMove = rand.nextInt(moves.size());
        		parent.setMove(moves.get(randomMove));
        	}
        }
        return parent;
    }
}














