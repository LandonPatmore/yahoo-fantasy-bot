package com.landonpatmore.yahoofantasybot.bot.workflows.impl

import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.oauth.OAuth20Service
import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.AuthenticationWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.output.AuthenticationOutput
import com.landonpatmore.yahoofantasybot.bot.workflows.props.AuthenticationProps
import com.landonpatmore.yahoofantasybot.shared.database.Db
import com.squareup.workflow1.StatelessWorkflow
import com.squareup.workflow1.Worker
import com.squareup.workflow1.action
import com.squareup.workflow1.runningWorker

class RealAuthenticationWorkflow(
  private val database: Db,
  private val oauthService: OAuth20Service
) : AuthenticationWorkflow, StatelessWorkflow<AuthenticationProps, AuthenticationOutput, Unit>() {
  override fun render(renderProps: AuthenticationProps, context: RenderContext) {
    context.runningWorker(
      worker = Worker.create {
        emit(
          refreshTokenIfNeeded(
            when (renderProps) {
              AuthenticationProps.Initialization -> database.getLatestTokenData()
              is AuthenticationProps.Token -> renderProps.authToken
            }
          )
        )
      }
    ) { output ->
      action {
        setOutput(
          when (output) {
            null -> AuthenticationOutput.Unauthenticated
            else -> AuthenticationOutput.Authenticated(authToken = output)
          }
        )
      }
    }
  }

  private fun refreshTokenIfNeeded(token: Pair<Long, OAuth2AccessToken>?) = token?.let {
    if (isTokenExpired(token.first, token.second.expiresIn)) {
      val refreshToken = oauthService.refreshAccessToken(token.second.refreshToken)
      database.saveToken(refreshToken)
      Pair(System.currentTimeMillis(), refreshToken)
    } else {
      token
    }
  }

  private fun isTokenExpired(retrieved: Long, expiresIn: Int): Boolean {
    val timeElapsed = ((System.currentTimeMillis() - retrieved) / 1000)
    return timeElapsed >= expiresIn
  }
}