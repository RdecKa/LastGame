import java.io.*;
import java.net.*;

public class GameClient{
    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    GameClient(){}
    void run()
    {
        try{
            //1. creating a socket to connect to the server
            requestSocket = new Socket("localhost", 4447);
            System.out.println("Connected to localhost in port 4447");
            //2. get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            //3: Communicating with the server - Say Hello
            do{
                try{
                    message = (String)in.readObject();
                    System.out.println("server>" + message);
                    sendMessage("Hi my server");
                    message = "bye";
                    sendMessage(message);
                }
                catch(ClassNotFoundException classNot){
                    System.err.println("data received in unknown format");
                }
            }while(!message.equals("bye"));

            // Send data
            int data = 1;
            while (data < 30) {
                try {
                    sendMessage(Integer.toString(data));
                    data++;
                }
                catch (Exception e) {
                    System.err.println("Cannot send a message, error: " + e.getMessage());
                }
            }
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
    void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    public static void main(String args[])
    {
        GameClient client = new GameClient();
        client.run();
    }
}
