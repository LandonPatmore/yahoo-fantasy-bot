package bot.messaging_services

sealed class Message(val message : String) {
    class Score(message: String) : Message(message)
    class CloseScore(message: String) : Message(message)
    class MatchUp(message: String) : Message(message)
    class Standings(message: String) : Message(message)
    class Generic(message: String) : Message(message)
    class Unknown(message: String) : Message(message)
    sealed class Transaction(message: String) : Message(message) {
        class Add(message: String) : Transaction(message)
        class Drop(message: String) : Transaction(message)
        class AddDrop(message: String) : Transaction(message)
        class Trade(message: String) : Transaction(message)
        class Commish(message: String) : Transaction(message)
    }
}
