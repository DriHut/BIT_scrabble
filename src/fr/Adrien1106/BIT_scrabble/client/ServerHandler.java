package fr.Adrien1106.BIT_scrabble.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.client.GUI.BoardPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.RackPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.ScorePane;
import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.server.ServerGame;
import fr.Adrien1106.BIT_scrabble.util.render.AnsiColor;
import fr.Adrien1106.util.interfaces.IPlayer;
import fr.Adrien1106.util.interfaces.IServerHandler;
import fr.Adrien1106.util.protocol.ProtocolMessages;

public class ServerHandler implements Runnable, IServerHandler {

	private Socket socket;
	private BufferedReader input_reader;
	private BufferedWriter output_reader;

	public static String ip_address = "";
	public static String name = "";
	private String identifier = "";
	
	@Override
	public void run() {
		try {
			ClientGame.INSTANCE.print("> Enter your name");
			while(name.equals("")) Thread.sleep(100);
			ClientGame.INSTANCE.print("> Enter ip address to connect to");
			while(ip_address.equals("")) Thread.sleep(100);
			setup();
			sendCommand(ProtocolMessages.CONNECT, Arrays.asList(name));
			String msg = input_reader.readLine();
			while (msg != null && ClientGame.IS_RUNNING) {
				ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m Incoming: " + msg);
				handleCommand(msg);
				msg = input_reader.readLine();
			}
		} catch (IOException | InterruptedException e) {
			ClientGame.INSTANCE.log("A server IO error occurred: " + e.getMessage());
		}
	}

	/**
	 * Sets up the connection with the server
	 */
	private void setup() {
		while (socket == null) {
			try {
				InetAddress addr = InetAddress.getByName(ip_address);
				ClientGame.INSTANCE.log("Attempting to connect to " + addr + ":"  + ServerGame.PORT + "...");
				socket = new Socket(addr, ServerGame.PORT);
				input_reader = new BufferedReader(new InputStreamReader( socket.getInputStream()));
				output_reader = new BufferedWriter(new OutputStreamWriter( socket.getOutputStream()));
				ClientGame.INSTANCE.log("Connection restablished");
			} catch (IOException e) {
				ClientGame.INSTANCE.log("could not create a socket on " + ip_address + ":" + ServerGame.PORT);
			}
		}
	}
	
	/**
	 * Sends a command to the server
	 * @param command - the command
	 * @param args - the list of all the argument to be sent
	 */
	public void sendCommand(String command, List<String> args) {
		if (output_reader == null) return;
		String msg = command;
		for (String arg: args)
			msg += ProtocolMessages.DELIMITER + arg;
		try {
			output_reader.write(msg);
			output_reader.newLine();
			output_reader.flush();
		} catch (IOException e) {
			ClientGame.INSTANCE.log(e.getMessage());
		}
	}
	
	/**
	 * Called when a message is received from the server
	 * @param msg - the message received
	 */
	private void handleCommand(String msg) throws ArrayIndexOutOfBoundsException {
		String[] cmd = msg.split(ProtocolMessages.DELIMITER);
		switch (cmd[0]) {
		case ProtocolMessages.INITIATE_GAME:
			if (cmd.length != 3) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m initiate game");
			handleInitiategame(cmd[1], cmd[2]);
			return;
		case ProtocolMessages.UPDATE_TABLE:
			if (cmd.length != 2) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m update table");
			handleUpdateTable(cmd[1]);
			return;
		case ProtocolMessages.UPDATE_SCORE:
			if (cmd.length != 3) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m update score");
			handleUpdateScore(cmd[1],cmd[2]);
			return;
		case ProtocolMessages.ADD_OR_REMOVE_PLAYER:
			if (cmd.length != 2) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m player join");
			handlePlayerJoin(cmd[1]);
			return;
		case ProtocolMessages.FEEDBACK:
			if (cmd.length != 2) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m feedback");
			handleFeedback(cmd[1]);
			return;
		case ProtocolMessages.GIVE_TILE:
			if (cmd.length != 2) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m receive tiles");
			handleGiveTile(cmd[1]);
			return;
		case ProtocolMessages.FINISH_GAME:
			if (cmd.length != 3) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m end game");
			handleFinishGame(cmd[1],cmd[2]);
			return;
		case ProtocolMessages.CUSTOM_COMMAND + "lr":
			if (cmd.length != 2) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m listing rooms");
			handleListRoom(cmd[1]);
			return;
		case ProtocolMessages.CUSTOM_COMMAND + "cp":
			if (cmd.length != 2) return;
			ClientGame.INSTANCE.print("> \u001b[32m[server]\u001b[0m current player");
			handleCurrentPlayer(cmd[1]);
			return;
		default:
		}
	}

