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
	private PackageState defeat;
	private PackageState bullet;
	private PackageState newStart;

	public static void main(String[] args) throws Exception {
		new GameClient();
	}

	public GameClient() throws Exception {
		// connect to the chat server
		try {
			System.out.println("[system] connecting to the server ...");
			socket = new Socket("localhost", serverPort); // create socket connection
			nickname = "user_" + Integer.toString(rand.nextInt(1000000));
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
		PackageState p = new PackageState(playerPosition, playerDirection);
		sendPackage(p);
	}

	public void sendToServer(Wall wall) {
		PackageState p = new PackageState(wall);
		sendPackage(p);
	}

	public void announceVictory(boolean win) {
		PackageState p = new PackageState(win);
		sendPackage(p);
	}

	public void sendToServer(Point3D bulletPosition) {
		PackageState p = new PackageState(bulletPosition);
		sendPackage(p);
	}

	public void announceNewStart() {
		PackageState p = new PackageState();
		sendPackage(p);
	}

	public void sendPackage(PackageState p) {
		String userInput = p.toStringToSend();
		String receiver = "__server__";
		Date time;
		String text = userInput;
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

	public void setDefeat(PackageState p) {
		this.defeat = p;
	}

	public PackageState getDefeat() {
		PackageState tmp = this.defeat;
		this.defeat = null;
		return tmp;
	}

	public void addBulletPackage(PackageState bulletPackage) {
		this.bullet = bulletPackage;
	}

	public PackageState getBullet() {
		PackageState p = this.bullet;
		this.bullet = null;
		return p;
	}

	public void setNewStart(PackageState p) {
		this.newStart = p;
	}

	public boolean isNewStart() {
		if (this.newStart != null) {
			this.newStart = null;
			return true;
		}
		return false;
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
				else if (p.getType().equals("defeat"))
					this.client.setDefeat(p);
				else if (p.getType().equals("bullet"))
					this.client.addBulletPackage(p);
				else if (p.getType().equals("init"))
					this.client.setNewStart(p);
				else
					System.out.println("This type of package is not supported.");
			}
		} catch (Exception e) {
			System.err.println("[system] could not read message");
			e.printStackTrace(System.err);
		}
	}
}
