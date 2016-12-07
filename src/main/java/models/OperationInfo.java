package models;


import core.domain.authorization.Operation;

public class OperationInfo implements Operation {
    private long OPER_ID ;
    private String OPER_NAME;

    public OperationInfo() {
    }

    public OperationInfo(long OPER_ID, String OPER_NAME) {
        this.OPER_ID = OPER_ID;
        this.OPER_NAME = OPER_NAME;
    }

    public long getOPER_ID() {
        return OPER_ID;
    }

    public void setOPER_ID(long OPER_ID) {
        this.OPER_ID = OPER_ID;
    }

    public String getOPER_NAME() {
        return OPER_NAME;
    }

    public void setOPER_NAME(String OPER_NAME) {
        this.OPER_NAME = OPER_NAME;
    }

    @Override
    public String toString() {
        return "OperationInfo{" +
                "OPER_NAME='" + OPER_NAME + '\'' +
                '}';
    }
}