	@Override
	public void handleGiveTile(String letters) {
		if (ClientGame.INSTANCE.getPlayer() == null) return;
		ClientGame.INSTANCE.getPlayer().removeTiles(ClientGame.INSTANCE.getPlayer().getTiles());
		ClientGame.INSTANCE.getPlayer().addTiles(letters);
		if (ClientGame.HAS_GUI) RackPane.INSTANCE.updateTiles();
	}

	@Override
	public void handleUpdateScore(String identifier, String score) {
		Player player = ClientGame.INSTANCE.getPlayer(identifier);
		if (player == null) return;
		player.setScore(Integer.valueOf(score));
		if (ClientGame.HAS_GUI) ScorePane.INSTANCE.updateScores();
	}

	@Override
	public void handleFeedback(String feedback) {
		if (feedback.contains("#")) identifier = feedback;
		ClientGame.INSTANCE.log(feedback);
	}

	@Override
	public void handlePlayerJoin(String identifier) {
		ClientGame.INSTANCE.addPlayer(identifier);
		if (ClientGame.HAS_GUI) ScorePane.INSTANCE.updateScores();
	}

	@Override
	public void handleInitiategame(String board, String players) {
		for (String player: players.split(","))
			ClientGame.INSTANCE.addPlayer(player);
		ClientGame.INSTANCE.setPlayer(ClientGame.INSTANCE.getPlayer(identifier));
		if (ClientGame.HAS_GUI) ScorePane.INSTANCE.updateScores();
		handleUpdateTable(board);
	}

	@Override
	public void handleUpdateTable(String table) {
		ClientGame.INSTANCE.getBoard().fromString(table);
		if (!ClientGame.HAS_GUI) ClientGame.INSTANCE.print(ClientGame.INSTANCE.getBoard().getString());
		if (ClientGame.HAS_GUI) BoardPane.INSTANCE.updateTiles();
	}

	@Override
	public void handleFinishGame(String player, String score) {
		clearPlayers();
		Player c_player = ClientGame.INSTANCE.getPlayer();
		c_player.removeTiles(c_player.getTiles());
		c_player.setScore(0);
		ClientGame.INSTANCE.print("> " + AnsiColor.TEXT_MAGENTA + "[info]" + AnsiColor.RESET + " Player(s) \"" + player + "\" won with a score of " + score);
		if (!ClientGame.HAS_GUI) return;
		BoardPane.INSTANCE.updateTiles();
		RackPane.INSTANCE.updateTiles();
	}

	/**
	 * Print the list of all the rooms on the server
	 * @param room_list
	 */
	private void handleListRoom(String room_list) {
		if (room_list.equals(" ")) {
			ClientGame.INSTANCE.print("> " + AnsiColor.TEXT_MAGENTA + "[info]" + AnsiColor.RESET + " no rooms are accessible");
			return;
		}
		ClientGame.INSTANCE.print("> " + AnsiColor.TEXT_MAGENTA + "[info]" + AnsiColor.RESET + " room(s):" + room_list);
	}

	/**
	 * set the current player to the one the server sends
	 * @param player_id
	 */
	private void handleCurrentPlayer(String player_id) {
		Player player = ClientGame.INSTANCE.getPlayer(player_id);
		if (player == null) return;
		ClientGame.INSTANCE.setCurrentPlayer(player);
		if (!ClientGame.HAS_GUI) ClientGame.INSTANCE.print("> " + AnsiColor.TEXT_MAGENTA + "[info]" + AnsiColor.RESET + " it is " + player.getName() + "'s turn");
		if (ClientGame.HAS_GUI) ScorePane.INSTANCE.updateScores();
	}
	
	/**
	 * removes all the players except the client one from the list
	 */
	public void clearPlayers() {
		Player c_player = ClientGame.INSTANCE.getPlayer();
		List<IPlayer> players = ClientGame.INSTANCE.getPlayers();
		for (IPlayer player: players) {
			if (player.equals(c_player)) continue;
			ClientGame.INSTANCE.remove(player);
		}
	}

}
