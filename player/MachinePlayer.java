/* MachinePlayer.java */

package player;

import list.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 *
 *  @author parthivm
 *  @author victors
 */

public class MachinePlayer extends Player {

  private char myColor;
  private char oppColor;
  private int searchDepth = 3;
  private Board board;
  private int pieces = 10;

  //  Creates a machine player with the given color.  Color is either 0 (black)
  //  or 1 (white).  (White has the first move.)
  /**
   *  @param color the int representing the color of the player
   */
  public MachinePlayer(int color) {
    if (color == 0) {
      myColor = 'B';
      oppColor = 'W';
    } else {
      myColor = 'W';
      oppColor = 'B';
    }
    this.board = new Board();
  }

  //  Creates a machine player with the given color and search depth.  Color is
  //  either 0 (black) or 1 (white).  (White has the first move.)
  /**
   *  @param color
   *  @param searchDepth
   */
  public MachinePlayer(int color, int searchDepth) {
    this(color);
    this.searchDepth = searchDepth;
  }

  //  Checks whether a particular move is valid by taking enforcing the constraints of the game. 
  //  Does not allow pieces to be placed outside the bounds of the board, in the corners or in 
  //  the opponent's starting or ending zones. Does not allow pieces to placed such that 3 pieces
  //  are connected. Returns true if the move is valid and vice versa.
  /** 
   *  @param m the Move that is being checked
   *  @param color the player who is trying to make said move 
   *  @return true or false depending on the move's validity
   */
  private boolean isValidMove(Move m, char color) {
    if (m.x1 < 0 || m.y1 < 0 || m.x1 >= Board.DIM || m.y1 >= Board.DIM) {
      //Move is out of bounds
      return false;
    }

    if (board.getContents(m.x1, m.y1) != null) {
      //Move is already occupied
      return false;
    }

    if ((m.x1 == 0 && m.y1 == 0) || (m.x1 == Board.DIM - 1 && m.y1 == 0) || (m.x1 == 0 && m.y1 == Board.DIM - 1) || (m.x1 == Board.DIM - 1 && m.y1 == Board.DIM - 1)) {
      //Move is in a corner
      return false;
    }

    if (((color == 'B') && (m.x1 == 0 || m.x1 == Board.DIM - 1)) || ((color == 'W') && (m.y1 == 0 || m.y1 == Board.DIM - 1))) {
      //Move is in opponent's goal
      return false;
    }

    makeMove(m, color);
    Piece[] neighbors = (board.getContents(m.x1, m.y1)).neighbors();
    int adjacent = 0;
    for (int i = 0; i < 8; i++) {
      if (neighbors[i] != null && neighbors[i].color() == color) {
        adjacent++;
        if (adjacent >= 2) {
          unMove(m, color);
          //Move connects 2 others together
          return false;
        }
        Piece[] nextNeighbors = neighbors[i].neighbors();
        int ignore = 1;
        for (int j = 0; j < 8; j++) {
          if (nextNeighbors[j] != null && nextNeighbors[j].color() == color) {
            if (ignore == 1) {
              ignore--;
              continue;
            }
            unMove(m, color);
            //Move extends 2 connected
            return false;
          }
        }
      }
    }
    unMove(m, color);
    //Move doesn't break any rules
    return true;
  }

  //  Loops through the board with the conditions of Move Legality and
  //  stores all the moves that are valid for a particular color in a DList.
  /** 
   *  @param color
   *  @return a DList containing all the possible valid moves for a certain player
   */
  private DList listMoves(char color) {
    DList moves = new DList();
    for (int i = 0; i < Board.DIM; i++) {
      for (int j = 0; j < Board.DIM; j++) {
        if (pieces > 0) {
          Move m = new Move(i, j);
          if (isValidMove(m, color)) {
            moves.insertBack(m);
          }
        } else {
          if (board.getContents(i, j) != null && board.getContents(i, j).color() == color) {
            for (int u = 0; u < Board.DIM; u++) {
              for (int v = 0; v < Board.DIM; v++) {
                Move m = new Move(u, v, i, j);
                if (isValidMove(m, color)) {
                  moves.insertBack(m);
                }
              }
            }
          }
        }
      }
    }
    return moves;
  }

  //  Performs an evaluation algorithm on a board and returns a rating for a player.
  //  Used in the game tree search to identify the best possible move.
  /** 
   *  @return an int representing the probability of of a player winning for the board's current state
   */
  private int boardEval() {
  	return ((board.connects(new DList(), myColor) * board.connects(new DList(), myColor) * board.connects(new DList(), myColor))) - ((board.connects(new DList(), oppColor) * board.connects(new DList(), oppColor) * board.connects(new DList(), oppColor)));
  }

