# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a large-scale newsfeed system built with Spring Boot 3.5.4 and Java 21, implementing a distributed architecture similar to the designs from "Designing Data-Intensive Applications". The system uses multiple databases for different purposes: MySQL for user/post data, Neo4j for social graph relationships, Redis for caching, and RabbitMQ for message queuing.

## Development Commands

### Build and Run
- Build the project: `./gradlew build`
- Run the application: `./gradlew bootRun`
- Clean build: `./gradlew clean build`

### Testing
- Run all tests: `./gradlew test`
- Run tests with info logging: `./gradlew test --info`
- Run a specific test class: `./gradlew test --tests "com.personal.nksnewfeed.*"`

### Database Migrations
- Database schema is managed via Flyway migrations in `src/main/resources/db/migration/`
- Migrations run automatically on application startup

## Architecture

### Multi-Database Strategy
- **MySQL**: User information and posts (`users`, `posts` tables)
- **Neo4j**: Social graph relationships (User nodes with FOLLOWS/BLOCKS relationships)
- **Redis**: Caching layer for posts, user info, and newsfeed data
- **RabbitMQ**: Message queue for post distribution

### Data Consistency Pattern
The system implements **Saga Pattern** for maintaining consistency between MySQL and Neo4j:

1. **Primary Transaction**: User creation in MySQL
2. **Event Publishing**: `UserCreatedEvent` fired after MySQL commit
3. **Secondary Operations**: Neo4j UserNode creation via `@TransactionalEventListener`
4. **Retry Logic**: `@Retryable` with exponential backoff (1s → 2s → 4s, max 3 attempts)
5. **Compensation**: On failure, delete MySQL user record to maintain consistency

Key classes: `UserNodeSyncService`, `UserGraphSyncHandler`, `UserCreatedEvent`

### Package Structure
- `entity/`: JPA entities (User, Post) and Neo4j nodes (UserNode)
- `repository/`: Data access interfaces for MySQL (UserRepository, PostRepository) and Neo4j (UserNodeRepository)
- `service/`: Business logic interfaces (PostService, UserService, FriendService, AuthService, PostTransmitService)
- `event/`: Event-driven architecture components for data synchronization
- `config/`: Spring configuration classes (JpaConfig for auditing)

### Service Layer Design
- **PostService**: CRUD operations for posts, newsfeed generation
- **FriendService**: Social graph operations using Neo4j (follow/unfollow, friend recommendations)
- **PostTransmitService**: Message queue integration for post distribution
- **UserService**: User management with automatic Neo4j synchronization
- **AuthService**: JWT-based authentication

### Key Implementation Notes
- Uses Lombok for reducing boilerplate code
- JPA Auditing enabled for automatic timestamp management (`@CreatedDate`, `@LastModifiedDate`)
- Spring Data Neo4j with custom Cypher queries for complex graph operations
- Event-driven architecture with `@TransactionalEventListener` for cross-database operations
- Compensation-based error handling for distributed transaction scenarios

### Technology Stack
- **Framework**: Spring Boot 3.5.4 with Spring Data JPA and Spring Data Neo4j
- **Java Version**: 21 (configured via Gradle toolchain)
- **Build Tool**: Gradle with wrapper
- **Databases**: MySQL 8.0, Neo4j, Redis
- **Message Queue**: RabbitMQ with Spring AMQP
- **Testing**: JUnit 5 with Spring Boot Test

## Development Notes

- Always use Gradle wrapper (`./gradlew`) instead of system Gradle
- Database migrations are versioned in `src/main/resources/db/migration/`
- When adding new user-related features, consider the MySQL-Neo4j synchronization pattern
- Neo4j queries are optimized for social graph operations (friend recommendations, follower lists)
- The system is designed for eventual consistency rather than strict ACID transactions across databases

## Coding Style
- 수정하지 않는 파라미터나 필드에는 final 을 붙여라
