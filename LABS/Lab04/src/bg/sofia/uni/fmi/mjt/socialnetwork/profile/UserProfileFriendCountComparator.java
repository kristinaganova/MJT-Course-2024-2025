package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.Comparator;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

public class UserProfileFriendCountComparator implements Comparator<UserProfile> {
    @Override
    public int compare(UserProfile u1, UserProfile u2) {
        int friendsCountComparison = Integer.compare(u2.getFriends().size(), u1.getFriends().size());

        if (friendsCountComparison != 0) {
            return friendsCountComparison;
        }

        return u1.getUsername().compareTo(u2.getUsername());
    }
}