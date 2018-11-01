package entities;

public class TradeTransaction extends Transaction {

    public TradeTransaction(String entityOne, String entitiyTwo, String date) {
        super(entityOne, entitiyTwo, date);
    }

    @Override
    public String getTransactionString() {
        return "===TRADE ALERT===\\nTrade finalized on: " + date + "\\n\\n" + entityOne + " acquired: " + listString(entityTwoList) + ".\\n\\n" +
                entitiyTwo + " acquired: " + listString(entityOneList) + ".";
    }
}
