package models;

import core.domain.authorization.User;


public class UserInfo implements User {
    private long user_id;
    private String login;
    private String passwd;
    private Long del_user;

    public UserInfo() {
    }

    public long getUSER_ID() {
        return user_id;
    }

    public void setUSER_ID(long user_id) {
        this.user_id = user_id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Long getDEL_USER() {
        return del_user;
    }

    public void setDEL_USER(Long del_user) {
        this.del_user = del_user;
    }
}
