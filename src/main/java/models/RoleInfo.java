package models;


import core.domain.authorization.Role;

public class RoleInfo implements Role {
    private long ROLE_ID;
    private String ROLE_NAME;

    public RoleInfo() {
    }

    public RoleInfo(long ROLE_ID, String ROLE_NAME) {
        this.ROLE_ID = ROLE_ID;
        this.ROLE_NAME = ROLE_NAME;
    }

    public long getROLE_ID() {
        return ROLE_ID;
    }

    public void setROLE_ID(long ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }

    public String getROLE_NAME() {
        return ROLE_NAME;
    }

    public void setROLE_NAME(String ROLE_NAME) {
        this.ROLE_NAME = ROLE_NAME;
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "ROLE_NAME='" + ROLE_NAME + '\'' +
                '}';
    }
}
