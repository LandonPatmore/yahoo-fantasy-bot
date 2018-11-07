package entities;

public class AddTransaction extends Transaction {

    public AddTransaction(String entityOne, String entityTwo, String date) {
        super(entityOne, entityTwo, date);
        this.type = TransactionType.ADD;
    }

    @Override
    public String transactionBody() {
        final String entityTwoFixedName = entityTwo.substring(0, 1).toUpperCase() + entityTwo.substring(1);
        return "Type: Add" + "\\n" +
                "Team: " + entityOne + "\\n" +
                (entityTwoList.size() == 1 ? "Player Added: " + listString(entityTwoList) : "Players Added: " + listString(entityTwoList)) + "\\n" +
                "Source: " + entityTwoFixedName + "\\n";
    }
}
