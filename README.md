# BASIC DYNAMIC AUTHENTICATION WITH SPRING BOOT
 - Using java Reflections and Anotations to create an dynamic authentication api
# SETUP
- create a `application.properties` file in `src/main/resources` to setup your database connection. See content of `.env` file
- Run project as Spring Boot Aplication: `$ mvn spring-boot:run`

# Notes
- Not support controller method with `@[Get/Post/Put/Delete]Mapping` yet so use `@RequestMapping` instead.
