package entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public abstract class Transaction {
    protected String entityOne;
    protected String entitiyTwo;
    protected final String date;
    protected final ArrayList<String> entityOneList;
    protected final ArrayList<String> entityTwoList;

    protected Transaction(String entityOne, String entitiyTwo, String date) {
        this.entityOne = entityOne;
        this.entitiyTwo = entitiyTwo;
        this.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(date)), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("M/d/yyyy - h:mm a", Locale.US));
        this.entityOneList = new ArrayList<>();
        this.entityTwoList = new ArrayList<>();
    }

    protected String listString(ArrayList<String> list) {
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

    public String getEntityOne() {
        return entityOne;
    }

    public String getEntitiyTwo() {
        return entitiyTwo;
    }

    public void addPlayerToEntity(String player, String entityName) {
        if (entityName.equals(entityOne)) {
            entityOneList.add(player);
        } else {
            entityTwoList.add(player);
        }
    }

    public abstract String getTransactionString();

    @Override
    public String toString() {
        return "Transaction{" +
                "entityOne='" + entityOne + '\'' +
                ", entitiyTwo='" + entitiyTwo + '\'' +
                ", date='" + date + '\'' +
                ", entityOneList=" + entityOneList +
                ", entityTwoList=" + entityTwoList +
                '}';
    }
}
