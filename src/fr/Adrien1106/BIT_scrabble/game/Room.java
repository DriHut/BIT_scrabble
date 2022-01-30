package fr.Adrien1106.BIT_scrabble.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.server.ServerGame;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.ModifierBoard;
import fr.Adrien1106.BIT_scrabble.util.TileBag;
import fr.Adrien1106.BIT_scrabble.util.Tiles;
import fr.Adrien1106.util.exceptions.TooFewPlayersException;
import fr.Adrien1106.util.exceptions.TooManyPlayersException;
import fr.Adrien1106.util.interfaces.IBoard;
import fr.Adrien1106.util.interfaces.IPlayer;
import fr.Adrien1106.util.interfaces.IRoom;

public class Room implements IRoom {

	private int id;
	private List<IPlayer> players;
	private List<Tiles> bag;
	private Board board;
	
	private Player current_player;
	private Player finishing_player;
	private int skip_streak = 0;
	
	private int max_players;
	private int min_players;
	
	public Room(int id) {
		this(id, References.MAX_PLAYERS, References.MIN_PLAYERS);
	}
	
	public Room(int id, int max_players, int min_players) {
		players = new ArrayList<>();
		this.max_players = max_players;
		this.min_players = min_players;
		this.id = id;
		this.board = new Board(ModifierBoard.convertFromString(ModifierBoard.spreadBoard(Board.SIZE, ModifierBoard.BOARD_I)));
		this.bag = TileBag.BAG_I.getBag();
	}
	
	/**
	 * Set a board on the server
	 * @param board
	 */
	public void setBoard(Board board) {
		this.board = board;
	}
	
	/**
	 * @return room board
	 */
	@Override
	public IBoard getBoard() {
		return board;
	}
	
	/**
	 * @return room id
	 */
	@Override
	public int getId() {
		return id;
	}
	
	/**
	 * Add a player to the room
	 * @param player
	 * @throws TooManyPlayersException 
	 */
	public void addPlayer(Player player) throws TooManyPlayersException  {
		if (isFull()) throw new TooManyPlayersException(players.size()+1, max_players);
		players.add(player);
	}
	
	/**
	 * @return if the room is full
	 */
	public boolean isFull() {
		return players.size() == max_players;
	}

	/**
	 * try to start the room
	 */
	public void tryStart() {
		if (isFull())
			try {
				start();
			} catch (TooFewPlayersException e) {}
	}
	
	/**
	 * get the player list
	 * @return list of players
	 */
	@Override
	public List<IPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Gives the minimum of players needed for this room to start
	 * @return minimum
	 */
	public int getMinPlayers() {
		return min_players;
	}
	
	/**
	 * Retrieve a tile from the bag. Be aware the tiles gets removed from the bag
	 * @return a tile
	 */
	public Tiles getTile() {
		if (bag.isEmpty()) return null;
		Random rand = new Random();
		
		Tiles tile = bag.get(rand.nextInt(bag.size()));
		bag.remove(tile);
		
		return tile;
	}
	
	/**
	 * Add a tile to the bag
	 * @param tile - tile to be added
	 */
	public void addTile(Tiles tile) {
		bag.add(tile);
	}
	
	/**
	 * Add multiple tiles to the bag
	 * @param letters - string of the letters to be added
	 */
	public void addTiles(String letters) {
		for (String letter: letters.split("")) {
			addTile((Tiles) Tiles.fromLetter(letter));
		}
	}

	/**
	 * Checks if the player is allowed to play
	 * @param player
	 * @return if it the player turn
	 */
	public boolean isTurn(Player player) {
		return current_player.equals(player);
	}
	
	/**
	 * breaks skip streak
	 */
	public void breakSkipStreak() {
		skip_streak = 0;
		for (IPlayer p: players)
			if (((Player) p).getTiles().length() == 0) return;
		finishing_player = null;
	}
	
