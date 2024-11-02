package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfileFriendCountComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocialNetworkImpl implements SocialNetwork {
    private final Set<UserProfile> users = new HashSet<>();
    private final Set<Post> posts = new HashSet<>();

    @Override
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile cannot be null");
        }
        if (users.contains(userProfile)) {
            throw new UserRegistrationException("User profile is already registered");
        }
        users.add(userProfile);
    }

    @Override
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if (userProfile == null || content == null || content.isBlank()) {
            throw new IllegalArgumentException("User profile or content cannot be null/empty");
        }
        if (!users.contains(userProfile)) {
            throw new UserRegistrationException("User profile is not registered");
        }
        Post newPost = new SocialFeedPost(userProfile, content);
        posts.add(newPost);
        return newPost;
    }

    @Override
    public Collection<Post> getPosts() {
        return Collections.unmodifiableSet(posts);
    }

    @Override
    public Set<UserProfile> getReachedUsers(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }

        Set<UserProfile> reachedUsers = new HashSet<>();
        UserProfile author = post.getAuthor();

        traverseFriends(author, reachedUsers);

        Set<UserProfile> finalReachedUsers = new HashSet<>();
        for (UserProfile user : reachedUsers) {
            if (hasCommonInterest(author, user)) {
                finalReachedUsers.add(user);
            }
        }

        return finalReachedUsers;
    }

    /**
     * Helper method to traverse the network of friends up to friends of friends.
     */
    private void traverseFriends(UserProfile user, Set<UserProfile> reachedUsers) {
        Queue<UserProfile> toVisit = new LinkedList<>();
        Set<UserProfile> visited = new HashSet<>();

        toVisit.add(user);
        visited.add(user);

        while (!toVisit.isEmpty()) {
            UserProfile current = toVisit.poll();

            for (UserProfile friend : current.getFriends()) {
                if (!visited.contains(friend)) {
                    visited.add(friend);
                    toVisit.add(friend);
                    reachedUsers.add(friend);
                }
            }
        }
    }

    /**
     * Helper method to check if two users have at least one common interest.
     */
    private boolean hasCommonInterest(UserProfile author, UserProfile user) {
        for (Interest interest : user.getInterests()) {
            if (author.getInterests().contains(interest)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<UserProfile> getMutualFriends(UserProfile userProfile1, UserProfile userProfile2)
            throws UserRegistrationException {
        if (userProfile1 == null || userProfile2 == null) {
            throw new IllegalArgumentException("User profiles cannot be null");
        }
        if (!users.contains(userProfile1) || !users.contains(userProfile2)) {
            throw new UserRegistrationException("Both users must be registered");
        }

        Set<UserProfile> mutualFriends = new HashSet<>(userProfile1.getFriends());
        mutualFriends.retainAll(userProfile2.getFriends());
        return mutualFriends;
    }

    @Override
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        SortedSet<UserProfile> sortedProfiles = new TreeSet<>(new UserProfileFriendCountComparator());
        sortedProfiles.addAll(users);
        return sortedProfiles;
    }
}

