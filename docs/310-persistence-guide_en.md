# Java Spring Persistence Layer Guide
> In Spring-based applications, the Persistence Layer is responsible for interacting with databases and plays a crucial role in separating business logic from data.  
> Proper persistence layer design forms the foundation for improving maintainability and scalability.  
> This guide covers the basic principles and philosophy of designing persistence layers in a Java Spring environment.

---

## Table of Contents

1. [Basic Principles and Design Philosophy](#basic-principles-and-design-philosophy)
   - [1.1 Basic Role of the Persistence Layer](#11-basic-role-of-the-persistence-layer)
   - [1.2 Repository Pattern and Domain Object Separation](#12-repository-pattern-and-domain-object-separation)
   - [1.3 Role Separation between Entity and Model](#13-role-separation-between-entity-and-model)
   - [1.4 Core Design Principles of the Persistence Layer](#14-core-design-principles-of-the-persistence-layer)
2. [Model Design Rules](#model-design-rules)
   - [2.1 Role of the Model](#21-role-of-the-model)
   - [2.2 Design Examples](#22-design-examples)
3. [Entity Design Rules](#entity-design-rules)
   - [3.1 Role of the Entity](#31-role-of-the-entity)
   - [3.2 Design Examples](#32-design-examples)
4. [Repository Design Rules](#repository-design-rules)
   - [4.1 Philosophy and Role of Repository](#41-philosophy-and-role-of-repository)
   - [4.2 Repository Design Principles](#42-repository-design-principles)
   - [4.3 Design Examples](#43-design-examples)
      - [Top-Level Repository Interface](#top-level-repository-interface-technology-independent-design)
      - [JPA-Based Repository](#jpa-based-repository-interface-using-spring-data-jpa)
      - [JPA Custom Implementation](#jpa-based-custom-implementation-using-querydsl-and-technology-implementation)
   - [4.4 MyBatis-Based Repository Implementation](#44-mybatis-based-repository-implementation-example)
   - [4.5 Implementation in Service Layer](#45-implementation-in-service-layer)
   - [4.6 Implementation Selection](#46-implementation-selection)
   - [4.7 Advantages of This Design](#47-advantages-of-this-design)
5. [Reference Notes (JPA Usage Guide)](#reference-notes-jpa-usage-guide)
   - [5.1 QueryDSL Usage: Building Conditions with BooleanBuilder](#51-querydsl-usage-building-conditions-with-booleanbuilder)
   - [5.2 Pagination Processing](#52-pagination-processing)
   - [5.3 Performance Optimization](#53-performance-optimization)

---

## 1. Basic Principles and Design Philosophy

### **1.1 Basic Role of the Persistence Layer**
The persistence layer performs CRUD operations such as **saving, retrieving, modifying, and deleting** data,
handling detailed aspects of data access separately from business logic implementation.  
This ensures that domain logic remains independent of persistence implementation technologies (JPA, MyBatis, etc.).

### **1.2 Repository Pattern and Domain Object Separation**
Repository serves as a **storage for domain objects (Models)** in the application. This pattern enhances code cohesion through data access abstraction and is designed to isolate code changes related to persistence technology changes.

#### Basic Principles of Repository Design:
- Define a **Repository interface** at the top level for use in domain logic.
- Repository implementations are subdivided according to specific persistence technologies (JPA, MyBatis, etc.), and domain models need not be aware of these specific implementation details.
- Each repository implementation is responsible for persisting and depersisting domain objects.

### **1.3 Role Separation between Entity and Model**
  
In enterprise applications, domain objects (Models) and persistence entities (Entity) such as JPA must have clearly separated roles.
This design ensures that changes in the persistence layer do not affect business logic.

#### **Role of Entity**
- Responsible for **persistence management** and handles mapping between objects and databases.
- Represents a database row and serves as the basic structure for ORM (Object-Relational Mapping).
- Designed solely for data storage and retrieval, **excluding business logic**.

#### **Role of Model (Domain Model)**
- Performs business logic and encapsulates data state and behavior.
- Models are implemented without depending on specific persistence technologies (JPA, MyBatis, etc.).
- Used as central objects containing domain logic in Domain-Driven Design (DDD).

#### **Conversion between Entity and Model**
Since Entity and Model are used differently, it's important to design conversion methods between these objects to clearly define collaboration between objects with different responsibilities:
- Define `toModel()` and `from(Entity)` methods within Entity to handle conversions.
- This maintains clear separation between database and business logic.

### **1.4 Core Design Principles of the Persistence Layer**

#### **1) Technology Independence**
Domain models must be designed to be independent of specific persistence frameworks (JPA, MyBatis, etc.). This minimizes the impact scope when changing specific technology implementations.

#### **2) Repository Pattern Utilization**
- Abstraction through the Repository layer is necessary to prevent business logic from depending on data access layer details.
- Define a **top-level Repository interface** and separate it into specific implementations (JPA, MyBatis, etc.) for maintainability.

#### **3) SRP (Single Responsibility Principle) Compliance**
- Entity is responsible only for database interaction, while business logic is handled by domain models (Model).
- Design becomes clear, and separation of responsibilities between objects is maintained.

#### **4) Testability Assurance**
Through technology-independent design, business logic or data access layers can be tested separately when needed. This ensures that changes in persistence layer implementation do not affect core logic.

---

## 2. Model Design Rules

### 2.1 Role of the Model
- Models implement **enterprise business rules** based on Domain-Driven Design (DDD).
- Models are implemented as **pure objects (POJO)** without depending on specific frameworks or technologies.
- Model state is designed with immutability as a basic principle (providing state change methods when needed).

### 2.2 Design Examples
```java
@Getter
@Builder
@AllArgsConstructor
public class User {

    private final Long id;
    private final String username;
    private final String email;
    private UserRole role;

    // Static factory method (flexible creation)
    public static User create(String username, String email, UserRole role) {
        // Business rules can be applied (e.g., email format validation)
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        return new User(null, username, email, role);
    }

    // State change method (e.g., role change)
    public void changeRole(UserRole newRole) {
        if (newRole == UserRole.ADMIN && !this.role.equals(UserRole.ADMIN)) {
            // Apply specific business logic
            checkAdminPermission();
        }
        this.role = newRole;
    }

    // State check method (e.g., business validation)
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    private void checkAdminPermission() {
        // Handle business rules like admin approval process
    }
}
```

## 3. Entity Design Rules

### 3.1 Role of the Entity
- Entity performs only the role of persisting domain model state to the database.
- Entity does not contain business logic or complex decision logic.
- Provides conversion methods (`toModel` and `fromModel`) with domain models.

### 3.2 Entity Design Example

```java
import jakarta.persistence.*;

@Getter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private Long id;

   private String email;

   private String password;

   private String username;

   @Enumerated(EnumType.STRING)
   private UserRole role;

   @Enumerated(EnumType.STRING)
   private UserStatus status;
    
   // Convert to entity
   public static UserEntity from(User user) {
      UserEntity userEntity = new UserEntity();
      userEntity.id = user.getId();
      userEntity.email = user.getEmail();
      userEntity.password = user.getPassword();
      userEntity.username = user.getUsername();
      userEntity.role = user.getRole();
      userEntity.status = user.getStatus();
      return userEntity;
   }

   // Convert to model
   public User toModel() {
      return User.builder()
              .id(id)
              .email(email)
              .password(password)
              .username(username)
              .role(role)
              .status(status)
              .createdDate(super.getCreatedDate())
              .lastModifiedDate(super.getLastModifiedDate())
              .build();
   }
}
```

---

## 4. Repository Design Rules

### 4.1 Philosophy and Role of Repository
- Repository serves as a storage for domain objects (Model) and is designed to prevent domain from depending on persistence technologies (JPA, MyBatis, etc.).
- Define a **Repository interface** at the top level for use in domain logic, and separate it into implementations using various technologies (JPA, MyBatis, etc.).
- Each implementation implements `XXXRepositoryPort (top-level interface)` and handles specific persistence internally using actual technology (JPA, etc.).

---

### 4.2 Repository Design Principles

1. **Top-Level Repository Interface Separation**
    - Declare a technology-independent, domain-centered `RepositoryPort` interface at the top level.
    - Business logic uses this interface, making it free from technology changes (JPA → MyBatis, etc.).

2. **Technology-Based Repository Implementation Separation**
    - Define actual implementations as `CustomImpl` according to technology (JPA, MyBatis, etc.).
    - When using JPA, use the `XXXRepositoryJpaCustomImpl` naming convention.

3. **Writing Method**
    - `XXXRepositoryPort interface`: Used in domain layer (pure business logic).
    - `XXXRepositoryJpa`: Provides basic Repository functionality of JPA (using Spring Data JPA interface).
    - `XXXRepositoryJpaCustomImpl`: Includes extended features like QueryDSL and JPA custom implementation.

4. **Complete Dependency Separation**
    - Implementation can have dependencies on technology (JPA, MyBatis, etc.) internally, but business logic and service layer remain independent of specific technology.

---

### 4.3 Design Examples

#### Top-Level Repository Interface: Technology-Independent Design
The `RepositoryPort` interface uses domain objects (Model).

```java
public interface UserRepositoryPort {
   
   Optional<User> findById(Long id);
   Optional<User> findByEmail(String email);
   User save(User user);
   List<User> findAll();
   Page<User> findAll(Pageable pageable);
   Page<User> findAdminUsers(Pageable pageable);   
   
}
```

#### JPA-Based Repository Interface: Using Spring Data JPA
```java
public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
```

#### JPA-Based Custom Implementation: Using QueryDSL and Technology Implementation
`UserRepositoryJpa+`Custom`+Impl` is defined to avoid conflicts with proxy objects created by JPA.

```java
import jakarta.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpaCustomImpl implements UserRepositoryPort {

   private final UserRepositoryJpa userRepositoryJpa;
   private final JPAQueryFactory queryFactory;

   @Override
   public Optional<User> findById(Long id) {
      return userRepositoryJpa.findById(id).map(UserEntity::toModel);
   }

   @Override
   public Optional<User> findByEmail(String email) {
      return userRepositoryJpa.findByEmail(email)
              .map(UserEntity::toModel);
   }

   @Override
   public User save(User user) {
      return userRepositoryJpa.save(UserEntity.from(user)).toModel();
   }

   @Override
   public List<User> findAll() {
      return userRepositoryJpa.findAll()
              .stream()
              .map(UserEntity::toModel)
              .toList();
   }

   @Override
   public Page<User> findAll(Pageable pageable) {
      return userRepositoryJpa.findAll(pageable)
              .map(UserEntity::toModel);
   }

   @Override
   public Page<User> findAdminUsers(Pageable pageable) {
      QUserEntity user = QUserEntity.userEntity;

      // 1. Write basic QueryDSL query
      JPAQuery<UserEntity> query = queryFactory
              .selectFrom(user)
              .where(user.role.eq(UserRole.ADMIN)); // Condition where role is ADMIN

      // 2. Pagination processing
      long total = query.stream().count(); // Get total data count
      List<User> users = query
              .offset(pageable.getOffset()) // Starting position
              .limit(pageable.getPageSize()) // Data count per page
              .fetch()
              .stream()
              .map(UserEntity::toModel)
              .toList();

      log.info("[Repository] users.size : {}", users.size());
      // 3. Convert to Page and return
      return new PageImpl<>(users, pageable, total);
   }

   @Override
   public User updateStatus(User user) {
      return userRepositoryJpa.save(UserEntity.from(user)).toModel();
   }
}
```

---

### 4.4 MyBatis-Based Repository Implementation (Example)
Even when adding MyBatis-based implementation, the service layer remains unaffected by technology changes.  
Created and used `UserDto` for **MyBatis**.

```java
@Mapper
public interface UserRepositoryMybatis {

   Optional<UserDto> findById(Long id);
   Optional<UserDto> findByEmail(String email);
   Long save(UserDto user);
   List<UserDto> findAll();
   Page<UserDto> findAll(Pageable pageable);
   List<UserDto> findAllWithPageable(@Param("offset") long offset, @Param("pageSize") int pageSize);
   long countAll();
}
```

```java
@Repository
@RequiredArgsConstructor
public class UserRepositoryMyBatisImpl implements UserRepositoryPort {

   private final UserRepositoryMybatis userRepositoryMybatis;

   @Override
   public Optional<User> findById(Long id) {
      return userRepositoryMybatis.findById(id).map(UserDto::toModel);
   }

   @Override
   public Optional<User> findByEmail(String email) {
      return userRepositoryMybatis.findByEmail(email).map(UserDto::toModel);
   }

   @Override
   public User save(User user) {
      UserDto userDto = UserDto.from(user);
      Long savedCount = userRepositoryMybatis.save(userDto);
      if(savedCount == 0) {
         return null;
      }
      return userRepositoryMybatis.findById(userDto.getId()).map(UserDto::toModel)
              .orElse(null);
   }

   @Override
   public List<User> findAll() {
      return userRepositoryMybatis.findAll()
              .stream()
              .map(UserDto::toModel)
              .toList();
   }

   @Override
   public Page<User> findAll(Pageable pageable) {
      // Call MyBatis query
      List<UserDto> userDtos = userRepositoryMybatis.findAllWithPageable(
              pageable.getOffset(),
              pageable.getPageSize()
      );

      // Logic to get total data count
      long totalCount = userRepositoryMybatis.countAll();

      // Convert to Page<User>
      List<User> users = userDtos.stream()
              .map(UserDto::toModel)
              .toList();

      // Return: Create Page implementation
      return new PageImpl<>(users, pageable, totalCount);
   }

   @Override
   public Page<User> findAdminUsers(Pageable pageable) {
      // TODO - Implementation needed
      return null;
   }

   @Override
   public User updateStatus(User user) {
      // TODO - Implementation needed
      return null;
   }
}
```

---

### 4.5 Implementation in Service Layer
The service layer injects and uses the technology-independent `UserRepositoryPort` interface.  
With the above design, service layer code need not be modified even when data access technology changes.

```java
@Service
@RequiredArgsConstructor
public class UserService implements UserServicePort {

   private final UserRepositoryPort userRepositoryPort;
   private final PasswordEncoder passwordEncoder;
   private final JwtUtil jwtUtil;

   // Sign-up method
   @Override
   @Transactional
   public User signUp(UserCreate userCreate) {
      // Check if user exists with input email
      checkUserExistByEmail(userCreate.getEmail());
      return userRepositoryPort.save(User.from(userCreate, passwordEncoder));
   }

   // Authentication
   @Override
   public String authenticate(String email, String rawPassword) {
      User user = userRepositoryPort.findByEmail(email)
              .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
      boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
      if (!matches) {
         throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
      }

      // Generate JWT Token
      Map<String, Object> claims = new HashMap<>();
      claims.put("uid", user.getEmail());
      claims.put("username", user.getUsername());
      claims.put("email", user.getEmail());
      claims.put("role", user.getRole());

      String token = jwtUtil.generateToken(claims);
      log.info("generated token : {}", token);

      return token;
   }

   // Find all users
   @Override
   public List<User> findAllUsers() {
      return userRepositoryPort.findAll();
   }

   @Override
   public Page<User> findAll(Pageable pageable) {
      return userRepositoryPort.findAll(pageable);
   }

   @Override
   public User getById(Long id) {
      return userRepositoryPort.findById(id)
              .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
   }

   @Override
   public Page<User> findAdminUsers(Pageable pageable) {
      log.info("[Service] findAdminUsers : {}", pageable);
      return userRepositoryPort.findAdminUsers(pageable);
   }

   @Override
   public User updateUserStatus(User user) {
      // Find
      User findUser = userRepositoryPort.findByEmail(user.getEmail())
              .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));

      // Update status
      User updateUser = findUser.updateStatus(user.getStatus());
      return userRepositoryPort.updateStatus(updateUser);
   }
}
```

---

### 4.6 Implementation Selection 

- Select the actual implementation in AppConfig.java file.
- While there's no need to develop JPA and MyBatis technologies simultaneously, you can change technology by developing only the technology implementation and changing the config setting when technology transition is needed.

```java
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepositoryJpa userRepositoryJpa;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepositoryMybatis userRepositoryMybatis;

    /**
     * Select JPA or MyBatis implementation
     */
    @Bean
    public UserRepository userRepository() {
        // Using JPA
        return new UserRepositoryJpaCustomImpl(userRepositoryJpa, jpaQueryFactory);
        
        // Using MyBatis
//        return new UserRepositoryMybatisImpl(userRepositoryMybatis);
    }
}
```

---

### 4.7 Advantages of This Design

1. **Flexibility in Technology Changes**
    - Changes from JPA → MyBatis or other technology stacks do not affect business logic like `UserService`.
    - Only technology-specific implementations need to be replaced.

2. **Repository Basic Principle Compliance**
    - Repository is responsible for data storage and retrieval, excluding business logic.
    - Domain models are designed to be concentrated in Service and Model internally.

3. **Clean Architecture Compliance**
    - `UserRepository` encapsulates technical details far from the center (hexagon).
    - Core domain layer remains unaffected even when technology (implementation) changes.

---

## 5. Reference Notes (JPA Usage Guide)
> Explains JPA processing methods including condition building with `BooleanBuilder`, pagination, and performance optimization based on QueryDSL.

### 5.1 QueryDSL Usage: Building Conditions with BooleanBuilder
`BooleanBuilder` provides a structure to flexibly combine conditions and build dynamic queries based on specific conditions.

#### **Example Condition Builder Object**
`CodeConditionBuilder` uses `BooleanBuilder` object to dynamically generate `BooleanExpression` based on search conditions (`CodeSearchCondition`).

   ```java
   public abstract class CodeConditionBuilder {
   
       public static BooleanBuilder codeCondition(CodeSearchCondition condition) {
           BooleanBuilder builder = new BooleanBuilder();
           if (condition == null) {
               return builder.and(alwaysTrue());
           }
   
           return builder
                   .and(codeEq(condition.getCode()))
                   .and(codeNameLike(condition.getCodeName()))
                   .and(descriptionLike(condition.getDescription()))
                   .and(parentCodeEq(condition.getParentCode()))
                   .and(delYnEq(condition.getDelYn()));
       }
   
       private static BooleanExpression codeEq(String code) {
           return hasText(code) ? codeEntity.code.eq(code) : null;
       }
   
       private static BooleanExpression codeNameLike(String codeName) {
           return hasText(codeName) ? codeEntity.codeName.like("%" + codeName + "%") : null;
       }
   
       private static BooleanExpression descriptionLike(String description) {
           return hasText(description) ? codeEntity.description.like("%" + description + "%") : null;
       }
   
       private static BooleanExpression parentCodeEq(CodeEntity parentCode) {
           return ObjectUtils.isNotEmpty(parentCode) ? codeEntity.parentCode.eq(parentCode) : null;
       }
   
       private static BooleanExpression delYnEq(Boolean delYn) {
           return codeEntity.delYn.eq(Boolean.TRUE.equals(delYn));
       }
   
       private static BooleanExpression alwaysTrue() {
           return codeEntity.isNotNull(); // Condition that is always true
       }
   }
   ```

#### **Using Condition Builder Object in JPA Implementation Example**

   ```java
   private List<Code> getQueryResults(Pageable pageable, CodeSearchCondition condition, boolean withChild) {
   
        if (condition.getParentCodeId() != null ) {
            CodeEntity parentCodeEntity = codeRepositoryJpa.findById(condition.getParentCodeId()).orElse(null);
            condition.setParentCode(parentCodeEntity);
        }

        JPAQuery<CodeEntity> query = queryFactory.selectFrom(codeEntity)
                .where(CodeConditionBuilder.codeCondition(condition))
                .orderBy(codeEntity.seq.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        return query.fetch().stream()
                .map(withChild ? CodeEntity::toModelWithChild : CodeEntity::toModel)
                .collect(Collectors.toList());
   }
   ```

### 5.2 Pagination Processing
QueryDSL can easily configure pagination processing using `Pageable`.

- `.offset(pageable.getOffset())`: Page starting position.
- `.limit(pageable.getPageSize())`: Data size per page.
- `.orderBy(codeEntity.seq.asc())`: Sort based on entity's `seq` attribute.

### 5.3 Performance Optimization

1. Use **fetch join**
```java
    query.leftJoin(codeEntity.child).fetchJoin();
```

2. **Batch Size Setting**  
  Additional setting for performance optimization when fetching data with `Fetch Join`.  
Improves query efficiency when there are many related data.
   - Using yaml
      ```yaml
      spring:
        jpa:
          properties:
            hibernate.default_batch_fetch_size: 100
      ```
   - Define in Entity code on relationship field
      ```java
      @BatchSize(size = 100)
      private List<CodeEntity> child = new ArrayList<>();
      ``` 