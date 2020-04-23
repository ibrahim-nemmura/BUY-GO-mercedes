package model;


public class ChatDoc {

    public static String SENDER="sender";
    public static String CHATID="chatid";
    public static String LASTMSG="lastMessage";
    public static String TIME="timestamp";

    private String sender;
    private String chatid;
    private String lastMessage;
    private String timestamp;

    public ChatDoc() { }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
