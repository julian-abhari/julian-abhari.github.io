package com.Julian.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.Julian.game.Game;
import com.Julian.game.entities.PlayerMP;
import com.Julian.game.net.packets.Packet;
import com.Julian.game.net.packets.Packet.PacketTypes;
import com.Julian.game.net.packets.Packet00Login;
import com.Julian.game.net.packets.Packet01Disconnect;
import com.Julian.game.net.packets.Packet02Move;

// The way all of this multiplayer support is being programmed is through UDP not TCP. 
// So there will be a lot of low level programming with packets, 
// unlike TCP where you just need to worry about sockets. 
// The reason why I'm using UDP instead of TCP is because if a packet is lost in TCP, 
// then the servers will wait until the packet is resent then everything else will 
// all happen at once. With UDP, if a packet is lost then it will just move on to 
// the next packet and not worry about the lost packets.
public class GameServer extends Thread {

	private DatagramSocket socket;
	private Game game;
	private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();

	public GameServer(Game game, int port) {
		this.game = game;
		try {
			// This is creating a new socket of the port that the server specifies.
			// Keep in mind that the client must be sending the data (in the "sendData"
			// function) to the same ipAddress and port number that the server specifies
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			// When the program is running it will listen for an array of bytes of data that
			// was received from the Client
			byte[] data = new byte[1024];
			// This is the actual packet that's going to be sent to and from the server.
			DatagramPacket packet = new DatagramPacket(data, data.length);
			// This line is accepting the data that it received
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			// String message = new String(packet.getData());
			// // This prints out the array of data that the client sends to the server
			// System.out.println("CLIENT [" + packet.getAddress().getHostAddress() + ": " +
			// packet.getPort() + "] > " + message);
			//
			// if (message.trim().equalsIgnoreCase("ping")) {
			// sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
			// }

		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;

		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("SERVER: [" + address.getHostAddress() + ": " + port + "] "
					+ ((Packet00Login) packet).getUsername() + " has connected...");
			PlayerMP player = new PlayerMP(game.level, (game.level.width / 2) * 8, (game.level.height / 2) * 8,
					((Packet00Login) packet).getUsername(), address, port);
			this.addConnection(player, (Packet00Login) packet);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("SERVER: [" + address.getHostAddress() + ": " + port + "] "
					+ ((Packet01Disconnect) packet).getUsername() + " has left the game...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
		case MOVE:
			packet = new Packet02Move(data);
			this.handleMove(((Packet02Move) packet));
			break;
		}
	}

	public void addConnection(PlayerMP player, Packet00Login packet) {
		Packet00Login parameterPacket = packet;
		boolean alreadyConnected = false;
		for (PlayerMP p : this.connectedPlayers) {
			packet = parameterPacket;
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}

				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				// Telay to the currently connected player that there is a new player
				sendData(packet.getData(), p.ipAddress, p.port);
				// Relay to the new player that the currently connected player exists
				packet = new Packet00Login(p.getUsername(), p.x, p.y, p.getNumSteps(), p.isMoving(), p.getMovingDir());
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}

	public void removeConnection(Packet01Disconnect packet) {
		this.connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
		packet.writeData(this);
	}

	public PlayerMP getPlayerMP(String username) {
		for (PlayerMP player : this.connectedPlayers) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public int getPlayerMPIndex(String username) {
		int index = 0;
		for (PlayerMP player : this.connectedPlayers) {
			if (player.getUsername().equals(username)) {
				return index;
			}
			index += 1;
		}
		return index;
	}

	// This function allows us to send a byte[] of data
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		// This creates a new packet sent to the ipAddress with the port number
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (PlayerMP p : this.connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}

	private void handleMove(Packet02Move packet) {
		if (getPlayerMP(packet.getUsername()) != null) {
			int index = getPlayerMPIndex(packet.getUsername());
			PlayerMP player = this.connectedPlayers.get(index);
			player.x = packet.getX();
			player.y = packet.getY();
			player.setNumSteps(packet.getMovingDir());
			player.setMoving(packet.isMoving());
			player.setMovingDir(packet.getMovingDir());
			packet.writeData(this);
		}
	}
}
