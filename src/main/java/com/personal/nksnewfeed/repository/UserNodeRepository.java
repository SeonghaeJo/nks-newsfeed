package com.personal.nksnewfeed.repository;

import com.personal.nksnewfeed.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
    
    @Query("MATCH (u:User {id: $userId})-[:FOLLOWS]->(f:User) RETURN f.id")
    List<Long> getFollowingIds(@Param("userId") Long userId);
    
    @Query("MATCH (u:User {id: $userId})<-[:FOLLOWS]-(f:User) RETURN f.id")
    List<Long> getFollowerIds(@Param("userId") Long userId);
    
    @Query("MATCH (u:User {id: $userId})-[:BLOCKS]->(b:User) RETURN b.id")
    List<Long> getBlockedIds(@Param("userId") Long userId);
    
    @Query("MATCH (u:User {id: $userId})-[:FOLLOWS]->(f:User)-[:FOLLOWS]->(fof:User) " +
           "WHERE fof.id <> $userId AND NOT (u)-[:FOLLOWS]->(fof) " +
           "RETURN fof.id, count(*) as mutualFriends " +
           "ORDER BY mutualFriends DESC LIMIT $limit")
    List<Long> getFriendRecommendations(@Param("userId") Long userId, @Param("limit") int limit);
    
    @Query("MATCH (u1:User {id: $userId1}), (u2:User {id: $userId2}) " +
           "RETURN EXISTS((u1)-[:FOLLOWS]->(u2))")
    boolean isFollowing(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("MATCH (u1:User {id: $userId1}), (u2:User {id: $userId2}) " +
           "CREATE (u1)-[:FOLLOWS]->(u2)")
    void createFollowRelationship(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("MATCH (u1:User {id: $userId1})-[r:FOLLOWS]->(u2:User {id: $userId2}) DELETE r")
    void deleteFollowRelationship(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("MATCH (u1:User {id: $userId1}), (u2:User {id: $userId2}) " +
           "CREATE (u1)-[:BLOCKS]->(u2)")
    void createBlockRelationship(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}