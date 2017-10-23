package com.ru.tgra.network;

import com.ru.tgra.shapes.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameClient extends Thread
{
	protected int serverPort = 1234;
	private Socket socket = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private String nickname = "";
	private Random rand = new Random();
	private PackageState lastPackageState;
	private List<PackageState> wallPackets = new ArrayList<PackageState>();

	public static void main(String[] args) throws Exception {
		new GameClient();
	}

	public GameClient() throws Exception {
		// connect to the chat server
		BufferedReader std_in = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("[system] connecting to the server ...");
			boolean nick_ok = false;
			while (!nick_ok) {
				//nickname = std_in.readLine();
				nickname = "user_" + Integer.toString(rand.nextInt(1000000));
				nick_ok = checkNickname(nickname);
			}
			socket = new Socket("localhost", serverPort); // create socket connection
			out = new ObjectOutputStream(socket.getOutputStream()); // create output stream for sending messages
			in = new ObjectInputStream(socket.getInputStream()); // create input stream for listening for incoming messages
			Message con = new Message(nickname, "__server__", new Date(), "init");
			this.sendMessage(con, out);

			GameClientMessageReceiver message_receiver = new GameClientMessageReceiver(in, this); // create a separate thread for listening to messages from the chat server
			message_receiver.start(); // run the new thread

			System.out.println("[system] connected");
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// cleanup
		/*out.close();
		in.close();
		std_in.close();
		socket.close();*/
	}

	private boolean checkNickname(String nickname) {
		if (nickname.equals("")) {
			System.out.println("[system] enter at least one character");
			return false;
		}
		for (int i = 0; i < nickname.length(); i++) {
			if (nickname.charAt(i) == '*') {
				System.out.println("[system] do not use '*' in your nickname");
				return false;
			}
		}
		return true;
	}

	private void sendMessage(Message message, ObjectOutputStream out) {
		try {
			out.writeObject(message.messageToString()); // Send the message to the chat server
			out.flush(); // ensure the message has been sent
		} catch (IOException e) {
			System.err.println("[system] could not send message");
			e.printStackTrace(System.err);
		}
	}

	public void sendToServer(Point3D playerPosition, Vector3D playerDirection) {
		// read from STDIN and send messages to the chat server
		//String userInput = position.toString();
		PackageState p = new PackageState(playerPosition, playerDirection);
		sendPackage(p);
	}

	public void sendToServer(Wall wall) {
		PackageState p = new PackageState(wall);
		sendPackage(p);
	}

	public void sendPackage(PackageState p) {
		String userInput = p.toStringToSend();
		String receiver = "__server__";
		Date time;
		String text = "";
		if (userInput.charAt(0) == '*') {
			// private
			int i = 1;
			while (i < userInput.length() && userInput.charAt(i) != '*') {
				i++;
			}
			if (i == userInput.length()) {
				// '*something ...'
				System.out.println("[system] invalid receiver name");
			} else {
				// '*name*text'
				receiver = userInput.substring(1, i);
				text = userInput.substring(i + 1, userInput.length());
			}
		} else {
			// "broadcast"
			text = userInput;
		}
		time = new Date();
		Message message = new Message(nickname, receiver, time, text);

		this.sendMessage(message, out); // send the message to the server
	}

	public PackageState getLastPackageState() {
		return this.lastPackageState;
	}

	public void setLastPackageState(PackageState lastPackageState) {
		this.lastPackageState = lastPackageState;
	}

	public void addWallPackage(PackageState wallPackage) {
		this.wallPackets.add(wallPackage);
	}

	public PackageState getWallPackage() {
		if (this.wallPackets.size() > 0) {
			PackageState pac = this.wallPackets.get(0);
			this.wallPackets.remove(0);
			return pac;
		} else {
			return null;
		}
	}
}

// wait for messages from the chat server and print the out
class GameClientMessageReceiver extends Thread {
	private ObjectInputStream in;
	private GameClient client;

	public GameClientMessageReceiver(ObjectInputStream in, GameClient client) {
		this.in = in;
		this.client = client;
	}

	public void run() {
		try {
			Message message;
			String rawMessage;
			while ((rawMessage = (String)this.in.readObject()) != null) { // read new message
				message = Message.stringToMessage(rawMessage);
				//System.out.println("[" + message.sender() + "] " + message.text() + message.time()); // print the message to the console
				PackageState p = PackageState.stringToPackage(message.text());
				p.reflectView();
				if (p.getType().equals("position"))
					this.client.setLastPackageState(p);
				else if (p.getType().equals("newwall"))
					this.client.addWallPackage(p);
			}
		} catch (Exception e) {
			System.err.println("[system] could not read message");
			e.printStackTrace(System.err);
		}
	}
}
