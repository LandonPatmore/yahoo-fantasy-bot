package transactions;

public class DropTransaction extends Transaction {

    public DropTransaction(String entityOne, String entityTwo, String date) {
        super(entityOne, entityTwo, date);
        this.type = TransactionType.DROP;
    }

    @Override
    public String transactionBody() {
        final String entityTwoFixedName = entityTwo.substring(0, 1).toUpperCase() + entityTwo.substring(1);
        return "Type: Drop" + "\\n" +
                "Team: " + entityOne + "\\n" +
                (entityOneList.size() == 1 ? "Player Dropped: " + listString(entityOneList) : "Players Dropped: " + listString(entityOneList)) + "\\n" +
                "Destination: " + entityTwoFixedName + "\\n";
    }
}
