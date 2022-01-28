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
	
	private void setup() {
		Dictionary.loadFromRessource();
		clients = new ArrayList<>();
		rooms = new ArrayList<>();
		while (server_socket == null) {
			try {
				server_socket = new ServerSocket(PORT, 0, InetAddress.getByName("127.0.0.1"));
				log("Server started at port " + PORT);
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
		log("new client connection: " + client_id);
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
	}

	@Override
	public synchronized void doJoin(IRoom room, String identifier) {
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer()) && !handler.getPlayer().getIdentifier().equals(identifier)) handler.sendCommand(ProtocolMessages.ADD_OR_REMOVE_PLAYER, Arrays.asList(identifier));
	}

	@Override
	public synchronized void doUpdateScore(IRoom room, String info) {
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer())) handler.sendCommand(ProtocolMessages.UPDATE_SCORE, Arrays.asList(info));
	}
	
	@Override
	public synchronized void doUpdateTable(IRoom room, String table) {
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer())) handler.sendCommand(ProtocolMessages.UPDATE_TABLE, Arrays.asList(table));
	}

	@Override
	public synchronized void doFinish(IRoom room, String best_player, int score) {
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer())) {
				handler.setRoom(null);
				handler.getPlayer().removeTiles(handler.getPlayer().getTiles());
				handler.sendCommand(ProtocolMessages.FINISH_GAME, Arrays.asList(best_player, "" + score));
			}
		rooms.remove(room);
	}
	
	public void removeClient(ClientHandler client) {
		this.clients.remove(client);
	}
	
	public void log(String message) {
		print("> \u001b[34m[LOG]\u001b[0m " + message);
	}
	
	public void print(String message) {
		out.println(message);
	}
	
	public synchronized Room getRoom(int id) {
		for (IRoom room: rooms) 
			if (room.getId() == id) return (Room) room;
		return null;
	}
	
	public static void main(String[] args) {
		new Thread(ServerGame.INSTANCE).start();
	}
}
