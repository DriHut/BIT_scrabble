package fr.Adrien1106.BIT_scrabble.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private ServerHandler server_handler;
	
	private Board board;
	private List<IPlayer> players;
	
	public ClientGame() {
		players = new ArrayList<>();
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
	
	public synchronized void addPlayer(String identifier) {
		if (getPlayer(identifier) == null) players.add(new Player(identifier));
	}
	
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
	
	public void handleInput(String line) {
		if (ServerHandler.name.equals("")) {
			ServerHandler.name = line;
			return;
		}
		String[] cmd = line.split(";");
		if (cmd.length == 0) return;
		switch (cmd[0]) {
		case ProtocolMessages.SKIP_TURN:
		case ProtocolMessages.CUSTOM_COMMAND + "fs":
		case ProtocolMessages.CUSTOM_COMMAND + "fx":
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
			server_handler.sendCommand(cmd[0], Arrays.asList(line.replace(line.split(";")[0] + ";", "")));
		}
	}

	public Board getBoard() {
		return board;
	}
	
	public Player getPlayer(String identifier) {
		for (IPlayer player: players)
			if (((Player) player).getIdentifier().equals(identifier)) return (Player) player;
		return null;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void log(String message) {
		print("> \u001b[34m[LOG]\u001b[0m " + message);
	}
	
	public void print(String message) {
		out.println(message);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public List<IPlayer> getPlayers() {
		return players;
	}
	
	public static void main(String[] args) {
		ClientGame.out = new SystemPrinter();
		new Thread(ClientGame.INSTANCE).start();
	}

}
