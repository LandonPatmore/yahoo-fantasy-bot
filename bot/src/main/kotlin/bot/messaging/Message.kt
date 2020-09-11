package bot.messaging

sealed class Message(val title: String, val message: String) {
    class Score(message: String) : Message("Score", message)
    class CloseScore(message: String) : Message("Close Score", message)
    class MatchUp(message: String) : Message("Match Up", message)
    class Standings(message: String) : Message("Standings", message)
    class Generic(message: String) : Message("Message", message)
    class Unknown(message: String) : Message("", message)
    sealed class Transaction(title: String, message: String) : Message(title, message) {
        class Add(message: String) : Transaction("Add", message)
        class Drop(message: String) : Transaction("Drop", message)
        class AddDrop(message: String) : Transaction("Add/Drop", message)
        class Trade(message: String) : Transaction("Trade", message)
        class Commish(message: String) : Transaction("Commish", message)
    }
}
