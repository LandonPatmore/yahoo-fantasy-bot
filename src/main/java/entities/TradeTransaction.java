package entities;

public class TradeTransaction extends Transaction {

    public TradeTransaction(String entityOne, String entitiyTwo, String date) {
        super(entityOne, entitiyTwo, date);
        this.type = TransactionType.TRADE;
    }

    @Override
    public String transactionBody() {
        return "Type: Trade" + "\\n" +
                entityOne + " Acquired: " + listString(entityTwoList) + "\\n" +
                entityTwo + " Acquired: " + listString(entityOneList) + "\\n";
    }
}
