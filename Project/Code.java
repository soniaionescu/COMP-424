package student_player;

import java.util.Collections;
import java.util.List;

import boardgame.Move;
import boardgame.Player;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {
	private class MoveScore {
		Move Move;
		int Score;
	}

    private boolean IsFirstMove = true;
    private MoveScore BestMove;
    private int subMovesEvaluated = 0;
    private int depthsEvaluated = 0;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260665763");
    }

    public StudentPlayer(String name) {
        super(name);
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();

        // Set an initial best move as some random one.
        //Move myBestMove = boardState.getRandomMove();

        int moveTimeoutInSeconds = 2;
        subMovesEvaluated = 0;
        depthsEvaluated = 0;

        BestMove = new MoveScore();
        BestMove.Move = null;
        BestMove.Score = Integer.MIN_VALUE;

        if (IsFirstMove) {
        	moveTimeoutInSeconds = 2; //Change this to 30
        	IsFirstMove = false;
        }

        long startTime = System.currentTimeMillis();
        double availableRunTime = moveTimeoutInSeconds * 1000 * 0.95; // Allow 95% time for traversing depth so we have some time to get final answer and reply to server.
        double expiryTime =  startTime + availableRunTime;

        final TablutBoardState parameterBoardState = boardState;
        final long parameterStartTime = startTime;
        final double parameterExpiryTime = expiryTime;
        Thread iterativeDeepeningRun = new Thread(new Runnable() {
            public void run() {
            	IterativeDeepeningWithAlphaBeta(parameterBoardState, player_id, Integer.MIN_VALUE, Integer.MAX_VALUE, parameterStartTime, parameterExpiryTime);
            }});

        iterativeDeepeningRun.start();

        try {
        	Thread.sleep((long)availableRunTime);
        }
        catch(Exception e) {

        }
        iterativeDeepeningRun.stop();

        System.out.println("Move predicted in: " + (System.currentTimeMillis()-startTime)/1000.0 + " seconds" + BestMove.Move.toPrettyString() + " after evaluating " + subMovesEvaluated + " moves upto depth " + depthsEvaluated + " with score " + BestMove.Score);

        // Return your move to be processed by the server.
        return BestMove.Move;
    }

    private void IterativeDeepeningWithAlphaBeta(TablutBoardState bs, int maximizingPlayer, int alpha, int beta, long startTime, double expiryTime) {
    	int depthToTraverse = 1;

    	while(true) {
    		MoveScore possibleBestMove = MinimaxWithAlphaBeta(depthToTraverse, bs, maximizingPlayer, alpha, beta, System.currentTimeMillis(), expiryTime);

    		if (possibleBestMove.Score > BestMove.Score) {
    			BestMove.Score = possibleBestMove.Score;
    			BestMove.Move = possibleBestMove.Move;
    		}

    		depthToTraverse++;
    	}
    }

	private MoveScore MinimaxWithAlphaBeta(int depth, TablutBoardState bs, int maximizingPlayer, int alpha, int beta, long startTime, double expiryTime) {
		double availableTime = expiryTime - startTime;
		MoveScore minimaxMoveScore = new MoveScore();
		MoveScore bestMoveScore = new MoveScore();
		subMovesEvaluated++;
		depthsEvaluated = Math.max(depth, depthsEvaluated);

    	List<TablutMove> possibleMoves = bs.getAllLegalMoves();
    	Collections.shuffle(possibleMoves);

    	//for(int i=0;i<6-depth;i++) System.out.print("\t");
    	//System.out.println("MinimaxWithAlphaBeta: depth: " + depth + " startTime: " + startTime + " expiryTime: " + expiryTime + " currentTime: " + System.currentTimeMillis() + " possibleMoves: " + possibleMoves.size() + " availableTime: " + availableTime);

        if (possibleMoves.isEmpty() || System.currentTimeMillis() >= expiryTime || depth <= 0 || bs.getWinner() == player_id) {
        	minimaxMoveScore.Score = calculatePayoff(bs, maximizingPlayer, depth);
        	minimaxMoveScore.Move = null;
        	//for(int i=0;i<6-depth;i++) System.out.print("\t");
        	//System.out.println("Move selected with score: " + minimaxMoveScore.Score);
        	return minimaxMoveScore;
        }

        double availableTimePerMove = availableTime / possibleMoves.size();

    	if (player_id == maximizingPlayer) {
    		bestMoveScore.Score = Integer.MIN_VALUE;

            for (TablutMove possibleMove : possibleMoves) {
            	TablutBoardState cloneBS = (TablutBoardState) bs.clone();
            	cloneBS.processMove(possibleMove);

            	long subTreeStartTime = System.currentTimeMillis();
            	double subTreeExpiryTime = subTreeStartTime + availableTimePerMove;

            	//for(int i=0;i<6-depth;i++) System.out.print("\t");
            	//System.out.println("Subtree: startTime " + subTreeStartTime + " expiry time: " + subTreeExpiryTime + " for move: " + possibleMove.toPrettyString());
            	minimaxMoveScore = MinimaxWithAlphaBeta(depth - 1, cloneBS, 1 - maximizingPlayer, alpha, beta, subTreeStartTime, subTreeExpiryTime);

            	if (minimaxMoveScore.Score > bestMoveScore.Score) {
            		bestMoveScore.Score = minimaxMoveScore.Score;
            		bestMoveScore.Move = possibleMove;
            	}

            	alpha = Math.max(alpha, bestMoveScore.Score);

            	//long subTreeEndTime = System.currentTimeMillis();
            	//for(int i=0;i<6-depth;i++) System.out.print("\t");
            	//System.out.println("Subtree: endTime " + subTreeEndTime + " expiry time: " + subTreeExpiryTime + " for move: " + possibleMove.toPrettyString());

            	if (alpha >= beta) {
            		break;
            	}
            }

            return bestMoveScore;
    	}
    	else {
    		bestMoveScore.Score = Integer.MAX_VALUE;

            for (TablutMove possibleMove : possibleMoves) {
            	TablutBoardState cloneBS = (TablutBoardState) bs.clone();
            	cloneBS.processMove(possibleMove);

            	long subTreeStartTime = System.currentTimeMillis();
            	double subTreeExpiryTime = subTreeStartTime + (availableTime / possibleMoves.size());

            	//for(int i=0;i<6-depth;i++) System.out.print("\t");
            	//System.out.println("Subtree: startTime" + subTreeStartTime + " expiry time: " + subTreeExpiryTime + " for move: " + possibleMove.toPrettyString());
            	minimaxMoveScore = MinimaxWithAlphaBeta(depth - 1, cloneBS, 1 - maximizingPlayer, alpha, beta, subTreeStartTime, subTreeExpiryTime);

            	if (minimaxMoveScore.Score < bestMoveScore.Score) {
            		bestMoveScore.Score = minimaxMoveScore.Score;
            		bestMoveScore.Move = possibleMove;
            	}

            	beta = Math.min(beta, bestMoveScore.Score);

            	//long subTreeEndTime = System.currentTimeMillis();
            	//for(int i=0;i<6-depth;i++) System.out.print("\t");
            	//System.out.println("Subtree: endTime" + subTreeEndTime + " expiry time: " + subTreeExpiryTime + " for move: " + possibleMove.toPrettyString());

            	if (alpha >= beta) {
            		break;
            	}
            }

            return bestMoveScore;
    	}
    }

    private int calculatePayoff(TablutBoardState bs, int maximizingPlayer, int depth) {
        int payOff = 0;
        payOff -= bs.getNumberPlayerPieces(1 - player_id) * 30;
        payOff += bs.getNumberPlayerPieces(player_id) * 60;

        try {
               // Some times getKingPosition returns null as the king position
               // which is weird but its not in our control so lets protect ourselves
               // so this thing doesn't crash and cause random move to be selected instead.
               Coord kingPos = bs.getKingPosition();
               int minDistanceToEnd = Coordinates.distanceToClosestCorner(kingPos);
               //Lower value of minDistanceToEnd is closer to corner
               if (player_id == TablutBoardState.SWEDE) {
                      payOff += (50 - minDistanceToEnd);
               }
               else {
                      payOff -= (50 - minDistanceToEnd);
               }
        }
        catch(Exception e) {
        }

        if (bs.getWinner() == player_id) {
               payOff = 50000; // if we are winning then set specific payoff so the number of pieces dont affect the score.
        }
        else if (bs.getWinner() == 1 - player_id) {
               payOff = -50000;
        }

        return payOff;
    }
}
