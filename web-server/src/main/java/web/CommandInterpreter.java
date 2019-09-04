package web;

import utils.ServicesHandler;
import utils.Yahoo;

public class CommandInterpreter {

    public static void interpretCommand(String message) {
        final String commandResponse = commandResponse(message);
        if (commandResponse != null) {
            ServicesHandler.sendMessage(commandResponse);
        } else {
            ServicesHandler.sendMessage(unknownCommand(message));
        }
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
                        return Yahoo.getTeamRecord(Integer.parseInt(m[2]));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return "ERROR: Please enter a valid team number.";
                    } catch (NumberFormatException e) {
                        return "ERROR: Please enter a valid number.";
                    }
                case "roster":
                    try {
                        return Yahoo.getTeamRoster(Integer.parseInt(m[2]));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return "ERROR: Please enter a valid team number.";
                    } catch (NumberFormatException e) {
                        return "ERROR: Please enter a valid number.";
                    }
                case "team":
                    try {
                        return Yahoo.getTeamInfo(Integer.parseInt(m[2]));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return "ERROR: Please enter a valid team number.";
                    } catch (NumberFormatException e) {
                        return "ERROR: Please enter a valid number.";
                    }
                case "transactions":
                    try {
                        return Yahoo.getTransactions(null, Integer.parseInt(m[2]));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return Yahoo.getTransactions(null, 5);
                    } catch (NumberFormatException e) {
                        return "ERROR: Please enter a valid number.";
                    }
                case "teamtrans":
                    try {
                        Integer.parseInt(m[2]);
                    } catch (NumberFormatException e) {
                        return "ERROR: Please enter a valid team number.";
                    }

                    try {
                        Integer.parseInt(m[3]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return Yahoo.getTransactions(Integer.parseInt(m[2]), 5);
                    } catch (Exception e) {
                        return "ERROR: Please enter a valid number for transactions amount.";
                    }
                    return Yahoo.getTransactions(Integer.parseInt(m[2]), Integer.parseInt(m[3]));

                default:
                    return null;
            }
        } else {
            return "What's up?";
        }
    }

    private static String helpCommandsList() {
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
                "./fantasy roster <team number>\\n" +
                "# Get the info on a specified team\\n" +
                "./fantasy team <team number>\\n" +
                "# Get the transactions on a specified team\\n" +
                "./fantasy teamtrans <team number> <number or none to show default>";
    }

    private static String unknownCommand(String message) {
        return "The command \\\"" + message + "\\\" is not a valid command. Please type \\\"./fantasy help\\\" for a list of available commands.";
    }
}
