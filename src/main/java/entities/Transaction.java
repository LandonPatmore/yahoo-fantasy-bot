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
        this.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(date)), ZoneId.systemDefault());
        this.entityOneList = new ArrayList<>();
        this.entityTwoList = new ArrayList<>();
    }

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

    public void addPlayerToEntity(String player, String entityName) {
        if (entityName.equals(entityOne)) {
            entityOneList.add(player);
        } else {
            entityTwoList.add(player);
        }
    }

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

    public String getTransactionString() {
        final StringBuilder builder = new StringBuilder();

        switch (type) {
            case ADD:
                builder.append("===ADD ALERT===\\n");
                break;
            case DROP:
                builder.append("===DROP ALERT===\\n");
                break;
            case ADD_DROP:
                builder.append("===ADD/DROP ALERT===\\n");
                break;
            case TRADE:
                builder.append("===TRADE ALERT===\\n");
                break;
            case COMMISH:
                builder.append("===COMMISH CHANGE ALERT===\\n");
                break;
            default:
                break;
        }

        builder.append("Time: ").append(date.format(DateTimeFormatter.ofPattern("M/d/yyyy - h:mm a", Locale.US))).append("\\n\\n");
        builder.append(transactionBody());
        builder.append("\\n\\n");

        return builder.toString();
    }

    @Override
    public int compareTo(Transaction o) {
        return this.date.compareTo(o.date);
    }

    public abstract String transactionBody();
}
