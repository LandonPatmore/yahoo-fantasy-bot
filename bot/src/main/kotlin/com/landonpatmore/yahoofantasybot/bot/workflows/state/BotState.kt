package com.landonpatmore.yahoofantasybot.bot.workflows.state

import com.github.scribejava.core.model.OAuth2AccessToken

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
  data class Authentication(val authToken: Pair<Long, OAuth2AccessToken>?) : BotState()

  /**
   * Main workflow where the logic of the bot resides.
   */
  data class Running(val authToken: Pair<Long, OAuth2AccessToken>) : BotState()

  /**
   * Bot has errored out and output needs to be displayed to the user.
   */
  data class Error(val errorMessage: String) : BotState()
}
