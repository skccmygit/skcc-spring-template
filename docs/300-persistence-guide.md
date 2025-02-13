# Java Spring 영속성 계층 가이드
> Spring 기반 애플리케이션에서 영속성 계층(Persistence Layer)은 데이터베이스와의 상호작용을 담당하며,비즈니스 로직과 데이터를 분리하는 중요한 역할을 합니다.  
> 올바른 영속성 계층 설계는 유지보수성과 확장성을 높이는 기반이 됩니다.   
> 이 가이드에서는 Java Spring 환경에서 영속성 계층을 설계하는 기본적인 원칙과 철학을 다룹니다.

---
## 목차

1. [기본 원칙 및 설계 철학](#기본-원칙-및-설계-철학)
   - [1.1 영속성 계층의 기본 역할](#11-영속성-계층의-기본-역할)
   - [1.2 Repository 패턴과 도메인 객체 분리](#12-repository-패턴과-도메인-객체-분리)
   - [1.3 Entity와 Model의 역할 분리](#13-entity와-model의-역할-분리)
   - [1.4 영속성 계층의 핵심 설계 원칙](#14-영속성-계층의-핵심-설계-원칙)
2. [Model 설계 규칙](#model-설계-규칙)
   - [2.1 Model의 역할](#21-model의-역할)
   - [2.2 설계 예시](#22-설계-예시)
3. [Entity 설계 규칙](#entity-설계-규칙)
   - [3.1 Entity의 역할](#31-entity의-역할)
   - [3.2 설계 예시](#32-설계-예시)
4. [Repository 설계 규칙](#repository-설계-규칙)
   - [4.1 Repository의 철학과 역할](#41-repository의-철학과-역할)
   - [4.2 Repository 설계 원칙](#42-repository-설계-원칙)
   - [4.3 설계 예시](#43-설계-예시)
      - [최상위 Repository 인터페이스](#최상위-repository-인터페이스-기술-비의존적인-설계)
      - [JPA 기반 Repository](#jpa-기반-repository-인터페이스-spring-data-jpa-사용)
      - [JPA 커스텀 구현체](#jpa-기반-custom-구현체-querydsl-활용-및-기술-구현)
   - [4.4 MyBatis 기반 Repository 구현](#44-mybatis-기반-repository-구현-예시)
   - [4.5 서비스 계층에서 구현](#45-서비스-계층에서-구현)
   - [4.6 구현체 선택](#46-구현체-선택)
   - [4.7 해당 설계의 장점](#47-해당-설계의-장점)
5. [참고사항 (JPA 사용 가이드)](#참고사항-jpa-사용-가이드)
   - [5.1 QueryDSL 사용: BooleanBuilder로 조건 빌드](#51-querydsl-사용-booleanbuilder로-조건-빌드)
   - [5.2 페이징 처리](#52-페이징-처리)
   - [5.3 성능 최적화](#53-성능-최적화)

---

## 1. 기본 원칙 및 설계 철학

### **1.1 영속성 계층의 기본 역할**
영속성 계층은 데이터의 **저장, 조회, 수정, 삭제** 등의 CRUD 작업을 수행하며,
비즈니스 로직의 구현과 별개로 데이터 접근에 관한 세부 사항을 처리합니다.  
이를 통해 도메인 로직이 영속성 구현 기술(JPA, MyBatis 등)에 종속되지 않도록 합니다.

### **1.2 Repository 패턴과 도메인 객체 분리**
Repository는 애플리케이션의 **도메인 객체(Model)의 저장소** 역할을 수행합니다. 이 패턴은 데이터 접근의 추상화를 통해 코드의 응집도를 높이고, 영속성 기술 변경에 따른 코드를 격리할 수 있도록 설계됩니다.
#### Repository 설계의 기본 원칙:
- 최상단에 도메인 로직에서 사용할 **Repository 인터페이스**를 정의합니다.
- Repository 구현체는 JPA, MyBatis 등 구체적인 영속성 기술에 따라 세분화되며, 도메인 모델은 이러한 구체적인 구현 사항에 대해 몰라도 됩니다.
- 각 Repository 구현체는 도메인 객체를 영속화 및 비영속화를 책임지는 역할을 합니다.

### **1.3 Entity와 Model의 역할 분리**
  
엔터프라이즈 애플리케이션에서 도메인 객체(Model)와 JPA와 같은 영속성 엔터티(Entity)는 각자의 역할을 명확히 분리해야 합니다.
이로써 영속성 계층의 변경이 비즈니스 로직에 영향을 미치지 않도록 설계합니다.

#### **Entity의 역할**
- **영속성 관리**를 책임지며, 객체와 데이터베이스 간의 매핑을 처리합니다.
- 데이터베이스의 한 행(Row)을 표현하며, ORM(객체 관계 매핑)의 기본 구조로 활용됩니다.
- **비즈니스 로직을 포함하지 않고**, 데이터의 저장과 조회만을 목적으로 설계됩니다.

#### **Model(도메인 모델)의 역할**
- 비즈니스 로직을 수행하며, 데이터의 상태와 행동을 캡슐화합니다.
- Model은 특정 영속성 기술(JPA, MyBatis 등)에 의존하지 않고 구현됩니다.
- 도메인 중심 설계(DDD)에서 도메인 로직을 담은 중심 객체로 활용됩니다.

#### **Entity와 Model 간의 변환**
Entity와 Model은 사용하는 방식이 다르기 때문에, 두 객체 간의 변환 메서드를 설계하는 것이 중요합니다. 이를 통해 서로 다른 책임을 가진 객체 간 협업을 명확히 할 수 있습니다:
- Entity 내부에 `toModel()`, `from(Entity)` 메서드를 정의하여 변환을 처리합니다.
- 이는 데이터베이스와 비즈니스 로직 사이의 분리를 명확히 유지합니다.

### **1.4 영속성 계층의 핵심 설계 원칙**
#### **1) 기술 종속성 제거**
도메인 모델은 특정 영속성 프레임워크(JPA, MyBatis 등)에 의존하지 않도록 설계해야 합니다. 이를 통해 특정 기술 구현 변경 시의 영향 범위를 최소화할 수 있습니다.
#### **2) Repository 패턴 활용**
- 비즈니스 로직이 데이터 접근 레이어의 디테일에 의존하지 않도록, Repository 계층을 통한 추상화가 필요합니다.
- **최상위 Repository 인터페이스**를 정의하고, 이를 구체적인 구현체(JPA, MyBatis 등)로 분리하여 유지보수가 용이하도록 만듭니다.

#### **3) SRP(단일 책임 원칙) 준수**
- Entity는 데이터베이스와의 상호작용에만 책임을 가지며, 비즈니스 로직은 도메인 모델(Model)이 담당합니다.
- 설계가 명확해지며, 객체 간의 책임 분리가 유지됩니다.

#### **4) 테스트 가능성 확보**
기술 독립적인 설계를 통해, 비즈니스 로직이나 데이터 접근 계층을 필요한 경우 각각 테스트할 수 있습니다. 이를 통해 영속성 계층의 구현이 변경되더라도 핵심 로직에 영향을 주지 않도록 보장합니다.

---

## 2. Model 설계 규칙

### 2.1 Model의 역할
- Model은 도메인 중심 설계(DDD)에 기반하여 **엔터프라이즈의 비즈니스 규칙**을 구현합니다.
- Model은 **순수 객체(POJO)**로 구현되며, 특정 프레임워크나 기술에 의존하지 않습니다.
- Model의 상태는 불변(Immutable)을 기본 원칙으로 설계합니다(필요 시 상태 변경 메서드 제공).

### 2.2 설계 예시
```java
@Getter
@Builder
@AllArgsConstructor
public class User {

    private final Long id;
    private final String username;
    private final String email;
    private UserRole role;

    // 정적 팩토리 메서드 (유연한 생성)
    public static User create(String username, String email, UserRole role) {
        // 비즈니스 규칙 적용 가능 (예: 이메일 형식 검사)
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        return new User(null, username, email, role);
    }

    // 상태 변경 메서드 (예: 역할 변경)
    public void changeRole(UserRole newRole) {
        if (newRole == UserRole.ADMIN && !this.role.equals(UserRole.ADMIN)) {
            // 특정 비즈니스 로직 적용
            checkAdminPermission();
        }
        this.role = newRole;
    }

    // 상태 확인 메서드 (예: 비즈니스 검증)
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    private void checkAdminPermission() {
        // 관리자 승인 절차 등 비즈니스 규칙 처리 가능
    }

}
```

## 3. Entity 설계 규칙

### 3.1 Entity의 역할
- Entity는 도메인 모델의 상태를 DB에 영속화하는 역할만 수행합니다.
- Entity는 비즈니스 로직이나 복잡한 판단 로직을 가지지 않습니다.
- 도메인 모델과의 변환 메서드(`toModel` 및 `fromModel`)를 제공합니다.

### 3.2 Entity 설계 예시

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
    
   // 엔티티로 변환
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

   // 모델로 변환
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

## 4. Repository 설계 규칙
### 4.1 Repository의 철학과 역할
- Repository는 도메인 객체(Model)의 저장소 역할을 담당하며, 도메인이 영속성 기술(JPA, MyBatis 등)에 종속되지 않도록 설계합니다.
- 최상단에 도메인 로직에서 사용할 **Repository 인터페이스**를 정의하고, 이를 다양한 기술(JPA, MyBatis 등)을 사용하는 구현체로 구분합니다.
- 각 구현체는 `XXXRepositoryPort(최상위 인터페이스)`를 구현하고, 실제 사용 기술(JPA 등)을 내부적으로 사용하여 구체적인 영속성을 처리합니다.

---
### 4.2 Repository 설계 원칙

1. **최상위 Repository 인터페이스 분리**
    - 최상위에 기술에 종속되지 않은 도메인 중심의 `RepositoryPort` 인터페이스를 선언합니다.
    - 비즈니스 로직에서는 이 인터페이스를 활용하므로, 기술 변경(JPA → MyBatis 등)에 자유롭습니다.

2. **기술 기반 Repository 구현 분리**
    - 기술(JPA, MyBatis 등)에 따라 실제 구현체를 `CustomImpl` 형태로 정의합니다.
    - JPA를 사용한다면, `XXXRepositoryJpaCustomImpl` 명명 규칙을 사용하여 구성합니다.

3. **작성 방식**
    - `XXXRepositoryPort 인터페이스`: 도메인 레이어(순수 비즈니스 로직)에서 활용.
    - `XXXRepositoryJpa`: JPA의 기본적인 Repository 기능 제공 (Spring Data JPA 인터페이스 활용).
    - `XXXRepositoryJpaCustomImpl`: QueryDSL과 같은 확장 기능 포함 및 JPA 커스텀 구현체.

4. **완전한 의존성 분리**
    - 구현체 내부에서는 기술(JPA, MyBatis 등)에 의존성을 가질 수 있으나, 비즈니스 로직 및 서비스 계층에서는 특정 기술에 의존하지 않습니다.

---
### 4.3 설계 예시

#### 최상위 Repository 인터페이스: 기술 비의존적인 설계
`RepostioryPort` 인터페이싀 경우 도메인 객체(Model)를 사용합니다.

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

#### JPA 기반 Repository 인터페이스: Spring Data JPA 사용
```java
public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
```

#### JPA 기반 Custom 구현체: QueryDSL 활용 및 기술 구현
UserRepositoryJpa+`Custom`+Impl 의 경우 Jpa가 프록시 객체를 만드는 이름과의 충돌을 피하기 위해 정의 하였습니다. 
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

      // 1. 기본 QueryDSL 쿼리 작성
      JPAQuery<UserEntity> query = queryFactory
              .selectFrom(user)
              .where(user.role.eq(UserRole.ADMIN)); // role이 ADMIN인 조건

      // 2. 페이징 처리
      long total = query.stream().count(); // 전체 데이터 개수 가져오기
      List<User> users = query
              .offset(pageable.getOffset()) // 시작 위치
              .limit(pageable.getPageSize()) // 페이지당 데이터 개수
              .fetch()
              .stream()
              .map(UserEntity::toModel)
              .toList();

      log.info("[Repository] users.size : {}", users.size());
      // 3. Page로 변환하여 반환
      return new PageImpl<>(users, pageable, total);

   }

   @Override
   public User updateStatus(User user) {
      return userRepositoryJpa.save(UserEntity.from(user)).toModel();
   }
}
```

---
### 4.4 MyBatis 기반 Repository 구현 (예시)
MyBatis 기반 구현체를 추가로 작성하여도 서비스 계층은 기술 변경 영향을 받지 않습니다.  
**MyBatis 용** `UserDto`를 생성하여 사용하였습니다. 

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
@Repostiory
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

      // MyBatis 쿼리를 호출
      List<UserDto> userDtos = userRepositoryMybatis.findAllWithPageable(
              pageable.getOffset(),
              pageable.getPageSize()
      );

      // 총 데이터 개수를 가져오는 로직
      long totalCount = userRepositoryMybatis.countAll();

      // Page<User>로 변환
      List<User> users = userDtos.stream()
              .map(UserDto::toModel)
              .toList();

      // 반환: Page 구현체 생성
      return new PageImpl<>(users, pageable, totalCount);
   }

   @Override
   public Page<User> findAdminUsers(Pageable pageable) {
      // TODO - 구현필요
      return null;
   }

   @Override
   public User updateStatus(User user) {
      // TODO - 구현필요
      return null;
   }
}
```

---
### 4.5 서비스 계층에서 구현
서비스 계층에서는 기술에 종속되지 않은 `UserRepositoryPort` 인터페이스를 주입받아 사용하게 됩니다.  
위의 설계로 데이터 접근 기술이 변경되더라도 서비스 계층의 코드를 수정할 필요가 없습니다.

```java
@Service
@RequiredArgsConstructor
public class UserService implements UserServicePort {

   private final UserRepositoryPort userRepositoryPort;
   private final PasswordEncoder passwordEncoder;
   private final JwtUtil jwtUtil;

   // 회원가입 메서드
   @Override
   @Transactional
   public User signUp(UserCreateRequest userCreateRequest) {

      // 입력받은 이메일로 회원 존재 점검
      checkUserExistByEmail(userCreateRequest.getEmail());
      String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword()); // 비밀번호 암호화

      UserCreate create = UserCreate.builder()
              .email(userCreateRequest.getEmail())
              .password(encodedPassword)
              .build();

      return userRepositoryPort.save(User.from(create));
   }

   // 전체 사용자 조회
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
}
```

---
### 4.6 구현체 선택 

- 실제 동작할 구현체 선택의 경우 AppConfig.java 파일에서 선택한다.
- JPA 또는 MyBatis 2 기술을 동시에 개발할 이유는 없지만, 기술 전환이 필요할 경우 기술 구현체만 개발 후 컨피그 설정으로 변경할 수 있다.

```java
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepositoryJpa userRepositoryJpa;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepositoryMybatis userRepositoryMybatis;

    /**
     * Jpa 또는 MyBatis 구현체 선택
     */
    @Bean
    public UserRepository userRepository() {
        // JPA 사용
        return new UserRepositoryJpaCustomImpl(userRepositoryJpa, jpaQueryFactory);
        
        // MyBatis 사용
//        return new UserRepositoryMybatisImpl(userRepositoryMybatis);
    }
}
```

---
### 4.7 해당 설계의 장점

1. **기술 변경에 대한 유연성**
    - JPA → MyBatis 또는 다른 기술 스택으로 변경하여도 `UserService`와 같은 비즈니스 로직에는 영향을 주지 않습니다.
    - 기술별 구현체만 교체하면 됩니다.

2. **Repository 기본 원칙 준수**
    - Repository는 데이터 저장과 조회를 담당하며, 비즈니스 로직을 포함하지 않습니다.
    - 도메인 모델은 Service와 Model 내부에 집중되어 설계됩니다.

3. **클린 아키텍처 준수**
    - `UserRepository`는 중심부(hexagon)로부터 멀리 떨어진 기술 세부 사항을 캡슐화합니다.
    - 기술(구현체) 변경 시에도, 코어 도메인 계층은 영향을 받지 않습니다.

---

## 5. 참고사항 (JPA 사용 가이드)
> QueryDSL 기반으로 `BooleanBuilder`를 활용하여 조건 빌드, 페이징, 성능 최적화를 포함한 JPA 처리 방식에 대해 설명합니다.

### 5.1 QueryDSL 사용: BooleanBuilder로 조건 빌드
`BooleanBuilder`를 활용하여 조건을 유연하게 조합할 수 있는 구조를 제공하며, 특정 조건에 따라 동적 쿼리를 구축할 수 있습니다.

#### **예시 조건 빌드 객체**
`CodeConditionBuilder`는 `BooleanBuilder` 객체를 활용하여 검색조건(`CodeSearchCondition`)에 따라 동적으로 `BooleanExpression`을 생성합니다.

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
           return codeEntity.isNotNull(); // 항상 참인 조건
       }
   }
   ```

#### **예시 JPA 구현체 내에서 조건빌드 객체 사용**

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

### 5.2 페이징 처리
QueryDSL에서 `Pageable`을 사용하여 쉽게 페이징 처리를 구성할 수 있습니다.

- `.offset(pageable.getOffset())`: 페이지 시작 위치.
- `.limit(pageable.getPageSize())`: 페이지 단위 크기.
- `.orderBy(codeEntity.seq.asc())`: 엔티티의 `seq` 속성을 기준으로 정렬.

### 5.3 성능 최적화

1. **fetch 조인** 사용
```java
    query.leftJoin(codeEntity.child).fetchJoin();
```
2. **Batch Size 설정**  
  `Fetch Join`로 데이터를 가져올 때 성능 최적화를 위한 추가적인 설정입니다.  
연관 데이터가 많을 경우 쿼리 효율을 개선합니다.
   - yaml 이용
      ```yaml
      spring:
        jpa:
          properties:
            hibernate.default_batch_fetch_size: 100
      ```
   - Entity 코드에서 연관관계 필드에서 정의
      ```java
      @BatchSize(size = 100)
      private List<CodeEntity> child = new ArrayList<>();
      ```