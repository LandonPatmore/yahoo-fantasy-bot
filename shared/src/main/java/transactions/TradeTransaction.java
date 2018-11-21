package transactions;

public class TradeTransaction extends Transaction {

    private final String status;

    public TradeTransaction(String entityOne, String entityTwo, String date, String status) {
        super(entityOne, entityTwo, date);
        this.type = TransactionType.TRADE;
        this.status = status;
    }

    @Override
    public String transactionBody() {
        if ("successful".equals(status)) {
            return "Type: Successful Trade" + "\\n" +
                    entityOne + " Acquired: " + listString(entityTwoList) + "\\n" +
                    entityTwo + " Acquired: " + listString(entityOneList) + "\\n";
        } else {
            return "Type: Vetoed Trade" + "\\n" +
                    entityOne + " Would have Acquired: " + listString(entityTwoList) + "\\n" +
                    entityTwo + " Would have Acquired: " + listString(entityOneList) + "\\n";
        }
    }
}
