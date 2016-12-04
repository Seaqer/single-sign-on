package core.domain.transfers;

import java.time.LocalDate;


public interface Transaction {
    public long getTransactionId();

    public float getAmount();

    public long getFromAccount();

    public long getToAccount();

    public LocalDate getTransactionDate();
}
