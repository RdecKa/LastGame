import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

	protected int serverPort = 1234;
	protected List<User> users = new ArrayList<User>();

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
				if (this.users.size() < 2) {
					ObjectOutputStream out = new ObjectOutputStream(newClientSocket.getOutputStream());
					User newUser = new User(newClientSocket, out);
					this.users.add(newUser);
					GameServerConnector conn = new GameServerConnector(this, newUser); // create a new thread for communication with the new client
					conn.start(); // run the new thread
				}
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
		Iterator<User> u = this.users.iterator();
		while (u.hasNext()) { // iterate through the client list
			User user = u.next();
			String nick = user.getNickname();
			if (nick.equals(message.sender())) {
				continue;
			}
			Socket socket = user.getSocket(); // get the socket for communicating with this client
			ObjectOutputStream out = user.getOutputStream();
			try {
				out.writeObject(message.messageToString()); // send message to the client
			} catch (Exception e) {
				System.err.println("[system] could not send message to a client");
				e.printStackTrace(System.err);
			}
		}
	}

	public void sendPrivateMessage(Message msg_send) throws Exception {
		String receiver = msg_send.receiver();
		Iterator<User> u = this.users.iterator();
		boolean success = false;
		while (u.hasNext()) {
			User user = u.next();
			String nick = user.getNickname();
			ObjectOutputStream out = user.getOutputStream();
			if (nick.equals(receiver)) {
				success = true;
				out.writeObject(msg_send.messageToString());
				break;
			}
		}
		if (!success) {
			Message err = new Message("system", msg_send.sender(), new Date(), "no client with nickname '" + msg_send.receiver() + "'");
		}
	}

	public void removeClient(User user) {
		users.remove(user);
	}

	public void addNickname(User user, String nickname) {
		user.setNickname(nickname);
	}
}

class GameServerConnector extends Thread {
	private GameServer server;
	private User user;

	public GameServerConnector(GameServer server, User user) {
		this.server = server;
		this.user = user;
	}

	public void run() {
		Socket socket = this.user.getSocket();
		System.out.println("[system] connected with " + socket.getInetAddress().getHostName() + ":" + socket.getPort());

		ObjectInputStream in;
		try {
			in = new ObjectInputStream(socket.getInputStream()); // create input stream for listening for incoming messages
		} catch (IOException e) {
			System.err.println("[system] could not open input stream!");
			e.printStackTrace(System.err);
			return;
		}

		Message init;
		try {
			String nn = (String)in.readObject();
			init = Message.stringToMessage(nn);
			this.user.setNickname(init.sender());
		} catch (Exception e) {
			System.err.println("[system] there was a problem while reading your nickname");
			e.printStackTrace(System.err);
			this.server.removeClient(this.user);
			return;
		}

		while (true) { // infinite loop in which this thread waits for incoming messages and processes them
			Message msg_received;
			try {
				Object m = in.readObject(); // read the message from the client
			    String msg = (String)m;
				msg_received = Message.stringToMessage(msg);
			} catch (Exception e) {
				System.err.println("[system] there was a problem while reading message from client on port " + socket.getPort() + ". Perhaps the player left the game.");
				this.server.removeClient(this.user);
				return;
			}

			if (msg_received.text().length() == 0) // invalid message
				continue;

			Message msg_send = msg_received;
			try {
				if (msg_send.receiver() == null || msg_send.receiver().equals("") || msg_send.receiver().equals("__server__")) {
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

class User {
	protected Socket socket;
	protected ObjectOutputStream outstream;
	protected String nickname;

	public User(Socket socket, ObjectOutputStream outstream) {
		this.socket = socket;
		this.outstream = outstream;
		this.nickname = "";
	}

	public void addNickname(String nickname) {
		this.nickname = nickname;
	}

	public Socket getSocket() {
		return this.socket;
	}
	
	public ObjectOutputStream getOutputStream() {
		return this.outstream;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
