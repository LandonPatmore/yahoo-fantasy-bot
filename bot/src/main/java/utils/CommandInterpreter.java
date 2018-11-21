package utils;

import services.Yahoo;

public class CommandInterpreter {

    public static void interpretCommand(String message) {
        if(isBotCommand(message)) {
            final String commandResponse = commandResponse(message);
            if(commandResponse != null){

            } else {
//                ServicesHandler.sendMessage(unknownCommand(message));
            }
        }
    }

    private static boolean isBotCommand(String command) {
        return command.startsWith(";;");
    }

    private static String commandResponse(String message) {
        final String[] m = message.split(" ");
        switch (m[0]) {
            case ";;records":
                return null;
            case ";;teams":
                Yahoo.getListOfTeams();
                return null;
            default:
                return null;
        }
    }

    private static String unknownCommand(String message) {
        return "The command \"" + message + "\" is not a valid command.  Please type \";;help\" for a list of available commands.";
    }
}
