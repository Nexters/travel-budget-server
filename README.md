# Travel budget server

## Getting started
1. Set Active Profiles to "local"
2. Run local mysql DB using 13303 port. (Or mariaDB) 
3. Execute create_db.sql and create_db_user.sql
4. Run TravelBudgetApplication

## Code Style
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Project Structure
- Based on DDD
- Reference: [DDD Sample](https://github.com/citerus/dddsample-core/tree/master/src/main/java/se/citerus/dddsample)
- Structure
  - web
    - UserController.java
    - interceptor
      - AuthInterceptor.java
    - filter
      - Filter.java
  - application: Application Layer.
    - authentication
      - SignInService.java
  - config
    - UserConfig.java
  - domain: Domain Layer. Domain model(Entity), Helper, Exception, ...
    - user
      - User.java
      - UserService.java
  - infra
    - user
      - UserRepository.java
      - KakaoHttpClient.java
    - http
      - BaseHttpClient.java
