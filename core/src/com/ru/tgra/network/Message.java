import java.io.*;
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
}
