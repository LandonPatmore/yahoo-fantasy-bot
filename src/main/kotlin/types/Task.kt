package types

sealed class Task {
    object MatchUpUpdate : Task()
    object ScoreUpdate : Task()
    object CloseScoreUpdate : Task()
}