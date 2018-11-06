package entities;

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

        for (Transaction t : transactions) {
            builder.append(t.specialAddDropTransactionString());
        }

        return builder.toString();
    }


}
