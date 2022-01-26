package fr.Adrien1106.BIT_scrabble.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.game.Room;
import fr.Adrien1106.BIT_scrabble.util.Align;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.util.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.util.exceptions.InvalidNameException;
import fr.Adrien1106.util.exceptions.NotOwnedTileException;
import fr.Adrien1106.util.exceptions.NotTurnException;
import fr.Adrien1106.util.exceptions.ProtocolException;
import fr.Adrien1106.util.exceptions.RoomFullException;
import fr.Adrien1106.util.exceptions.TooFewPlayersException;
import fr.Adrien1106.util.exceptions.TooManyPlayersException;
import fr.Adrien1106.util.exceptions.UnknownRoomException;
import fr.Adrien1106.util.exceptions.UnknownTileException;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;
import fr.Adrien1106.util.interfaces.IClientHandler;
import fr.Adrien1106.util.protocol.ProtocolMessages;

public class ClientHandler implements Runnable, IClientHandler {


	private BufferedReader input_stream;
	private BufferedWriter output_stream;
	private Socket socket;
	
	private Room room;
	private String client_id;
	private Player player;
	
	public ClientHandler(String client_id, Socket socket) {
		try {
			input_stream = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
			output_stream = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) );
			this.socket = socket;
			this.client_id = client_id;
		} catch (IOException e) {
			shutdown();
		}
	}

	@Override
	public void run() {
		String msg;
		try {
			msg = input_stream.readLine();
			while (msg != null) {
				ServerGame.INSTANCE.print("> \u001b[32m[CLIENT#" + client_id + "]\u001b[0m Incoming: " + msg);
				try {
					handleCommand(msg);
				} catch (Exception e) {
					ServerGame.INSTANCE.log(e.getMessage());
				}
				msg = input_stream.readLine();
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
		
	}
	
	private void handleCommand(String msg) throws ArrayIndexOutOfBoundsException {
		String[] cmd = msg.split(ProtocolMessages.DELIMITER);
		try {
			switch (cmd[0]) {
			case ProtocolMessages.CONNECT:
				if (cmd.length != 2) return;
				ServerGame.INSTANCE.print("> \u001b[32m[CLIENT#" + client_id + "]\u001b[0m connect");
				handleConnect(msg.split(ProtocolMessages.DELIMITER)[1]);
				return;
			case ProtocolMessages.CREATE_ROOM:
				if (cmd.length != 2) return;
				ServerGame.INSTANCE.print("> \u001b[32m[CLIENT#" + client_id + "]\u001b[0m create room");
				handleCreateRoom(cmd[1]);
				return;
			case ProtocolMessages.JOIN_ROOM:
				if (cmd.length != 2) return;
				ServerGame.INSTANCE.print("> \u001b[32m[CLIENT#" + client_id + "]\u001b[0m join room");
				handleJoinRoom(cmd[1]);
				return;
			case ProtocolMessages.MAKE_MOVE:
				if (cmd.length != 4) return;
				ServerGame.INSTANCE.print("> \u001b[36m[ROOM#" + room.getId() + ":" + client_id + "]\u001b[0m makes move");
				handleMove(cmd[1],cmd[2],cmd[3]);
				return;
			case ProtocolMessages.SKIP_TURN:
				if (cmd.length != 1) return;
				ServerGame.INSTANCE.print("> \u001b[36m[ROOM#" + room.getId() + ":" + client_id + "]\u001b[0m skip turn");
				handleSkip();
				return;
			case ProtocolMessages.REPLACE_TILES:
				if (cmd.length != 2) return;
				ServerGame.INSTANCE.print("> \u001b[36m[ROOM#" + room.getId() + ":" + client_id + "]\u001b[0m replace tiles");
				handleReplaceTiles(cmd[1]);
				return;
			case ProtocolMessages.CUSTOM_COMMAND + "fs":
				if (cmd.length != 1) return;
				ServerGame.INSTANCE.print("> \u001b[36m[ROOM#" + room.getId() + ":" + client_id + "]\u001b[0m force start");
				handleForceStart();
				return;
			default:
			}
		} catch (ProtocolException e) {
			sendCommand(e.getId(), Arrays.asList(e.getMessage()));
			ServerGame.INSTANCE.log(e.getMessage());
		}
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Room getRoom() {
		return room;
	}

	private void shutdown() {
		ServerGame.INSTANCE.log("client is closing: " + client_id);
		if (room != null) room.removePlayer(player);
		try {
			input_stream.close();
			output_stream.close();
			socket.close();
		} catch (IOException e) {
			ServerGame.INSTANCE.log(e.getMessage());
		}
		ServerGame.INSTANCE.removeClient(this);
	}
	
	public void sendCommand(String command, List<String> args) {
		String msg = command;
		for (String arg: args)
			msg += ProtocolMessages.DELIMITER + arg;
		try {
			output_stream.write(msg);
			output_stream.newLine();
			output_stream.flush();
			ServerGame.INSTANCE.print("> \u001b[32m[CLIENT#" + client_id + "]\u001b[0m Sending: " + msg);
		} catch (IOException e) {
			ServerGame.INSTANCE.log(e.getMessage());
		}
	}

	@Override
	public void handleCreateRoom(String player_number) throws NumberFormatException, TooManyPlayersException, TooFewPlayersException { //
		String room_id = ServerGame.INSTANCE.doCreateRoom(player_number);
		try {
			handleJoinRoom(room_id);
		} catch (RoomFullException | UnknownRoomException e) { 
			ServerGame.INSTANCE.log(e.getMessage());
		}
	}
	
	@Override
	public void handleConnect(String name) throws InvalidNameException {
		player = new Player(name + "#" + client_id);
		sendCommand(ProtocolMessages.FEEDBACK, Arrays.asList(player.getIdentifier()));
	}

	@Override
	public void handleJoinRoom(String room_id) throws RoomFullException, UnknownRoomException {
		int id = Integer.valueOf(room_id);
		room = ServerGame.INSTANCE.getRoom(id);
		if (room == null) throw new UnknownRoomException(id);
		try {
			room.addPlayer(player);
		} catch (TooManyPlayersException e) {
			room = null;
			ServerGame.INSTANCE.log(e.getMessage());
			throw new RoomFullException(id);
		}
		
		List<String> args = new ArrayList<>();
		
		args.add(room.getBoard().toString());
		String players = ((Player) room.getPlayers().get(0)).getIdentifier();
		for (int i = 1; i < room.getPlayers().size(); i++) {
			players += "," + ((Player) room.getPlayers().get(i)).getIdentifier();
		}
		args.add(players);
		
		ServerGame.INSTANCE.doJoin(room, player.getIdentifier());
		sendCommand(ProtocolMessages.INITIATE_GAME, args);
	}

	@Override
	public void handleMove(String alignment, String coordinates, String word) throws NotOwnedTileException, NotTurnException, WordOutOfBoundsException, UnknownTileException, WrongCoordinateException, CantPlaceWordHereException {
		Board board = (Board) room.getBoard();
		Align align = Align.valueOf(alignment.toUpperCase());
		
		if (!room.isTurn(player)) throw new NotTurnException();
		try {
			int score = board.place(coordinates, word, align);
			
			String used_letters = board.getUsedTiles(coordinates, word, align);
			if (!player.hasTiles(used_letters)) throw new NotOwnedTileException(player.getTiles() + ": " + used_letters);
			
			player.removeTiles(used_letters);
			used_letters = getNewTiles(used_letters.length());
			player.addTiles(used_letters);
			sendCommand(ProtocolMessages.GIVE_TILE, Arrays.asList(player.getTiles()));
			player.addScore(score);
			
			board.saveMove();
			room.next();
		} catch (NumberFormatException e) {
			throw new WrongCoordinateException(coordinates);
		}
	}

	@Override
	public void handleSkip() throws NotTurnException {
		if (!room.isTurn(player)) throw new NotTurnException();
		room.next();
		sendCommand(ProtocolMessages.FEEDBACK, Arrays.asList("true"));
	}

	@Override
	public void handleReplaceTiles(String tiles) throws NotOwnedTileException, NotTurnException { 
		if (!room.isTurn(player)) throw new NotTurnException();
		if (!player.hasTiles(tiles)) throw new NotOwnedTileException(tiles);
		String new_tiles = getNewTiles(tiles.length());
		
		int length = tiles.length();
		if (new_tiles.length() < tiles.length()) {
			for (int i = 0; i < length; i++) {
				if (i+1 == length) {
					new_tiles += tiles;
					tiles = "";
				} else {
					new_tiles += tiles.substring(i,i+1);
					tiles = tiles.substring(i+1);
				}
			}
		}
		
		room.addTiles(tiles);
		player.removeTiles(tiles);
		player.addTiles(new_tiles);

		sendCommand(ProtocolMessages.GIVE_TILE, Arrays.asList(player.getTiles()));
		room.next();
	}

	public void handleForceStart() throws TooFewPlayersException {
		room.start();
	}
	
	private String getNewTiles(int amount) {
		String output = "";
		for(int i = 0; i < amount; i++) {
			String letter = room.getTile().getLetter();
			if (letter == null) break; // no tiles left
			output += letter;
		}
		return output;
	}

	public Player getPlayer() {
		return player;
	}
}
