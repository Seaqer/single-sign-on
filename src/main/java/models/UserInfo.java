package models;

import core.domain.authorization.User;


public class UserInfo implements User {
    private long USER_ID;
    private String LOGIN;
    private String PASS;
    private Long DEL_USER;

    public UserInfo() {
    }

    public UserInfo(long USER_ID, String LOGIN, String PASS, Long DEL_USER) {
        this.USER_ID = USER_ID;
        this.LOGIN = LOGIN;
        this.PASS = PASS;
        this.DEL_USER = DEL_USER;
    }

    @Override
    public long getUSER_ID() {
        return USER_ID;
    }

    @Override
    public void setUSER_ID(long USER_ID) {
        this.USER_ID = USER_ID;
    }

    @Override
    public String getLOGIN() {
        return LOGIN;
    }

    @Override
    public void setLOGIN(String LOGIN) {
        this.LOGIN = LOGIN;
    }

    @Override
    public String getPASS() {
        return PASS;
    }

    @Override
    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    @Override
    public Long getDEL_USER() {
        return DEL_USER;
    }

    @Override
    public void setDEL_USER(Long DEL_USER) {
        this.DEL_USER = DEL_USER;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                " LOGIN='" + LOGIN + '\'' +
                ", DEL_USER=" + DEL_USER +
                '}';
    }
}
