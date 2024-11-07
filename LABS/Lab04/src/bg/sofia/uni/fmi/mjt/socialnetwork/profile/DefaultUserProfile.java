package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultUserProfile implements UserProfile, Comparable<DefaultUserProfile> {
    private final String username;
    private final Set<Interest> interests = new HashSet<>();
    private final Set<UserProfile> friends = new HashSet<>();

    public DefaultUserProfile(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<Interest> getInterests() {
        return Collections.unmodifiableSet(interests);
    }

    @Override
    public boolean addInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("Interest cannot be null");
        }
        return interests.add(interest);
    }

    @Override
    public boolean removeInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("Interest cannot be null");
        }
        return interests.remove(interest);
    }

    @Override
    public Collection<UserProfile> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    @Override
    public boolean addFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null");
        }
        if (this.equals(userProfile)) {
            throw new IllegalArgumentException("Cannot add oneself as a friend");
        }

        if (friends.contains(userProfile)) {
            return false;
        }

        friends.add(userProfile);
        if (!userProfile.isFriend(this)) {
            userProfile.addFriend(this);
        }
        return true;
    }

    @Override
    public boolean unfriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null");
        }

        Iterator<UserProfile> iterator = friends.iterator();
        while (iterator.hasNext()) {
            UserProfile friend = iterator.next();
            if (friend.equals(userProfile)) {
                iterator.remove();
                if (userProfile.isFriend(this)) {
                    userProfile.unfriend(this);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null");
        }
        return friends.contains(userProfile);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DefaultUserProfile that = (DefaultUserProfile) obj;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public int compareTo(DefaultUserProfile other) {
        return this.username.compareTo(other.username);
    }
}