package entities;

public class DropTransaction extends Transaction {

    protected DropTransaction(String entityOne, String entitiyTwo, String date) {
        super(entityOne, entitiyTwo, date);
    }

    @Override
    public String getTransactionString() {
        final String enitityTwoFixedName = entitiyTwo.substring(0,1).toUpperCase() + entitiyTwo.substring(1);
        return "===DROP ALERT===\\nDrop happened on: " + date + "\\n\\n" + entityOne + " dropped " + listString(entityOneList) + " to " + enitityTwoFixedName + ".";
    }
}
