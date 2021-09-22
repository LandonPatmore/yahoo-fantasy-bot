package com.landonpatmore.yahoofantasybot.bot.workflows.state

import com.github.scribejava.core.model.OAuth2AccessToken

sealed class ArbiterState {
  abstract val authToken: Pair<Long, OAuth2AccessToken>

  data class ReAuthenticate(
    override val authToken: Pair<Long, OAuth2AccessToken>
  ) : ArbiterState()

  data class RetrieveData(
    override val authToken: Pair<Long, OAuth2AccessToken>,
    val tokenValid: Boolean = false
  ) :
    ArbiterState()

  data class SendMessage(
    override val authToken: Pair<Long, OAuth2AccessToken>,
    val message: String
  ) : ArbiterState()
}