	/**
	 * used to keep track of the skip streak
	 */
	public void skip() {
		if (finishing_player == null) finishing_player = current_player;
		if (current_player.equals(finishing_player) && finishing_player.getTiles().length() != 0) skip_streak++;
		next();
	}

	/**
	 * Switch the current player to the next one
	 */
	public void next() {
		Player last_player = current_player;
		for (int i = 0; i < players.size(); i++)
			if (current_player.equals(players.get(i))) {
				current_player = (Player) players.get(i+1 == players.size()? 0: i+1);
				break;
			}
		
		boolean stopping = shouldStop();
		ServerGame.INSTANCE.doUpdateTable(this, board.toString());
		if (!stopping) ServerGame.INSTANCE.doUpdateScore(this, last_player.getIdentifier() + ";" + last_player.getScore());
		
		if (!stopping) {
			ServerGame.INSTANCE.doUpdateCurrentPlayer(this, current_player);
			if ((current_player.getTiles().length() == 0 || bag.size() == 0) && finishing_player != null) finishing_player = current_player;
		} else finish();
	}

	private boolean shouldStop() {
		return current_player.equals(finishing_player) && (skip_streak == 0 || skip_streak == 2);
	}

	/**
	 * Starts the game and do the initialisation
	 * @throws TooFewPlayersException - when the room is not full enough
	 */
	public void start() throws TooFewPlayersException {
		if (current_player != null) return;
		if (players.size() < min_players) throw new TooFewPlayersException(players.size(), min_players);
		current_player = (Player) players.get(0);
		ServerGame.INSTANCE.doStart(this);
	}

	/**
	 * Removes a player from the room
	 * @param player
	 */
	public void removePlayer(Player player) {
		players.remove(player);
		if (players.size() < min_players) finish();
	}

	/**
	 * Ends the game
	 */
	public void finish() {
		if (current_player == null) return;
		calculateFinalScores(false);
		List<IPlayer> best_players = getBestPlayers(players);
		
		if (best_players.size() > 1) {
			calculateFinalScores(true);
			best_players = getBestPlayers(best_players);
		}
		
		String best_player = ((Player) best_players.get(0)).getName();
		for (int i = 1; i < best_players.size(); i++)
			best_player += "," + ((Player) best_players.get(i)).getName();
		for (IPlayer p: players)
			ServerGame.INSTANCE.doUpdateScore(this, ((Player) p).getIdentifier() + ";" + ((Player) p).getScore());
		ServerGame.INSTANCE.doFinish(this, best_player, ((Player) best_players.get(0)).getScore());
	}
	
	/**
	 * Gets a list off the best player in the given list
	 * @param players - players list in which we want a winner
	 * @return the best players
	 */
	private List<IPlayer> getBestPlayers(List<IPlayer> players) {
		List<IPlayer> best_players = new ArrayList<>();

		int best_score = -1000; // to handle force stopping
		for (IPlayer p: players) {
			Player player = (Player) p;
			if (player.getScore() == best_score) best_players.add(player);
			if (player.getScore() > best_score) {
				best_score = player.getScore();
				best_players.add(player);
			}
		}
		
		return best_players;
	}

	/**
	 * Calculate the final score by retrieving all the point from the owned tiles and adding the point to the players that have used all the tiles
	 * @param reverse - boolean condition for reversing the final scoring action
	 */
	private void calculateFinalScores(boolean reverse) {
		// remove all needed points
		int extra_point = 0;
		for (IPlayer p: players) {
			Player player = (Player) p;
			if (player.getTiles().length() != 0)
				for(Tiles tile: ((Player) player).getTilesList()) {
					extra_point += tile.getValue();
					player.addScore(tile.getValue() * (reverse? 1: -1));
				}
		}
		
		for (IPlayer p: players) {
			Player player = (Player) p;
			if (player.getTiles().length() == 0)
				player.addScore(extra_point * (reverse? -1: 1));
		}
	}
}
