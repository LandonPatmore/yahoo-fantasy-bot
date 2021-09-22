package com.landonpatmore.yahoofantasybot.bot.workflows.output

import com.github.scribejava.core.model.OAuth2AccessToken

sealed class AuthenticationOutput{
  data class Authenticated(val authToken: Pair<Long, OAuth2AccessToken>) : AuthenticationOutput()
  object Unauthenticated : AuthenticationOutput()
}