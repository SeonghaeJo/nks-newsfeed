package com.personal.nksnewfeed.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("User")
@Getter
@NoArgsConstructor
public class UserNode {
    
    @Id
    private Long id;
    
    private String username;
    
    @Relationship(type = "FOLLOWS", direction = OUTGOING)
    private Set<UserNode> following = new HashSet<>();
    
    @Relationship(type = "BLOCKS", direction = OUTGOING)
    private Set<UserNode> blocked = new HashSet<>();
    
    public UserNode(final Long id, final String username) {
        this.id = id;
        this.username = username;
    }
    
    public void follow(final UserNode user) {
        this.following.add(user);
    }
    
    public void unfollow(final UserNode user) {
        this.following.remove(user);
    }
    
    public void block(final UserNode user) {
        this.blocked.add(user);
        this.following.remove(user); // 차단 시 팔로우 해제
    }
    
    public void unblock(final UserNode user) {
        this.blocked.remove(user);
    }
    
    public boolean isFollowing(final UserNode user) {
        return this.following.contains(user);
    }
    
    public boolean isBlocked(final UserNode user) {
        return this.blocked.contains(user);
    }
}
