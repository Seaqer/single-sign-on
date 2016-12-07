package config;

import exceptions.SqlErrorCodeTraslator;
import interfaces.repository.OperationRepository;
import interfaces.repository.RoleRepository;
import interfaces.repository.UserRepository;
import interfaces.service.PasswordManager;
import interfaces.service.UserValidator;
import interfaces.service.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import repository.spring.OperationRepositorySpring;
import repository.spring.RoleRepositorySpring;
import repository.spring.UserRepositorySpring;
import service.UserService;
import service.support.CommonValidatorService;
import service.support.HashPasswordManager;
import service.support.UserValidatorService;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan
@PropertySource("classpath:connection.properties")
public class ConstructorInjection {
    @Autowired
    Environment environment;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(environment.getProperty("db.path"),
                environment.getProperty("db.name"),
                environment.getProperty("db.password"));
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setExceptionTranslator(new SqlErrorCodeTraslator());
        return jdbcTemplate;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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
    public UserRepository userRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new UserRepositorySpring(namedParameterJdbcTemplate);
    }

    @Bean
    public RoleRepository roleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new RoleRepositorySpring(namedParameterJdbcTemplate);
    }

    @Bean
    public OperationRepository operationRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new OperationRepositorySpring(namedParameterJdbcTemplate);
    }

    @Bean
    public UserService userService(PasswordManager passwordManager, UserValidator userValidator, UserRepository userRepository) {
        return new UserService(passwordManager, userValidator, userRepository);
    }
//
//    @Bean
//    public RoleService roleService(UserService userService,  RoleRepository roleRepository, Validator validator) {
//        return new RoleService(userService, roleRepository, validator);
//    }
//
//    @Bean
//    public OperationsService operationsService(RoleService roleService, OperationRepository operationRepository, Validator validator) {
//        return new OperationsService(roleService, operationRepository, validator);
//    }
}
