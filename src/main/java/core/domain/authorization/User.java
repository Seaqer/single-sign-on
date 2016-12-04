package core.domain.authorization;


public interface User {
    long getUSER_ID();

    void setUSER_ID(long user_id);

    String getLogin();

    void setLogin(String login);

    String getPasswd();

    void setPasswd(String passwd);

    Long getDEL_USER();

    void setDEL_USER(Long del_user);
}
