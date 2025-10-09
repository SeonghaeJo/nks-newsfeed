package com.personal.nksnewfeed.repository;

import com.personal.nksnewfeed.node.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {

    Optional<UserNode> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}