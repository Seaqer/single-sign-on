package core.domain.bis;

import java.time.LocalDate;

/**
 * Created by Артём on 29.11.2016.
 */
public interface Card {
    String getCardNumber();

    String getCardOwner();

    LocalDate getToDate();

    long getAccountNumber();
}
