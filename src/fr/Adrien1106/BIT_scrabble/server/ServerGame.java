package fr.Adrien1106.BIT_scrabble.server;

import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.game.Room;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.util.exceptions.TooFewPlayersException;
import fr.Adrien1106.util.exceptions.TooManyPlayersException;
import fr.Adrien1106.util.interfaces.IPlayer;
import fr.Adrien1106.util.interfaces.IRoom;
import fr.Adrien1106.util.protocol.ProtocolMessages;
import fr.Adrien1106.util.protocol.ServerProtocol;

public class ServerGame implements ServerProtocol, Runnable {

	public static final ServerGame INSTANCE = new ServerGame();
	public static PrintStream out = System.out;
	
	private List<IRoom> rooms;
	private List<ClientHandler> clients;

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	public void register(String client_id, Socket socket) {
		ClientHandler client_handler = new ClientHandler(client_id, socket);
		new Thread(client_handler).start();
		clients.add(client_handler);
	}
	
	@Override
	public String doCreateRoom(String player_number) throws TooManyPlayersException, TooFewPlayersException, NumberFormatException {
		int amount = Integer.valueOf(player_number);
		if (amount > References.MAX_PLAYERS) throw new TooManyPlayersException(amount, References.MAX_PLAYERS);
		if (amount < References.MIN_PLAYERS) throw new TooFewPlayersException(amount, References.MIN_PLAYERS);
		
		IRoom room = new Room(rooms.get(rooms.size() - 1).getId() + 1, amount, References.MIN_PLAYERS);
		rooms.add(room); 
		return "" + room.getId();
	}
	
	@Override
	public synchronized void doStart(IRoom room) {
		List<String> args = new ArrayList<>();
		args.add(room.getBoard().toString());
		for (IPlayer player: room.getPlayers())
			args.add(((Player) player).getIdentifier());
		
		for (ClientHandler handler: clients)
			if (room.getPlayers().contains(handler.getPlayer())) handler.sendCommand(ProtocolMessages.INITIATE_GAME, args);
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
	
	public void removeClient(ClientHandler client) {
		this.clients.remove(client);
	}
	
	public void log(String message) {
		out.println("[LOG]" + message);
	}
	
	public synchronized Room getRoom(int id) {
		for (IRoom room: rooms) 
			if (room.getId() == id) return (Room) room;
		return null;
	}
}
