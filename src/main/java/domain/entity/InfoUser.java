package domain.entity;

import core.domain.authorization.User;

import javax.persistence.*;

/**
 * Информация о пользователе
 */
@Entity
@Table(name = "USERS")
public class InfoUser implements User {
    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;
    @Column(name = "LOGIN", nullable = false)
    private String login;
    @Column(name = "PASS", nullable = false)
    private String password;
    @Column(name = "DEL_USER")
    private Long delUser;

    /**
     * Создать пользователя
     *
     * @param id       идентификатор пользователя
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @param delUser  пользователь который закрыл текущего пользователя
     */
    public InfoUser(Long id, String login, String password, Long delUser) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.delUser = delUser;
    }

    /**
     * Создать пустого пользователя
     *
     * @return пустой пользователь
     */
    public static User createEmpty() {
        return new InfoUser(null, null, null, null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Long getDelUser() {
        return delUser;
    }

    @Override
    public void setDelUser(Long delUser) {
        this.delUser = delUser;
    }

    @Override
    public String toString() {
        return "InfoUser{" +
                "login='" + login + '\'' +
                ", delUser=" + delUser +
                '}';
    }
}
