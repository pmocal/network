/* Board.Java */

package player;

import list.*;

/**
 * Represents the Board that is used for the game Network as an array of Piece objects. 
 * 
 *  @author victors
 *  @author parthivm
 */

public class Board {

	public final static int DIM = 8;

	protected Piece[][] board = new Piece[DIM][DIM];

	/**
	 * Adds a Piece object to a particular coordinate (x, y) on the board.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param color the color of the piece
	 */
	public void addPiece(int x, int y, char color) {
		board[x][y] = new Piece(color, this, x, y);
	}

	/**
	 * Removes the Piece object inhabiting a particular coordinate (x,y) on the board.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void removePiece(int x, int y) {
		board[x][y] = null;
	}

	/**
	 * Returns the contents of a particular coordinate (x,y) on the board.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the piece at (x,y)
	 */
	public Piece getContents(int x, int y) {
		return board[x][y];
	}

	/**
	 * Checks whether a particular Piece object is inside a particular DList 
	 * by cycling through each node.
	 * 
	 * @param p the Piece
	 * @param d the DList being checked
	 * @return true or false depending on whether it is in the DList
	 */
	public boolean inList(Piece p, DList d) {
		DListNode curr = (DListNode)d.front();
		while (true) {
			try {
				if ((Piece)curr.item() == p) {
					return true;
				}
				curr = (DListNode)curr.next();
			} catch (InvalidNodeException e) {
				return false;
			}
		}
	}

	/**
	 * Checks whether the current board has a network for a particular color. 
	 * Returns true or false accordingly.
	 * 
	 * @param color int representing color of the player
	 * @return true or false depending on if there is a network or not
	 */
	public boolean hasNetwork(char color) {
		DList chain = new DList();
		if (color == 'B') {
			for (int i = 0; i < DIM - 1; i++) {
				for (int j = 0; j < DIM - 1; j++) {
					chain = new DList();
					if (getContents(j, i) != null && getContents(j, i).color() == 'B') {
						chain.insertBack(getContents(j, i));
						if (checkNetwork(chain, color, -1)) {
							return true;
						}
						try {
							chain.front().remove();
						} catch (InvalidNodeException e) {
							break;
						}
					}
				}
			}
			return false;
		} else {
			for (int i = 0; i < DIM - 1; i++) {
				for (int j = 0; j < DIM - 1; j++) {
					chain = new DList();
					if (getContents(i, j) != null && getContents(i, j).color() == 'W') {
						chain.insertBack(getContents(i, j));
						if (checkNetwork(chain, color, -1)) {
							return true;
						}
						try {
							chain.front().remove();
						} catch (InvalidNodeException e) {
							break;
						}
					}
				}
			}
			return false;
		}
	}

