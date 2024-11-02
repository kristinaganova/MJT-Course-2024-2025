package bg.sofia.uni.fmi.mjt.socialnetwork.post;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SocialFeedPost implements Post {
    private final String uniqueId;
    private final UserProfile author;
    private final LocalDateTime publishedOn;
    private final String content;
    private final Map<ReactionType, Set<UserProfile>> reactions;

    public SocialFeedPost(UserProfile author, String content) {
        if (author == null || content == null || author.getUsername() == null || content.isBlank()) {
            throw new IllegalArgumentException("Author or content cannot be null/blank");
        }

        this.uniqueId = PostIdGenerator.generateId();
        this.author = author;
        this.publishedOn = LocalDateTime.now();
        this.content = content;
        this.reactions = new HashMap<>();
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public UserProfile getAuthor() {
        return author;
    }

    @Override
    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean addReaction(UserProfile userProfile, ReactionType reactionType) {
        if (userProfile == null || reactionType == null) {
            throw new IllegalArgumentException("UserProfile or ReactionType cannot be null");
        }

        if (hasSameReaction(userProfile, reactionType)) {
            return false;
        }

        boolean isUpdate = removeReaction(userProfile);

        if (!reactions.containsKey(reactionType)) {
            reactions.put(reactionType, new HashSet<UserProfile>());
        }
        reactions.get(reactionType).add(userProfile);

        return !isUpdate;
    }

    /**
     * Helper method checks if user reacted with the same reaction
     */
    private boolean hasSameReaction(UserProfile userProfile, ReactionType reactionType) {
        return reactions.get(reactionType) != null && reactions.get(reactionType).contains(userProfile);
    }

    @Override
    public boolean removeReaction(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null");
        }

        boolean reactionRemoved = false;
        Iterator<Map.Entry<ReactionType, Set<UserProfile>>> iterator = reactions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ReactionType, Set<UserProfile>> entry = iterator.next();
            Set<UserProfile> reactionSet = entry.getValue();

            if (reactionSet != null && reactionSet.remove(userProfile)) {
                reactionRemoved = true;
                if (reactionSet.isEmpty()) {
                    iterator.remove();
                }
            }
        }

        return reactionRemoved;
    }

    @Override
    public Map<ReactionType, Set<UserProfile>> getAllReactions() {
        return Collections.unmodifiableMap(reactions);
    }

    @Override
    public int getReactionCount(ReactionType reactionType) {
        if (reactionType == null) {
            throw new IllegalArgumentException("ReactionType cannot be null");
        }

        Set<UserProfile> reactionSet = reactions.get(reactionType);
        return (reactionSet == null) ? 0 : reactionSet.size();
    }

    @Override
    public int totalReactionsCount() {
        return reactions.values().stream().mapToInt(Set::size).sum();
    }
}