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
import fr.Adrien1106.util.interfaces.IClientHandler;
import fr.Adrien1106.util.interfaces.IPlayer;
import fr.Adrien1106.util.exceptions.*;
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
		// TODO Auto-generated method stub
		
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Room getRoom() {
		return room;
	}

	private void shutdown() {
		ServerGame.INSTANCE.log("[" + client_id + "] Shutting down.");
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
		
	}

	@Override
	public void handleCreateRoom(String player_number) throws NumberFormatException, TooManyPlayersException, TooFewPlayersException {
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
		for (IPlayer player: room.getPlayers()) {
			args.add(((Player) player).getIdentifier());
		}
		
		ServerGame.INSTANCE.doJoin(room, player.getIdentifier());
		sendCommand(ProtocolMessages.INITIATE_GAME, args);
	}

	@Override
	public void handleMove(String alignment, String coordinates, String word) throws NotOwnedTileException, NotTurnException, WordOutOfBoundsException, UnknownTileException, WrongCoordinateException, CantPlaceWordHereException {
		Board board = (Board) room.getBoard();
		Align align = Align.valueOf(word.toUpperCase());
		
		if (room.isTurn(player)) throw new NotTurnException();
		try {
			int score = board.place(coordinates, word, align);
			
			String used_letters = board.getUsedLetters(coordinates, word, align);
			if (!player.hasTiles(word)) throw new NotOwnedTileException(used_letters);
			
			used_letters = getNewTiles(used_letters.length());
			player.addTiles(used_letters);
			sendCommand(ProtocolMessages.GIVE_TILE, Arrays.asList(used_letters));
			player.addScore(score);
			
			board.saveMove();
			room.next();
		} catch (NumberFormatException e) {
			throw new WrongCoordinateException(coordinates);
		}
	}

	@Override
	public void handleSkip() throws NotTurnException {
		if (room.isTurn(player)) throw new NotTurnException();
		room.next();
		sendCommand(ProtocolMessages.FEEDBACK, Arrays.asList("true"));
	}

	@Override
	public void handleReplaceTiles(String tiles) throws NotOwnedTileException {
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
		player.addTiles(new_tiles);

		sendCommand(ProtocolMessages.GIVE_TILE, Arrays.asList(new_tiles));
		room.next();
	}

	@Override
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
