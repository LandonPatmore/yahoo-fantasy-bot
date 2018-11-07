package entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public abstract class Transaction implements Comparable<Transaction> {

    public enum TransactionType {
        ADD,
        DROP,
        ADD_DROP,
        TRADE,
        COMMISH
    }

    public String entityOne;
    public String entitiyTwo;
    public final LocalDateTime date;
    public final ArrayList<String> entityOneList;
    public final ArrayList<String> entityTwoList;
    public TransactionType type;

    public Transaction(String entityOne, String entitiyTwo, String date) {
        this.entityOne = entityOne;
        this.entitiyTwo = entitiyTwo;
        this.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(date)), ZoneId.of("America/New_York"));
        this.entityOneList = new ArrayList<>();
        this.entityTwoList = new ArrayList<>();
    }

    /**
     * Dynamically creates a list string.
     *
     * @param list the list
     * @return string of transaction
     */
    public String listString(ArrayList<String> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 2) {
            return list.get(0) + " and " + list.get(1);
        } else if (list.size() > 2) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < list.size() - 1; i++) {
                builder.append(list.get(i)).append(", ");
            }
            builder.append(" and ").append(list.get(list.size() - 1));

            return builder.toString();
        }

        return "";
    }

    /**
     * Adds a player to a certain entity.
     *
     * @param player     the player data
     * @param entityName the entity to add to
     */
    public void addPlayerToEntity(String player, String entityName) {
        if (entityName.equals(entityOne)) {
            entityOneList.add(player);
        } else {
            entityTwoList.add(player);
        }
    }

    /**
     * Used to create an AddDropTransaction string because the way the string is printed out is different from normal.
     *
     * @return string of transaction
     */
    public String specialAddDropTransactionString() {
        final StringBuilder builder = new StringBuilder();

        switch (type) {
            case ADD:
            case DROP:
                builder.append(transactionBody()).append(" ");
                break;
            default:
                break;
        }

        return builder.toString();
    }

    /**
     * Creates the transaction string with the proper format.
     *
     * @return transaction string
     */
    public String getTransactionString() {
        return "Time: " + date.format(DateTimeFormatter.ofPattern("M/d/yyyy - h:mm a", Locale.US)) + "\\n\\n" +
                transactionBody() +
                "\\n\\n";
    }

    /**
     * Compares based on dates
     *
     * @param o transaction object
     * @return Compared data
     */
    @Override
    public int compareTo(Transaction o) {
        return this.date.compareTo(o.date);
    }

    /**
     * Creates the transaction body.
     *
     * @return transaction body
     */
    public abstract String transactionBody();
}
