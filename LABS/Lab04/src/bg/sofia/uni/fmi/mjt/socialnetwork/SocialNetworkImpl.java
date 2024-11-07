package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfileFriendCountComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class SocialNetworkImpl implements SocialNetwork {

    private final TreeMap<String, UserProfile> users = new TreeMap<>();
    private final LinkedHashMap<UserProfile, LinkedHashSet<Post>> postsByUser = new LinkedHashMap<>();

    @Override
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile cannot be null");
        }
        if (users.containsKey(userProfile.getUsername())) {
            throw new UserRegistrationException("User profile is already registered");
        }
        users.put(userProfile.getUsername(), userProfile);
        postsByUser.put(userProfile, new LinkedHashSet<>());  
    }

    @Override
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(new TreeSet<>(users.values()));
    }

    @Override
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if (userProfile == null || content == null || content.isBlank()) {
            throw new IllegalArgumentException("User profile or content cannot be null/empty");
        }
        if (!users.containsKey(userProfile.getUsername())) {
            throw new UserRegistrationException("User profile is not registered");
        }

        Post newPost = new SocialFeedPost(userProfile, content);
        postsByUser.get(userProfile).add(newPost);  
        return newPost;
    }

    @Override
    public Collection<Post> getPosts() {
        LinkedHashSet<Post> allPosts = new LinkedHashSet<>();
        for (LinkedHashSet<Post> userPosts : postsByUser.values()) {
            allPosts.addAll(userPosts);
        }
        return Collections.unmodifiableSet(allPosts);
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

    private boolean hasCommonInterest(UserProfile author, UserProfile user) {
        for (var interest : user.getInterests()) {
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
        if (!users.containsKey(userProfile1.getUsername()) || !users.containsKey(userProfile2.getUsername())) {
            throw new UserRegistrationException("Both users must be registered");
        }

        Set<UserProfile> mutualFriends = new HashSet<>(userProfile1.getFriends());
        mutualFriends.retainAll(userProfile2.getFriends());
        return mutualFriends;
    }

    @Override
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        SortedSet<UserProfile> sortedProfiles = new TreeSet<>(new UserProfileFriendCountComparator());
        sortedProfiles.addAll(users.values());
        return sortedProfiles;
    }
}