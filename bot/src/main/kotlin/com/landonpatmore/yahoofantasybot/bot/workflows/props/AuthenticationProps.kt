package com.landonpatmore.yahoofantasybot.bot.workflows.props

import com.github.scribejava.core.model.OAuth2AccessToken

sealed class AuthenticationProps {
  object Initialization : AuthenticationProps()
  data class Token(val authToken: Pair<Long, OAuth2AccessToken>) : AuthenticationProps()
}
