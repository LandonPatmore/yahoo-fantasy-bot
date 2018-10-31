import utils.Props;

public class Main {
    public static void main(String[] args) {
        Props.loadSettings();
        final Yahoo yahoo = new Yahoo();
        yahoo.authenticate();
        yahoo.testUrlDataGrab();
    }
}
