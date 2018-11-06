package entities;

public class DropTransaction extends Transaction {

    public DropTransaction(String entityOne, String entitiyTwo, String date) {
        super(entityOne, entitiyTwo, date);
        this.type = TransactionType.DROP;
    }

    @Override
    public String transactionBody() {
        final String entityTwoFixedName = entitiyTwo.substring(0,1).toUpperCase() + entitiyTwo.substring(1);
        return entityOne + " dropped " + listString(entityOneList) + " to " + entityTwoFixedName + ".";
    }
}
