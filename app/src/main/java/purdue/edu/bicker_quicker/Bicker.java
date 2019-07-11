package purdue.edu.bicker_quicker;

import java.util.ArrayList;
import java.util.Date;

public class Bicker {
    private String title;
    private String description;
    private String left_side;
    private String right_side;
    private Date create_date;
    private int left_votes;
    private int right_votes;
    private String code;
    private String category;
    private String senderID;
    private String receiverID; // This is used temporarily to store bickerID from db in callback()
    private String key;
    private boolean voted;
    private ArrayList<String> tags = new ArrayList<String>();
    private ArrayList<String> votedUsers = new ArrayList<String>();
    private double seconds_until_expired;

    public Bicker (){

    }
    public Bicker(String title, String left_Side, String right_side,
                  int left_votes, int right_votes, String category, String key, double seconds) {
        this(title, null,  left_Side, right_side, null, left_votes, right_votes, null, category, null, null, key , null, null, seconds);

    }
    public Bicker (String title, String description, String left_side, String right_side, Date create_date, int left_votes,
                   int right_votes, String code, String category, String senderID, String receiverID, String key, ArrayList<String> tags, ArrayList<String> votedUsers, double seconds){

        this.title = title;
        this.description = description;
        this.right_side = right_side;
        this.left_side = left_side;
        this.create_date = create_date;
        this.left_votes = left_votes;
        this.right_votes = right_votes;
        this.code = code;
        this.category = category;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.key = key;
        if (tags != null)
            this.tags = tags;
        if (votedUsers != null)
            this.votedUsers = votedUsers;
        seconds_until_expired = seconds;
    }

    public ArrayList<String> getTags() { return tags; }

    public ArrayList<String> getVotedUsers() { return votedUsers; }

    public void setTags(ArrayList<String> tags) { this.tags = tags; }

    public void setVotedUsers(ArrayList<String> votedUsers) { this.votedUsers = votedUsers; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLeft_side() {
        return left_side;
    }

    public void setLeft_side(String left_side) {
        this.left_side = left_side;
    }

    public String getRight_side() {
        return right_side;
    }

    public void setRight_side(String right_side) {
        this.right_side = right_side;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public int getLeft_votes() {
        return left_votes;
    }

    public void setLeft_votes(int left_votes) {
        this.left_votes = left_votes;
    }

    public int getRight_votes() {
        return right_votes;
    }

    public void setRight_votes(int right_votes) {
        this.right_votes = right_votes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public boolean isVoted() { return voted; }

    public void setVoted(boolean voted) { this.voted = voted; }

    public void setSeconds_until_expired(double seconds) { seconds_until_expired = seconds; }

    public double getSeconds_until_expired() { return seconds_until_expired; }


    public String toString() {
        String res = "";
        res += "Title: " + title;
        res += "\nDescription: " + description;
        res += "\nleft_side: " + left_side;
        res += "\nright_side: " + right_side;
        res += "\ncreate_date: " + create_date;
        res += "\nleft_votes: " + left_votes;
        res += "\nright_votes: " + right_votes;
        res += "\nCode: " + code;
        res += "\nCategory: " + category;
        res += "\nSenderID: " + senderID;
        res += "\nReceiverID: " + receiverID;
        res += "\nSecondsUntilExpired: " + seconds_until_expired;
        return res;
    }


}
