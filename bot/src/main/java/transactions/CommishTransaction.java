package transactions;

public class CommishTransaction extends Transaction {

    public CommishTransaction(String date) {
        super(null, null, date);
        this.type = Transaction.TransactionType.COMMISH;
    }

    @Override
    public String transactionBody() {
        return "The commissioner has changed a setting within the league.  You may want to check or ask them what they changed!";
    }
}
