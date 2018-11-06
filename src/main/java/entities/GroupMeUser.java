package entities;

public class GroupMeUser {
    private final int userId;
    private final String userNickname;

    public GroupMeUser(int userId, String userNickname) {
        this.userId = userId;
        this.userNickname = userNickname;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public int nickNameLength() {
        return userNickname.length();
    }

    @Override
    public String toString() {
        return "GroupMeUser{" +
                "userId=" + userId +
                ", userNickname='" + userNickname + '\'' +
                '}';
    }
}
