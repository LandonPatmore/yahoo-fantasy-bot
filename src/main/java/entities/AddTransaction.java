package entities;

public class AddTransaction extends Transaction {

    public AddTransaction(String entityOne, String entitiyTwo, String date) {
        super(entityOne, entitiyTwo, date);
        this.type = TransactionType.ADD;
    }

    @Override
    public String transactionBody() {
        final String enitityTwoFixedName = entitiyTwo.substring(0,1).toUpperCase() + entitiyTwo.substring(1);
        return entityOne + " added " + listString(entityTwoList) + " from " + enitityTwoFixedName + ".";
    }
}
