package purdue.edu.bicker_quicker;

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

    public Bicker (){

    }
    public Bicker (String title, String description){
        this.title = title;
        this.description = description;
    }

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
}
