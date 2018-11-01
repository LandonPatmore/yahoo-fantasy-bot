package entities;

public class AddTransaction extends Transaction {

    protected AddTransaction(String entityOne, String entitiyTwo, String date) {
        super(entityOne, entitiyTwo, date);
    }

    @Override
    public String getTransactionString() {
        final String enitityTwoFixedName = entitiyTwo.substring(0,1).toUpperCase() + entitiyTwo.substring(1);
        return "===ADD ALERT===\\nAdd happened on: " + date + "\\n\\n" + entityOne + " added " + listString(entityTwoList) + " from " + enitityTwoFixedName + ".";
    }
}
