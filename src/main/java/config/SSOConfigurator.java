package config;

import domain.exceptions.SqlErrorCodeTraslator;
import interfaces.repository.custom.OperationRepository;
import interfaces.repository.custom.RoleRepository;
import interfaces.repository.custom.UserRepository;
import interfaces.service.OperationsService;
import interfaces.service.RoleService;
import interfaces.service.UserService;
import interfaces.utils.PasswordManager;
import interfaces.utils.UserValidator;
import interfaces.utils.Validator;
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
import repository.spring.SpringJdbcOperationRepository;
import repository.spring.SpringJdbcRoleRepository;
import repository.spring.SpringJdbcUserRepository;
import service.SimpleOperationsService;
import service.SimpleRoleService;
import service.SimpleUserService;
import utils.CommonValidatorService;
import utils.HashPasswordManager;
import utils.UserValidatorService;

import javax.sql.DataSource;


/**
 *  Spring configuration class for the application
 */
@Configuration
@EnableTransactionManagement
@ComponentScan
@PropertySource("classpath:connection.properties")
public class SSOConfigurator {
    @Autowired
    Environment environment;

    /**
     * Сreate DataSource
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(environment.getProperty("db.path"),
                environment.getProperty("db.name"),
                environment.getProperty("db.password"));
    }

    /**
     * Create JdbcTemplate
     * @param dataSource DataSource
     * @return JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setExceptionTranslator(new SqlErrorCodeTraslator());
        return jdbcTemplate;
    }

    /**
     * Create NamedParameterJdbcTemplate
     * @param jdbcTemplate JdbcTemplate
     * @return NamedParameterJdbcTemplate
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    /**
     * Create PlatformTransactionManager
     * @param dataSource DataSource
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * Create PasswordManager
     * @return PasswordManager
     */
    @Bean
    public PasswordManager passwordManager() {
        return new HashPasswordManager();
    }

    /**
     * Create UserValidator
     * @return UserValidator
     */
    @Bean
    public UserValidator userValidator() {
        return new UserValidatorService();
    }

    /**
     * Create Validator
     * @return Validator
     */
    @Bean
    public Validator validator() {
        return new CommonValidatorService();
    }

    /**
     * Create UserRepository
     * @param namedParameterJdbcTemplate NamedParameterJdbcTemplate
     * @return UserRepository
     */
    @Bean
    public UserRepository userRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SpringJdbcUserRepository(namedParameterJdbcTemplate);
    }

    /**
     * Create RoleRepository
     * @param namedParameterJdbcTemplate NamedParameterJdbcTemplate
     * @return RoleRepository
     */
    @Bean
    public RoleRepository roleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SpringJdbcRoleRepository(namedParameterJdbcTemplate);
    }

    /**
     * create OperationRepository
     * @param namedParameterJdbcTemplate NamedParameterJdbcTemplate
     * @return OperationRepository
     */
    @Bean
    public OperationRepository operationRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SpringJdbcOperationRepository(namedParameterJdbcTemplate);
    }

    /**
     * Create SimpleUserService
     * @param passwordManager PasswordManager
     * @param userValidator UserValidator
     * @param userRepository UserRepository
     * @return SimpleUserService
     */
    @Bean
    public UserService userService(PasswordManager passwordManager, UserValidator userValidator, UserRepository userRepository) {
        return new SimpleUserService(passwordManager, userValidator, userRepository);
    }

    /**
     * Create SimpleRoleService
     * @param userService SimpleUserService
     * @param roleRepository RoleRepository
     * @param validator Validator
     * @return SimpleRoleService
     */
    @Bean
    public RoleService roleService(UserService userService, RoleRepository roleRepository, Validator validator) {
        return new SimpleRoleService(userService, roleRepository, validator);
    }

    /**
     * Create SimpleOperationsService
     * @param roleService SimpleRoleService
     * @param operationRepository OperationRepository
     * @param validator Validator
     * @return SimpleOperationsService
     */
    @Bean
    public OperationsService operationsService(RoleService roleService, OperationRepository operationRepository, Validator validator) {
        return new SimpleOperationsService(roleService, operationRepository, validator);
    }
}
