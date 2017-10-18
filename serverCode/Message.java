import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.text.*;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private Date time;
    private String text;

    public Message(String sender, String receiver, Date time, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.text = text;
    }

    public String sender() {
        return this.sender;
    }

    public String receiver() {
        return this.receiver;
    }

    /*public Date time() {
        return this.time;
    }*/

    public String text() {
        return this.text;
    }

    public String time() {
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy, hh:mm:ss a");
        return (" (" + ft.format(this.time) + ")");
    }

    public String messageToString() {
        return this.time() + "5555555" + this.sender() + "5555555" + this.receiver() + "5555555" + this.text();
    }

    public static Message stringToMessage(String str) {
        String[] split = str.split("5555555");
        SimpleDateFormat ft = new SimpleDateFormat (" (dd.MM.yyyy, hh:mm:ss a)");
        Date date = new Date();
        try {
            date = ft.parse(split[0]);
        } catch (Exception e) {
            System.err.println(e);
        }
        return new Message(split[1], split[2], date, split[3]);
    }
}
