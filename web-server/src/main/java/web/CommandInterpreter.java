package web;

import utils.ServicesHandler;
import utils.Yahoo;

public class CommandInterpreter {

    public static void interpretCommand(String message) {
        if (isBotCommand(message)) {
            final String commandResponse = commandResponse(message);
            if (commandResponse != null) {
                ServicesHandler.sendMessage(commandResponse);
            } else {
                ServicesHandler.sendMessage(unknownCommand(message));
            }
        }
    }

    private static boolean isBotCommand(String command) {
        return command.startsWith("./fantasy");
    }

    private static String commandResponse(String message) {
        final String[] m = message.trim().split(" ");
        if (m.length > 1) {
            switch (m[1]) {
                case "help":
                    return helpCommandsList();
                case "teams":
                    return Yahoo.getListOfTeams();
                case "records":
                    return Yahoo.getAllRecords();
                case "record":
                    try {
                        return Yahoo.getTeamRecord(m[2]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return "ERROR: Please enter a valid team number.";
                    }
                case "roster":
                    try {
                        return Yahoo.getTeamRoster(m[2]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return "ERROR: Please enter a valid team number.";
                    }
                default:
                    return null;
            }
        } else {
            return "What's up?";
        }
    }

    private static String helpCommandsList() {
//        return "< Fantasy Bot Commands >\\n\\n" +
//
//                "./fantasy <command> <args>\\n\\n" +
//
//                "# Get a list of all available commands\\n" +
//                "./fantasy help\\n" +
//                "# Get the list of teams in the league\\n" +
//                "./fantasy teams\\n" +
//                "# Get the list of team records in the league\\n" +
//                "./fantasy records\\n" +
//                "# Get the record of a specified team\\n" +
//                "./fantasy record <team number>\\n" +
//                "# Get the info on a specified team\\n" +
//                "./fantasy team <team number>\\n" +
//                "# Get the players on a specified team\\n" +
//                "./fantasy players <team number>\\n" +
//                "# Get the latest transactions (5 is the default)\\n" +
//                "./fantasy transactions <number or none to show default>\\n" +
//                "# Get the latest transactions for a specified team (5 is the default)\\n" +
//                "./fantasy teamtrans <team number> <number or none to show default>";

        return "< Fantasy Bot Commands >\\n\\n" +

                "./fantasy <command> <args>\\n\\n" +

                "# Get a list of all available commands\\n" +
                "./fantasy help\\n" +
                "# Get the list of teams in the league\\n" +
                "./fantasy teams\\n" +
                "# Get the list of team records in the league\\n" +
                "./fantasy records\\n" +
                "# Get the record of a specified team\\n" +
                "./fantasy record <team number>\\n" +
                "# Get the players on a specified team\\n" +
                "./fantasy roster <team number>\\n";
    }

    private static String unknownCommand(String message) {
        return "The command \\\"" + message + "\\\" is not a valid command. Please type \\\"./fantasy help\\\" for a list of available commands.";
    }
}
