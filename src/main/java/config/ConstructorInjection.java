package config;
import interfaces.repository.OperationRepository;
import interfaces.repository.RoleRepository;
import interfaces.repository.UserRepository;
import interfaces.service.PasswordManager;
import interfaces.service.UserValidator;
import interfaces.service.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import repository.jdbc.OperationRepositoryJDBC;
import repository.jdbc.RoleRepositoryJDBC;
import repository.jdbc.UserRepositoryJDBC;
import service.OperationsService;
import service.RoleService;
import service.UserService;
import service.support.CommonValidatorService;
import service.support.HashPasswordManager;
import service.support.UserValidatorService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:connection.properties")
public class ConstructorInjection {
    @Autowired
    Environment environment;

    @Bean
    public Connection connection() throws SQLException {
        String name = environment.getProperty("db.path");
        name = environment.getProperty("db.name");
        name = environment.getProperty("db.password");


        return DriverManager.getConnection(environment.getProperty("db.path"),
                environment.getProperty("db.name"),
                environment.getProperty("db.password"));
    }

    @Bean
    public PasswordManager passwordManager() {
        return new HashPasswordManager();
    }

    @Bean
    public UserValidator userValidator() {
        return new UserValidatorService();
    }

    @Bean
    public Validator validator() {
        return new CommonValidatorService();
    }

    @Bean
    public UserRepository userRepository(Connection connection) {
        return new UserRepositoryJDBC(connection);
    }

    @Bean
    public RoleRepository roleRepository(Connection connection) {
        return new RoleRepositoryJDBC(connection);
    }

    @Bean
    public OperationRepository operationRepository(Connection connection) {
        return new OperationRepositoryJDBC(connection);
    }

    @Bean
    public UserService userService(PasswordManager passwordManager, UserValidator userValidator, UserRepository userRepository) {
        return new UserService(passwordManager, userValidator, userRepository);
    }

    @Bean
    public RoleService roleService(UserService userService,  RoleRepository roleRepository, Validator validator) {
        return new RoleService(userService, roleRepository, validator);
    }

    @Bean
    public OperationsService operationsService(RoleService roleService, OperationRepository operationRepository, Validator validator) {
        return new OperationsService(roleService, operationRepository, validator);
    }


}
