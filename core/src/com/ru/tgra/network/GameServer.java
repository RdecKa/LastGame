import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

	protected int serverPort = 1234;
	protected List<Socket> clients = new ArrayList<Socket>(); // list of clients
	protected List<ObjectOutputStream> outi = new ArrayList<ObjectOutputStream>();
	protected List<String> nicknames = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		new GameServer();
	}

	public GameServer() {
		ServerSocket serverSocket = null;

		// create socket
		try {
			serverSocket = new ServerSocket(this.serverPort); // create the ServerSocket
		} catch (Exception e) {
			System.err.println("[system] could not create socket on port " + this.serverPort);
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// start listening for new connections
		System.out.println("[system] listening ...");
		try {
			while (true) {
				Socket newClientSocket = serverSocket.accept(); // wait for a new client connection
				ObjectOutputStream out = new ObjectOutputStream(newClientSocket.getOutputStream());
				synchronized(this) {
					clients.add(newClientSocket); // add client to the list of clients
					outi.add(out);
				}
				GameServerConnector conn = new GameServerConnector(this, newClientSocket, out); // create a new thread for communication with the new client
				conn.start(); // run the new thread
			}
		} catch (Exception e) {
			System.err.println("[error] Accept failed.");
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// close socket
		System.out.println("[system] closing server socket ...");
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	// send a message to all clients connected to the server
	public void sendToAllClients(Message message) throws Exception {
		Iterator<Socket> i = clients.iterator();
		Iterator<ObjectOutputStream> j = outi.iterator();
		while (i.hasNext()) { // iterate through the client list
			Socket socket = (Socket) i.next(); // get the socket for communicating with this client
			ObjectOutputStream out = (ObjectOutputStream) j.next();
			try {
				out.writeObject(message); // send message to the client
			} catch (Exception e) {
				System.err.println("[system] could not send message to a client");
				e.printStackTrace(System.err);
			}
		}
	}

	public void sendPrivateMessage(Message msg_send) throws Exception {
		String receiver = msg_send.receiver();
		Iterator<String> i = nicknames.iterator();
		Iterator<ObjectOutputStream> j = outi.iterator();
		boolean success = false;
		while (i.hasNext()) {
			String nick = (String) i.next();
			ObjectOutputStream out = j.next();
			if (nick.equals(receiver)) {
				success = true;
				out.writeObject(msg_send);
				break;
			}
		}
		if (!success) {
			Message err = new Message("system", msg_send.sender(), new Date(), "no client with nickname '" + msg_send.receiver() + "'");
			sendPrivateMessage(err);
		}
	}

	public void removeClient(Socket socket, ObjectOutputStream out, String nickname) {
		synchronized(this) {
			clients.remove(socket);
			outi.remove(out);
			nicknames.remove(nickname);
		}
	}

	public void addNickname(String nickname) {
		synchronized(this) {
			nicknames.add(nickname);
		}
	}
}

class GameServerConnector extends Thread {
	private GameServer server;
	private Socket socket;
	private ObjectOutputStream out;
	private String nickname;

	public GameServerConnector(GameServer server, Socket socket, ObjectOutputStream out) {
		this.server = server;
		this.socket = socket;
		this.out = out;
		this.nickname = "";
	}

	public void run() {
		System.out.println("[system] connected with " + this.socket.getInetAddress().getHostName() + ":" + this.socket.getPort());

		ObjectInputStream in;
		try {
			in = new ObjectInputStream(this.socket.getInputStream()); // create input stream for listening for incoming messages
		} catch (IOException e) {
			System.err.println("[system] could not open input stream!");
			e.printStackTrace(System.err);
			this.server.removeClient(socket, out, nickname);
			return;
		}

		Message init;
		try {
			init = (Message)in.readObject();
			this.nickname = init.sender();
			this.server.addNickname(init.sender());
		} catch (Exception e) {
			System.err.println("[system] there was a problem while reading your nickname");
			e.printStackTrace(System.err);
			this.server.removeClient(this.socket, this.out, this.nickname);
			return;
		}

		while (true) { // infinite loop in which this thread waits for incoming messages and processes them
			Message msg_received;
			try {
				msg_received = (Message)in.readObject(); // read the message from the client
			} catch (Exception e) {
				System.err.println("[system] there was a problem while reading message client on port " + this.socket.getPort());
				e.printStackTrace(System.err);
				this.server.removeClient(this.socket, this.out, this.nickname);
				return;
			}

			if (msg_received.text().length() == 0) // invalid message
				continue;

			System.out.println("[RKchat] [" + msg_received.sender() + "] : " + msg_received.text() + msg_received.time()); // print the incoming message in the console

			Message msg_send = msg_received;
			try {
				if (msg_send.receiver() == null || msg_send.receiver().equals("")) {
					this.server.sendToAllClients(msg_send); // send message to all clients
				} else {
					this.server.sendPrivateMessage(msg_send);
				}
			} catch (Exception e) {
				System.err.println("[system] there was a problem while sending the message to all clients");
				e.printStackTrace(System.err);
				continue;
			}
		}
	}
}
