package fr.Adrien1106.BIT_scrabble.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.game.Room;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.util.render.AnsiColor;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.TooFewPlayersException;
import fr.Adrien1106.util.exceptions.TooManyPlayersException;
import fr.Adrien1106.util.interfaces.IRoom;
import fr.Adrien1106.util.protocol.ProtocolMessages;
import fr.Adrien1106.util.protocol.ServerProtocol;

public class ServerGame implements ServerProtocol, Runnable {

	public static final ServerGame INSTANCE = new ServerGame();
	public static final int PORT = 25500;
	
	private ServerSocket server_socket;
	private static PrintStream out = System.out;
	
	private List<IRoom> rooms;
	private List<ClientHandler> clients;
	private int last_client_id = 0;
	private int last_room_id = 0;
	
	private ServerGame() {
		Dictionary.loadFromRessource();
		clients = new ArrayList<>();
		rooms = new ArrayList<>();
	}

	@Override
	public void run() {
		try {
			setup();

			while (true) {
				Socket sock = server_socket.accept();
				register("" + ++last_client_id, sock);
			}
			
		} catch (IOException e) {
			log("A server IO error occurred: " + e.getMessage());
		}
	}
	
	/**
	 * Sets up the server instance and server socket listener
	 */
	private void setup() {
		while (server_socket == null) {
			try {
				server_socket = new ServerSocket(PORT, 0, InetAddress.getByName("127.0.0.1"));
				info("Server started at port " + PORT);
			} catch (IOException e) {
				log("A server IO error occurred: " + e.getMessage());
			}
		}
	}

	@Override
	public void register(String client_id, Socket socket) {
		ClientHandler client_handler = new ClientHandler(client_id, socket);
		new Thread(client_handler).start();
		clients.add(client_handler);
		info("new client connection: " + client_id);
	}
	
	@Override
	public String doCreateRoom(String player_number) throws TooManyPlayersException, TooFewPlayersException, NumberFormatException {
		int amount = Integer.valueOf(player_number);
		if (amount > References.MAX_PLAYERS) throw new TooManyPlayersException(amount, References.MAX_PLAYERS);
		if (amount < References.MIN_PLAYERS) throw new TooFewPlayersException(amount, References.MIN_PLAYERS);
		
		IRoom room = new Room(++last_room_id, amount, References.MIN_PLAYERS);
		rooms.add(room); 
		return "" + room.getId();
	}
	
	@Override
	public synchronized void doStart(IRoom room) {
		List<String> args = new ArrayList<>();
		args.add(room.getBoard().toString());
		String players = ((Player) room.getPlayers().get(0)).getIdentifier();
		for (int i = 1; i < room.getPlayers().size(); i++)
			players += "," + ((Player) room.getPlayers().get(i)).getIdentifier();
		args.add(players);
			
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer())) {
				handler.sendCommand(ProtocolMessages.INITIATE_GAME, args);
				String tiles = "";
				for (int i = 0; i < 7; i++)
					tiles += ((Room) room).getTile().getLetter();
				handler.getPlayer().addTiles(tiles);
				handler.sendCommand(ProtocolMessages.GIVE_TILE, Arrays.asList(handler.getPlayer().getTiles()));
			}
		doUpdateCurrentPlayer(room, (Player) room.getPlayers().get(0));
	}

	@Override
	public synchronized void doJoin(IRoom room, String identifier) {
		sendToAllInRoom(room, ProtocolMessages.ADD_OR_REMOVE_PLAYER, Arrays.asList(identifier), identifier);
	}

	@Override
	public synchronized void doUpdateScore(IRoom room, String info) {
		sendToAllInRoom(room, ProtocolMessages.UPDATE_SCORE, Arrays.asList(info), null);
	}
	
	@Override
	public synchronized void doUpdateTable(IRoom room, String table) {
		sendToAllInRoom(room, ProtocolMessages.UPDATE_TABLE, Arrays.asList(table), null);
	}
	
	/**
	 * Send an update on who is the current playing player in the room
	 * @param room - the room
	 * @param player - the current player
	 */
	public synchronized void doUpdateCurrentPlayer(IRoom room, Player player) {
		sendToAllInRoom(room, ProtocolMessages.CUSTOM_COMMAND + "cp", Arrays.asList(player.getIdentifier()), null);
	}
	
	/**
	 * Send message to all the player in a room
	 * @param room - the room to send the info from
	 * @param cmd - the command to be sent
	 * @param args - the message arguements
	 * @param except - the identifier of the player to which we don't send information to
	 */
	public synchronized void sendToAllInRoom(IRoom room, String cmd, List<String> args, String except) {
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer()) && !handler.getPlayer().getIdentifier().equals(except)) handler.sendCommand(cmd, args);
	}

	@Override
	public synchronized void doFinish(IRoom room, String best_player, int score) {
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer())) {
				handler.setRoom(null);
				handler.getPlayer().removeTiles(handler.getPlayer().getTiles());
			}
		sendToAllInRoom(room, ProtocolMessages.FINISH_GAME, Arrays.asList(best_player, "" + score), null);
		rooms.remove(room);
	}
	
	/**
	 * remove a given client from client list
	 * @param client
	 */
	public void removeClient(ClientHandler client) {
		this.clients.remove(client);
	}
	
	/**
	 * send a log to be printed
	 * @param message - message to be logged
	 */
	public void log(String message) {
		print("> " + AnsiColor.TEXT_BLUE + "[LOG]" + AnsiColor.RESET + " " + message);
	}
	
	/**
	 * send an info to be printed
	 * @param message - message to be printed
	 */
	public void info(String message) {
		print("> " + AnsiColor.TEXT_MAGENTA + "[INFO]" + AnsiColor.RESET + " " + message);
	}
	
	/**
	 * print info to the given output
	 * @param message - message to be printed
	 */
	public void print(String message) {
		out.println(message);
	}
	
	/**
	 * Get room from id
	 * @param id - the room id to get
	 * @return the room or null if no such room
	 */
	public synchronized Room getRoom(int id) {
		for (IRoom room: rooms) 
			if (room.getId() == id) return (Room) room;
		return null;
	}

	/**
	 * @return the list of all the rooms
	 */
	public synchronized List<IRoom> getRooms() {
		return rooms;
	}
	
	/**
	 * run server instance
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(ServerGame.INSTANCE).start();
	}
}
