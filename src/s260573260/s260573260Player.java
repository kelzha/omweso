package s260573260;

import boardgame.Board;
import boardgame.BoardState;
import boardgame.Move;
import boardgame.Player;

import java.util.ArrayList;
import java.util.Random;

import omweso.CCBoard;
import omweso.CCBoardState;
import omweso.CCBoardState.Direction;
import omweso.CCMove;
import omweso.CCMove.MoveType;
import s260573260.mytools.MonteCarloNode;
import s260573260.mytools.MyBoardState;
import s260573260.mytools.MyTools;
import s260573260.mytools.Node;

/** Kelley Zhao, ID:260573260*/
public class s260573260Player extends Player {
    Random rand = new Random();

    public s260573260Player() { super("260573260"); }
    public s260573260Player(String s) { super(s); }

    public Board createBoard() { return new CCBoard(); }

    /** Use this method to take actions when the game is over. */
    public void gameOver( String msg, BoardState bs) {
        CCBoardState board_state = (CCBoardState) bs;

        if(board_state.haveWon()){
            System.out.println("260573260 won!");
        }else if(board_state.haveLost()){
            System.out.println("260573260 lost!");
        }else if(board_state.tieGame()){
            System.out.println("Draw!");
        }else{
            System.out.println("Undecided!");
        }
    }  

    /** Implements the algorithm the agent runs
     * Currently this is alphabeta to depth 10.
     */
    public Move chooseMove(BoardState bs){
    	CCBoardState board_state = (CCBoardState) bs;   	
        int[][] pits = board_state.getBoard();

        int[] my_pits = pits[0];
        int[] op_pits = pits[1];
        if(!board_state.isInitialized()){
            int[] initial_pits = new int[2 * CCBoardState.SIZE];

            int num_seeds = CCBoardState.NUM_INITIAL_SEEDS;

            //I found this configuration to have the most success, contributing many times to player 2 wins even at a disadvantage
            if(board_state.playFirst()){
            	initial_pits[7] = 17;
            	initial_pits[9] = 3;
            	initial_pits[10] = 3;
            	initial_pits[11] = 3;
            	initial_pits[12] = 3;
            	initial_pits[13] = 3;
            }else{
            	initial_pits[7] = 17;
            	initial_pits[9] = 3;
            	initial_pits[10] = 3;
            	initial_pits[11] = 3;
            	initial_pits[12] = 3;
            	initial_pits[13] = 3;
            }

            return new CCMove(initial_pits);
        }else{
        	Node result;
        	/*Checks if it is player 2 turn 1, and constructs a new MyBoardState very similar to CCBoardState, except with
        	 * turn number = 2.  Also sets the flag for alphabeta to true.
        	 */
        	if(board_state.getTurnsPlayed() == 1 && !board_state.playFirst()){
        		MyBoardState myBoardState = new MyBoardState(board_state.getBoard(), 2, board_state.getWinner(), 0, 0);
        		result = MyTools.alphabeta(myBoardState, -200, 200, true, 10, true);
        	} else{
        		result = MyTools.alphabeta(board_state, -200, 200, true, 10, false);
        	}
        	
        	//Simply returns the move here
            return result.getMove();
        }
    }
}