  //  Calculates the move that should be performed by the MachinePlayer with a given board. Uses
  //  game trees with alpha beta pruning to find the best possible move. 
  /**  
   *  @param color the color of the player
   *  @param opp the color of the opposing player
   *  @param alpha a score that the computer knows with certainty it can achieve
   *  @param beta a scpre that the opponent can achieve
   *  @param depth the level that the tree is at
   *  @param previous the last move that was made at that step in the tree
   *  @return a Best object containing the optimal move
   */
  public Best calcMove(char color, char opp, int alpha, int beta, int depth, Move previous) {
    Best myBest = new Best();
    Best reply;

    boolean win = board.hasNetwork(color);
    boolean lose = board.hasNetwork(opp);
    if (win) {
    	return new Best(null, Integer.MAX_VALUE - depth);
    }
    if (lose) {
    	if (depth % 2 == 1) {
    		return new Best(previous, Integer.MAX_VALUE - depth);
    	}
      return new Best(null, Integer.MIN_VALUE);
    }
    if (pieces > 0) {
      if (depth == searchDepth) {
        return new Best(null, boardEval());
      }
    } else {
      if (depth == searchDepth - 1) {
        return new Best(null, boardEval());
      }
    }
    if (color == myColor) {
      myBest.score = Integer.MIN_VALUE;
    } else {
      myBest.score = Integer.MAX_VALUE;
    }
    DList moves = listMoves(color);
    try {
		  myBest.m = (Move)moves.front().item();
	  } catch (InvalidNodeException e1) {
		  e1.printStackTrace();
	  }
    DListNode curr = (DListNode)moves.front();
    while (true) {
      try {
        makeMove((Move)curr.item(), color);
        reply = calcMove(opp, color, alpha, beta, depth + 1, (Move)curr.item());
        unMove((Move)curr.item(), color);
        if (color == myColor && reply.score > myBest.score) {
          myBest.m = (Move)curr.item();
          myBest.score = reply.score;
          alpha = reply.score;
        } else if (color == oppColor && reply.score < myBest.score) {
          myBest.m = (Move)curr.item();
          myBest.score = reply.score;
          beta = reply.score;
        }
        if (alpha >= beta) {
          return myBest;
        }
        curr = (DListNode)curr.next();
      } catch (InvalidNodeException e) {    	
        return myBest;
      }
    }
  }

  //  Modifies the game board according to the Move that is passed in for a certain color.
  /** 
   *  @param m
   *  @param color
   */
  public void makeMove(Move m, char color) {
    if (m.moveKind == Move.STEP) {
      board.removePiece(m.x2, m.y2);
    }
    board.addPiece(m.x1, m.y1, color);
  }

  //  Reverts the game board to its previous state after the board has been modified.
  /** 
   *  @param m
   *  @param color
   */
  public void unMove(Move m, char color) {
      board.removePiece(m.x1, m.y1);
      if (m.moveKind == Move.STEP) {
        board.addPiece(m.x2, m.y2, color); 
      }
    }


  //  Returns a new move by "this" player.  Internally records the move (updates
  //  the internal game board) as a move by "this" player.
  /** (non-Javadoc)
   *  @see player.Player#chooseMove()
   *  @return the Move that is to be made
   */
  public Move chooseMove() {
      Move m = calcMove(myColor, oppColor, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, null).m;
      makeMove(m, myColor);
      pieces--;
      return m;
  }

  //  If the Move m is legal, records the move as a move by the opponent
  //  (updates the internal game board) and returns true.  If the move is
  //  illegal, returns false without modifying the internal state of "this"
  //  player.  This method allows your opponents to inform you of their moves.
  //  (non-Javadoc)
  /** @see player.Player#opponentMove(player.Move)
   *  @return true or false depending on the validity of the opponent's move
   */
  public boolean opponentMove(Move m) {
      if (!isValidMove(m, oppColor)) {
        return false;
      }
      makeMove(m, oppColor);
      return true;
  }

  //  If the Move m is legal, records the move as a move by "this" player
  //  (updates the internal game board) and returns true.  If the move is
  //  illegal, returns false without modifying the internal state of "this"
  //  player.  This method is used to help set up "Network problems" for your
  //  player to solve.
  /** (non-Javadoc)
   *  @see player.Player#forceMove(player.Move)
   *  @return true or false depending on the validity of the move
   */
  public boolean forceMove(Move m) {
      if (!isValidMove(m, myColor)) {
        return false;
      }
      makeMove(m, myColor);
      pieces--;
      return true;
  }

}

/**
 *  Contains the current Best move and its corresponding score.
 *
 */
class Best {

  public Move m;
  public int score;

/**
 *  Creates a Best with a default move of QUIT and a score of zero.
 */
  public Best() {
    m = new Move();
    score = 0;
  }

/**
 *  Creates a Best with the move passed in and its corresponding score.
 * 
 *  @param m the Move being assigned
 *  @param score the score of the board when this Move is made
 */
  public Best(Move m, int score) {
    this.m = m;
    this.score = score;
  }

}