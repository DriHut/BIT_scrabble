package fr.Adrien1106.BIT_scrabble.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.ModifierBoard;
import fr.Adrien1106.BIT_scrabble.util.IO.Printer;
import fr.Adrien1106.BIT_scrabble.util.IO.SystemPrinter;
import fr.Adrien1106.util.interfaces.IPlayer;
import fr.Adrien1106.util.protocol.ProtocolMessages;

public class ClientGame implements Runnable {
	
	public static final ClientGame INSTANCE = new ClientGame();
	public static Printer out;
	
	public static boolean HAS_GUI = false;
	public static boolean IS_RUNNING = true;

	private Player player;
	private Player current_player;
	private ServerHandler server_handler;
	
	private Board board;
	private List<IPlayer> players;
	
	public ClientGame() {
		players = new CopyOnWriteArrayList<>();
		player = new Player("dummy#0");
		board = new Board(ModifierBoard.convertFromString(ModifierBoard.spreadBoard(Board.SIZE, ModifierBoard.BOARD_I)));
	}

	@Override
	public void run() {
		server_handler = new ServerHandler();
		new Thread(server_handler).start();
		if (out instanceof SystemPrinter)
			while (ClientGame.IS_RUNNING)
				ListenToConsole();
	}
	
	/**
	 * Add a new player from identifier
	 * @param identifier
	 */
	public synchronized void addPlayer(String identifier) {
		if (getPlayer(identifier) == null) players.add(new Player(identifier));
	}
	
	/**
	 * remove a player from the players list
	 * @param player - player to be removed
	 */
	public synchronized void remove(IPlayer player) {
		if (players.contains(player)) players.remove(player);
	}
	
	/**
	 * read text from the console (not used when using gui)
	 */
	public void ListenToConsole() {
		String line = null;
		while (line == null && ClientGame.IS_RUNNING) {
			BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
			 
	        try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		handleInput(line);
	}
	
	/**
	 * Handles a text input to be sent as a command
	 * @param line - the input
	 */
	public void handleInput(String line) {
		String[] cmd = line.split(";");
		if (cmd.length == 0) return;
		switch (cmd[0]) {
		case ProtocolMessages.SKIP_TURN:
		case ProtocolMessages.CUSTOM_COMMAND + "fs":
		case ProtocolMessages.CUSTOM_COMMAND + "fx":
		case ProtocolMessages.CUSTOM_COMMAND + "rr":
			if (cmd.length != 1) return;
			server_handler.sendCommand(cmd[0], Arrays.asList(""));
		case ProtocolMessages.CONNECT:
		case ProtocolMessages.CREATE_ROOM:
		case ProtocolMessages.JOIN_ROOM:
			if (cmd.length != 2) return;
			server_handler.sendCommand(cmd[0], Arrays.asList(cmd[1]));
			return;
		case ProtocolMessages.REPLACE_TILES:
			if (cmd.length != 2) return;
			server_handler.sendCommand(cmd[0], Arrays.asList(cmd[1]));
		case ProtocolMessages.MAKE_MOVE:
			if (cmd.length != 4) return;
			server_handler.sendCommand(cmd[0], Arrays.asList(cmd[1], cmd[2], cmd[3]));
			return;
		default:
			if (ServerHandler.name.equals("")) {
				ServerHandler.name = line;
				return;
			}
			if (ServerHandler.ip_address.equals("") && line.split("\\.").length == 4) {
				ServerHandler.ip_address = line;
				return;
			}
			server_handler.sendCommand(cmd[0], Arrays.asList(line.replace(line.split(";")[0] + ";", "")));
		}
	}

	/**
	 * @return the client copy of the board
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Get a player from an identifier
	 * @param identifier - identifier of the wanter player
	 * @return the player or null if no such players
	 */
	public Player getPlayer(String identifier) {
		for (IPlayer player: players)
			if (((Player) player).getIdentifier().equals(identifier)) return (Player) player;
		return null;
	}
	
	/**
	 * @return the client player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return the currently playing player
	 */
	public Player getCurrentPlayer() {
		return current_player;
	}
	
	/**
	 * set the current playing player to the given player
	 * @param player - the currently playing player
	 */
	public void setCurrentPlayer(Player player) {
		current_player = player;
	}

	/**
	 * Print using a log information
	 * @param message - to be logged
	 */
	public void log(String message) {
		print("> \u001b[34m[LOG]\u001b[0m " + message);
	}
	
	/**
	 * Print to the given message to the set output
	 * @param message - to be printed
	 */
	public void print(String message) {
		out.println(message);
	}

	/**
	 * Set the client player
	 * @param player - the player to be set as the client player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * @return list of all players in the room
	 */
	public List<IPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Create a console version of the client
	 * @param args
	 */
	public static void main(String[] args) {
		ClientGame.out = new SystemPrinter();
		new Thread(ClientGame.INSTANCE).start();
	}

}
