package transactions;

import java.util.ArrayList;

public class AddDropTransaction extends Transaction {

    private final ArrayList<Transaction> transactions;

    public AddDropTransaction(String date) {
        super(null, null, date);
        this.type = TransactionType.ADD_DROP;

        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction addTransaction) {
        transactions.add(addTransaction);
    }

    @Override
    public String transactionBody() {
        final StringBuilder builder = new StringBuilder();
        boolean nameGrabbed = false;

        builder.append("Type: Add/Drop\\n");

        for (Transaction t : transactions) {
            final String entityTwoFixedName = t.entityTwo.substring(0, 1).toUpperCase() + t.entityTwo.substring(1);

            if (!nameGrabbed) {
                builder.append("Team: ").append(t.entityOne).append("\\n");
                nameGrabbed = true;
            }
            if (t.type.equals(TransactionType.ADD)) {
                builder.append(t.entityTwoList.size() == 1 ? "Player Added: " + listString(t.entityTwoList) : "Players Added: " + listString(t.entityTwoList)).append("\\n");
                builder.append("Source: ").append(entityTwoFixedName).append("\\n");
            } else if (t.type.equals(TransactionType.DROP)) {
                builder.append(t.entityOneList.size() == 1 ? "Player Dropped: " + listString(t.entityOneList) : "Players Dropped: " + listString(t.entityOneList)).append("\\n");
                builder.append("Destination: ").append(entityTwoFixedName).append("\\n");
            }
        }

        return builder.toString();
    }


}
