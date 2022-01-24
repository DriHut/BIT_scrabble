package fr.Adrien1106.BIT_scrabble.game;

import java.util.List;
import java.util.Random;

import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.server.ServerGame;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.ModifierBoard;
import fr.Adrien1106.BIT_scrabble.util.Tile;
import fr.Adrien1106.util.exceptions.TooFewPlayersException;
import fr.Adrien1106.util.exceptions.TooManyPlayersException;
import fr.Adrien1106.util.interfaces.IBoard;
import fr.Adrien1106.util.interfaces.IPlayer;
import fr.Adrien1106.util.interfaces.IRoom;

public class Room implements IRoom {

	private int id;
	private List<IPlayer> players;
	private List<Tile> bag;
	private Board board;
	
	private Player current_player;
	
	private int max_players;
	private int min_players;
	
	public Room(int id) {
		this(id, References.MAX_PLAYERS, References.MIN_PLAYERS);
	}
	
	public Room(int id, int max_players, int min_players) {
		this.id = id;
		this.board = new Board(ModifierBoard.convertFromString(ModifierBoard.spreadBoard(Board.SIZE, ModifierBoard.BOARD_I)));
		this.bag = null;
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
		if (players.size() == max_players) throw new TooManyPlayersException(players.size()+1, max_players);
		players.add(player);
		if (players.size() == max_players)
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
	public Tile getTile() {
		if (bag.isEmpty()) return null;
		Random rand = new Random();
		
		Tile tile = bag.get(rand.nextInt(bag.size()));
		bag.remove(tile);
		
		return tile;
	}
	
	/**
	 * Add a tile to the bag
	 * @param tile - tile to be added
	 */
	public void addTile(Tile tile) {
		bag.add(tile);
	}
	
	public void addTiles(String letters) {
		for (String letter: letters.split("")) {
			addTile(Tile.fromLetter(letter));
		}
	}

	public boolean isTurn(Player player) {
		return current_player.equals(player);
	}

	public void next() {
		ServerGame.INSTANCE.doUpdateScore(this, "" + current_player.getScore());
		for (int i = 0; i < players.size(); i++)
			if (current_player.equals(players.get(i)))
				current_player = (Player) players.get(i+1 == players.size()? 0: i+1);
	}

	public void start() throws TooFewPlayersException {
		if (players.size() < min_players) throw new TooFewPlayersException(players.size(), min_players);
		ServerGame.INSTANCE.doStart(this);
	}
}