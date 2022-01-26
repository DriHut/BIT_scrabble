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

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.server.ServerGame;
import fr.Adrien1106.util.interfaces.IServerHandler;
import fr.Adrien1106.util.protocol.ProtocolMessages;

public class ServerHandler implements Runnable, IServerHandler {

	private Socket socket;
	private BufferedReader input_reader;
	private BufferedWriter output_reader;

	private String name = "itsameee";
	
	@Override
	public void run() {
		try {
			setup();
			sendCommand(ProtocolMessages.CONNECT, Arrays.asList(name));
			String msg = input_reader.readLine();
			while (msg != null) {
				ClientGame.INSTANCE.print("> [server] Incoming: " + msg);
				handleCommand(msg);
				msg = input_reader.readLine();
			}
		} catch (IOException e) {
			ClientGame.INSTANCE.log("A server IO error occurred: " + e.getMessage());
		}
	}

	private void setup() {
		while (socket == null) {
			try {
				InetAddress addr = InetAddress.getByName("127.0.0.1");
				ClientGame.INSTANCE.log("Attempting to connect to " + addr + ":"  + ServerGame.PORT + "...");
				socket = new Socket(addr, ServerGame.PORT);
				input_reader = new BufferedReader(new InputStreamReader( socket.getInputStream()));
				output_reader = new BufferedWriter(new OutputStreamWriter( socket.getOutputStream()));
				ClientGame.INSTANCE.log("Connection restablished");
			} catch (IOException e) {
				ClientGame.INSTANCE.log("could not create a socket on " 
					+ "127.0.0.1" + " and port " + ServerGame.PORT + ".");
			}
		}
	}
	
	public void sendCommand(String command, List<String> args) {
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
	
	private void handleCommand(String msg) throws ArrayIndexOutOfBoundsException {
		String[] cmd = msg.split(ProtocolMessages.DELIMITER);
		switch (cmd[0]) {
		case ProtocolMessages.INITIATE_GAME:
			if (cmd.length != 3) return;
			handleInitiategame(cmd[1], cmd[2]);
			return;
		case ProtocolMessages.UPDATE_TABLE:
			if (cmd.length != 2) return;
			handleUpdateTable(cmd[1]);
			return;
		case ProtocolMessages.UPDATE_SCORE:
			if (cmd.length != 3) return;
			handleUpdateScore(cmd[1],cmd[2]);
			return;
		case ProtocolMessages.ADD_OR_REMOVE_PLAYER:
			if (cmd.length != 2) return;
			handlePlayerJoin(cmd[1]);
			return;
		case ProtocolMessages.FEEDBACK:
			if (cmd.length != 2) return;
			handleFeedback(cmd[1]);
			return;
		case ProtocolMessages.GIVE_TILE:
			if (cmd.length != 2) return;
			handleGiveTile(cmd[1]);
			return;
		case ProtocolMessages.FINISH_GAME:
			if (cmd.length != 2) return;
			handleFinishGame(cmd[1],cmd[2]);
			return;
		default:
		}
	}

	@Override
	public void handleGiveTile(String letters) {
		if (ClientGame.INSTANCE.getPlayer() == null) return;
		ClientGame.INSTANCE.getPlayer().removeTiles(ClientGame.INSTANCE.getPlayer().getTiles());
		ClientGame.INSTANCE.getPlayer().addTiles(letters);
		ClientGame.INSTANCE.print(ClientGame.INSTANCE.getPlayer().getTiles());
	}

	@Override
	public void handleUpdateScore(String identifier, String score) {
		Player player = ClientGame.INSTANCE.getPlayer(identifier);
		if (player == null) return;
		player.setScore(Integer.valueOf(score));
	}

	@Override
	public void handleFeedback(String feedback) {
		ClientGame.INSTANCE.log(feedback);
	}

	@Override
	public void handlePlayerJoin(String identifier) {
		ClientGame.INSTANCE.addPlayer(identifier);
	}

	@Override
	public void handleInitiategame(String board, String players) {
		for (String player: players.split(","))
			ClientGame.INSTANCE.addPlayer(player);
		ClientGame.INSTANCE.setPlayer(ClientGame.INSTANCE.getPlayer(name + "#1"));
		handleUpdateTable(board);
	}

	@Override
	public void handleUpdateTable(String table) {
		ClientGame.INSTANCE.getBoard().fromString(table);
		ClientGame.INSTANCE.print(ClientGame.INSTANCE.getBoard().getString());
	}

	@Override
	public void handleFinishGame(String player, String score) {
		ClientGame.INSTANCE.print("Player \"" + player + "\" won with a score of " + score);
	}

}
