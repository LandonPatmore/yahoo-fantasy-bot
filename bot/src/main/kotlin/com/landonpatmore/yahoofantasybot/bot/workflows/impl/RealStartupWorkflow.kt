package com.landonpatmore.yahoofantasybot.bot.workflows.impl

import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.StartupWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.StartupRendering
import com.landonpatmore.yahoofantasybot.shared.utils.models.EnvVariable
import com.squareup.workflow1.StatelessWorkflow

class RealStartupWorkflow : StartupWorkflow,
  StatelessWorkflow<Unit, Nothing, StartupRendering>() {

  override fun render(
    renderProps: Unit,
    context: RenderContext
  ) = when {
    envCheck.isNotEmpty() -> StartupRendering.Failure(error = envCheck.joinToString("\n"))
    else -> StartupRendering.Success
  }

  private val envCheck = listOfNotNull(
    checkString(EnvVariable.Str.YahooClientId),
    checkString(EnvVariable.Str.YahooClientSecret),
    checkGameKey(EnvVariable.Str.YahooGameKey),
    checkString(EnvVariable.Str.YahooLeagueId),
    checkString(EnvVariable.Str.GroupMeBotId),
    checkString(EnvVariable.Str.DiscordWebhookUrl),
    checkString(EnvVariable.Str.SlackWebhookUrl),
    checkString(EnvVariable.Str.JdbcDatabaseUrl),
    checkInt(EnvVariable.Integer.Port)
  )

  private fun checkGameKey(env: EnvVariable.Str): StartupRendering.Failure? =
    when (val failure = checkString(env)) {
      is StartupRendering.Failure -> failure
      else -> when (env.variable.toLowerCase()) {
        "nfl", "nba", "mlb" -> null
        else -> errorMessage(env)
      }
    }

  private fun checkString(
    env: EnvVariable.Str
  ) = when (!env.optional && env.variable.trim().isEmpty()) {
    true -> errorMessage(env)
    false -> null
  }

  private fun checkInt(
    env: EnvVariable.Integer
  ) = when (env.variable == -1) {
    true -> errorMessage(env)
    false -> null
  }

  private fun errorMessage(env: EnvVariable) =
    StartupRendering.Failure(error = "$env was entered incorrectly")
}