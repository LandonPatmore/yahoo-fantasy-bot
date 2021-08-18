package com.landonpatmore.yahoofantasybot.bot.workflows.state

/**
 * Different states the bot can be in at any given time. Resembles a state machine with a
 * finite amount of states.
 */
sealed class BotState {
    /**
     * Bot has started up.
     */
    object Startup : BotState()

    /**
     * Authentication.
     */
    object Authentication : BotState()

    /**
     * Main workflow where the logic of the bot resides.
     */
    object Running : BotState()

    /**
     * Bot has errored out and output needs to be displayed to the user.
     */
    object Error : BotState()
}
