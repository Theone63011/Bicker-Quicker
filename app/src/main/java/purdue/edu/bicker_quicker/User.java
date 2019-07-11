package purdue.edu.bicker_quicker;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<String> bickerId =  new ArrayList<String>();
    private String email;
    private String username;
    private String userId;
    private boolean moderator;
    private String token;
    private List<String> votedBickerIds = new ArrayList<String>();
    private int notificationSettings;

    public void setNotificationSettings(int newSettings) {
        notificationSettings = newSettings;
    }

    public int getNotSettings() {
        return notificationSettings;
    }

    public User() {
        // Init settings for notifications
        notificationSettings = 0b11111100;
    }

    public void setBickerId(String bickerId){this.bickerId.add(bickerId);}

    public List<String> getBickerId(){ return bickerId;}

    public void setEmail(String email){this.email = email;}

    public String getEmail(){ return email;}

    public void setUsername(String username){ this.username = username;}

    public String getUsername(){ return username;}

    public void setUserId(String userId){ this.userId = userId;}

    public String getUserId(){ return userId;}

    public void setModerator(boolean moderator){ this.moderator = moderator;}

    public boolean getModerator(){ return moderator;}

    public void setVotedBickerIds(String votedBickerIds){this.votedBickerIds.add(votedBickerIds);}

    public List<String> getVotedBickerIds(){ return votedBickerIds;}

    public String getToken(){ return token;}

    public void setToken(String token){ this.token = token;}

}
