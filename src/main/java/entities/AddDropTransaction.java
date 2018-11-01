package entities;

import java.util.ArrayList;

public class AddDropTransaction extends Transaction {

    private String addTransaction;
    private String dropTransaction;

    public AddDropTransaction(String date) {
        super(null, null, date);
    }

    public void addTransaction(String entity, String otherEntity, ArrayList<String> players) {
        final String enitityTwoFixedName = otherEntity.substring(0, 1).toUpperCase() + otherEntity.substring(1);
        addTransaction = entity + " added " + listString(players) + " from " + enitityTwoFixedName + ".";
    }

    public void dropTransaction(String entity, String otherEntity, ArrayList<String> players) {
        final String enitityTwoFixedName = otherEntity.substring(0, 1).toUpperCase() + otherEntity.substring(1);
        dropTransaction = entity + " dropped " + listString(players) + " to " + enitityTwoFixedName + ".";
    }

    @Override
    public String getTransactionString() {
        return "===ADD/DROP ALERT===\\nDrop happened on: " + date + "\\n\\n" + addTransaction + "\\n\\n" + dropTransaction;
    }


}