	/**
	 * Operates recursively to check whether a player has a network, 6 connections from the start
	 * zone to the end zone with directional changes after every segment. Returns true or false
	 * accordingly.
	 * 
	 * @param chain a DList which is added to every time a new connection is found for the potential network
	 * @param color the color of the player
	 * @param dir the direction that the last connection came in
	 * @return true or false depending on whether a network was found 
	 */
	public boolean checkNetwork(DList chain, char color, int dir) {
		try {
			//System.out.println("Checking for network starting from " + ((Piece)chain.back().item()).x() + "," + ((Piece)chain.back().item()).y());
			if (color == 'B' && ((Piece)chain.back().item()).y() == DIM - 1 && ((Piece)chain.front().item()).y() == 0) {
				if (chain.length() >= 6) {
					return true;
				} else {
					return false;
				}
			} else if (color == 'W' && ((Piece)chain.back().item()).x() == DIM - 1 && ((Piece)chain.front().item()).x() == 0) {
				if (chain.length() >= 6) {
					return true;
				} else {
					return false;
				}
			}

			for (int i = 0; i < 8; i++) {
				int x = ((Piece)chain.back().item()).x();
				int y = ((Piece)chain.back().item()).y();
				switch (i) {
					//NW
					case 0: //System.out.println("Checking NW of " + x + "," + y);
							if (dir == 0 || dir == 7) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit NW");
								x--;
								y--;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								//System.out.println("Encountered piece of the same color");
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 0)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//W
					case 1: //System.out.println("Checking W of " + x + "," + y);
							if (dir == 1 || dir == 6) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit W");
								x--;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								//System.out.println("Encountered piece of the same color");
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 1)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//SW
					case 2: //System.out.println("Checking SW of " + x + "," + y);
							if (dir == 2 || dir == 5) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit SW");
								x--;
								y++;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 2)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//N
					case 3: //System.out.println("Checking N of " + x + "," + y);
							if (dir == 3 || dir == 4) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit N");
								y--;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 3)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//S
					case 4: //System.out.println("Checking S of " + x + "," + y);
							if (dir == 4 || dir == 3) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit S");
								y++;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								//System.out.println("Encountered piece of the same color");
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 4)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//NE
					case 5: //System.out.println("Checking NE of " + x + "," + y);
							if (dir == 5 || dir == 2) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit NE");
								x++;
								y--;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								//System.out.println("Encountered piece of the same color");
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 5)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//E
					case 6: //System.out.println("Checking E of " + x + "," + y);
							if (dir == 6 || dir == 1) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit E");
								x++;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								//System.out.println("Encountered piece of the same color");
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 6)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
					//SE
					case 7: //System.out.println("Checking SE of " + x + "," + y);
							if (dir == 7 || dir == 0) {
								//System.out.println("Direction restricted");
								continue;
							}
							do {
								//System.out.print("Moving one unit SE");
								x++;
								y++;
								//System.out.println(" to " + x + "," + y);
							} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

							if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
								//System.out.println("Went out of bounds");
								continue;
							}

							if (getContents(x, y).color() == color) {
								//System.out.println("Encountered piece of the same color");
								if (inList(getContents(x, y), chain)) {
									continue;
								}
								chain.insertBack(getContents(x, y));
								if (checkNetwork(chain, color, 7)) {
									return true;
								} else {
									chain.back().remove();
									continue;
								}
							} else {
								continue;
							}
				}
				return false;
			}
		} catch (InvalidNodeException e) {
			return false;
		}
		return false;
	}

	/**
	 * Counts the number of connections between pieces on the board for a certain color.
	 * Does so by checking for any pieces in all directions excluding the direction that
	 * the previous piece came from. Does not repeat paths and takes into account pieces
	 * of the opposite color which blocking paths.
	 * 
	 * 
	 * @param visited a DList of pieces that have already been started from 
	 * @param color the color of the piece whose connections are being counted
	 * @return an int representing the total number of connections
	 */
	public int connects(DList visited, char color) {
		int connects = 0;
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				if (getContents(i, j) != null && getContents(i, j).color() == color) {
					visited.insertBack(getContents(i, j));
					for (int k = 0; k < 8; k++) {
						int x = i;
						int y = j;
						switch (k) {
							case 0: 
									do {
										//System.out.print("Moving one unit NW");
										x--;
										y--;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 1: 
									do {
										//System.out.print("Moving one unit NW");
										x--;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 2: 
									do {
										//System.out.print("Moving one unit NW");
										x--;
										y++;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 3: 
									do {
										//System.out.print("Moving one unit NW");
										y--;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 4: 
									do {
										//System.out.print("Moving one unit NW");
										y++;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 5: 
									do {
										//System.out.print("Moving one unit NW");
										x++;
										y--;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 6: 
									do {
										//System.out.print("Moving one unit NW");
										x++;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
							case 7: 
									do {
										//System.out.print("Moving one unit NW");
										x++;
										y++;
										//System.out.println(" to " + x + "," + y);
									} while (x > 0 && y > 0 && x < Board.DIM && y < Board.DIM && getContents(x, y) == null);

									if (x <= 0 || y <= 0 || x >= Board.DIM || y >= Board.DIM) {
										//System.out.println("Went out of bounds");
										continue;
									}

									if (getContents(x, y).color() == color) {
										if (!inList(getContents(x, y), visited)) {
											connects++;
										}
									}
									break;
						}
					}
				}
			}
		}
		return connects;
	}

	//For debugging
	/**
	 * @param d
	 */
	public void printDList(DList d) {
		DListNode curr = (DListNode)d.front();
		while (true) {
			try {
				System.out.print(((Piece)curr.item()).x() + "," + ((Piece)curr.item()).y() + " ");
				curr = (DListNode)curr.next();
			} catch (InvalidNodeException e) {
				break;
			}
		}
		System.out.println();
	}

	//For debugging
	/**
	 * 
	 */
	public void printBoard() {
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				if (board[j][i] == null) {
					System.out.print("  ");
				} else {
					System.out.print(board[j][i].color() + " ");
				}
			}
			System.out.println();
		}
	}

}

/**
 * A Piece object represents the pieces used in the game board of Network.
 *
 */
class Piece {

	private char color;
	private Board board;
	private int x;
	private int y;

	/**
	 * Constructs a Piece object with the desired parameters.
	 * 
	 * @param color a char representing the color of the piece
	 * @param board the board to which the piece belongs
	 * @param x the piece's x coordinate
	 * @param y the piece's y coordinate
	 */
	public Piece(char color, Board board, int x, int y) {
		this.color = color;
		this.board = board;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the color of a Piece.
	 * 
	 * @return a char representing the color of a piece
	 */
	public char color() {
		return color;
	}

	/**
	 * Returns the x coordinate of a Piece.
	 * 
	 * @return an int representing the x coordinate of a piece
	 */
	public int x() {
		return x;
	}

	/**
	 * Returns the y coordinate of a Piece.
	 * 
	 * @return an int representing the y coordinate of a piece
	 */
	public int y() {
		return y;
	}

	/**
	 * Returns an array containing any Piece objects in the adjacent spots of a particular
	 * Piece. Is used to ensure that the rule banning 3 pieces of the same color from being
	 * adjacent is not violated.
	 * 
	 * @return an array of adjacent Piece objects
	 */
	public Piece[] neighbors() {
		Piece[] neighbors = new Piece[8];
		int index = -1;
		for (int i = x - 1; i < x + 2; i++) {
			for (int j = y - 1; j < y + 2; j++) {
				if (i == x && j == y) {
					continue;
				}
				index++;
				if (i < 0 || j < 0 || i >= Board.DIM || j >= Board.DIM) {
					continue;
				}
				neighbors[index] = board.getContents(i, j);
			}
		}
		return neighbors;
	}
}