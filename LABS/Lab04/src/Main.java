//import bg.sofia.uni.fmi.mjt.socialnetwork.SocialNetworkImpl;
//import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
//import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
//import bg.sofia.uni.fmi.mjt.socialnetwork.profile.DefaultUserProfile;
//import bg.sofia.uni.fmi.mjt.socialnetwork.post.ReactionType;
//import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
//import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
//import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;
//
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.Set;
//
//public class Main {
//    public static void main(String[] args) throws UserRegistrationException {
//        UserProfile user1 = new DefaultUserProfile("Alice");
//        UserProfile user2 = new DefaultUserProfile("Bob");
//        UserProfile user3 = new DefaultUserProfile("Charlie");
//
//        user1.addInterest(Interest.MUSIC);
//        user2.addInterest(Interest.BOOKS);
//        user3.addInterest(Interest.GAMES);
//
//        SocialNetworkImpl sn = new SocialNetworkImpl();
//        sn.registerUser(user1);
//        sn.registerUser(user2);
//        sn.registerUser(user3);
//
//        user1.addFriend(user3);
//        user2.addFriend(user3);
//        Set<UserProfile> mutualFriends = sn.getMutualFriends(user1, user2);
//        for (Iterator<UserProfile> iterator = mutualFriends.iterator(); iterator.hasNext(); ) {
//            UserProfile mutualFriend = iterator.next();
//            System.out.println(mutualFriend.getUsername());
//        }
//        System.out.println(user1.isFriend(user3));
//        System.out.println(user1.isFriend(user3));
//
//        Set<UserProfile> sortedfriends = sn.getAllProfilesSortedByFriendsCount();
//        for (Iterator<UserProfile> iterator = sortedfriends.iterator(); iterator.hasNext(); ) {
//            UserProfile user = iterator.next();
//            System.out.println(user.getUsername());
//        }
//
//        Post post1 = sn.post(user1, "hello");
//        Post post2 = sn.post(user2, "hello too");
//        Post post3 = sn.post(user3, "hello too too");
//
//        Collection<Post> posts = sn.getPosts();
//        for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext(); ) {
//            Post post = iterator.next();
//            System.out.println(post.getAuthor().getUsername() + ": " + post.getContent());
//        }
//    }
//}

import bg.sofia.uni.fmi.mjt.socialnetwork.SocialNetworkImpl;
import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.DefaultUserProfile;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws UserRegistrationException {
        SocialNetworkImpl socialNetwork = new SocialNetworkImpl();

        UserProfile user1 = new DefaultUserProfile("user1");
        UserProfile user2 = new DefaultUserProfile("user2");
        UserProfile user3 = new DefaultUserProfile("user3");
        UserProfile user4 = new DefaultUserProfile("user4");
        UserProfile user5 = new DefaultUserProfile("user5");
        UserProfile user6 = new DefaultUserProfile("user6");
        UserProfile user7 = new DefaultUserProfile("user7");
        UserProfile user8 = new DefaultUserProfile("user7");
        UserProfile user9 = new DefaultUserProfile("user8");

        user1.addInterest(Interest.MUSIC);
        user2.addInterest(Interest.MUSIC);
        user3.addInterest(Interest.MUSIC);
        user4.addInterest(Interest.SPORTS);
        user5.addInterest(Interest.MUSIC);
        user6.addInterest(Interest.BOOKS);
        user7.addInterest(Interest.MUSIC);

        socialNetwork.registerUser(user1);
        socialNetwork.registerUser(user2);
        socialNetwork.registerUser(user3);
        socialNetwork.registerUser(user4);
        socialNetwork.registerUser(user5);

        user1.addFriend(user2); // user1 <-> user2
        user1.addFriend(user6); // user1 <-> user6
        user1.addFriend(user7); // user1 <->user7
        user2.addFriend(user3); // user2 <-> user3
        user3.addFriend(user4); // user3 <-> user4
        user4.addFriend(user5); // user4 <-> user5
        user6.addFriend(user7);

        Post post = socialNetwork.post(user1, "Hello, this is user1's post about music!");

        Set<UserProfile> reachedUsers = socialNetwork.getReachedUsers(post);

        System.out.println("Users who can see the post by " + user1.getUsername() + ":");
        for (UserProfile user : reachedUsers) {
            System.out.println(user.getUsername());
        }
    }
}