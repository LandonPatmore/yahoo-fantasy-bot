package com.landonpatmore.yahoofantasybot.bot.workflows.impl

import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.ArbiterWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.AuthenticationWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.BotWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.StartupWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.output.AuthenticationOutput
import com.landonpatmore.yahoofantasybot.bot.workflows.props.ArbiterProps
import com.landonpatmore.yahoofantasybot.bot.workflows.props.AuthenticationProps
import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.BotRendering
import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.StartupRendering
import com.landonpatmore.yahoofantasybot.bot.workflows.state.BotState
import com.landonpatmore.yahoofantasybot.bot.workflows.state.BotState.Startup
import com.landonpatmore.yahoofantasybot.bot.workflows.state.BotState.Authentication
import com.landonpatmore.yahoofantasybot.bot.workflows.state.BotState.Running
import com.landonpatmore.yahoofantasybot.bot.workflows.state.BotState.Error
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import com.squareup.workflow1.renderChild

class RealBotWorkflow(
  private val startupWorkflow: StartupWorkflow,
  private val authenticationWorkflow: AuthenticationWorkflow,
  private val arbiterWorkflow: ArbiterWorkflow
) : BotWorkflow, StatefulWorkflow<Unit, BotState, Unit, BotRendering>() {
  override fun initialState(props: Unit, snapshot: Snapshot?) = Startup

  override fun render(
    renderProps: Unit,
    renderState: BotState,
    context: RenderContext
  ) = BotRendering(
    childWorkflow = when (renderState) {
      Startup -> {
        val startup = context.renderChild(child = startupWorkflow)

        context.actionSink.send(
          action {
            state = when (startup) {
              is StartupRendering.Failure -> Error(errorMessage = startup.error)
              StartupRendering.Success -> Authentication(authToken = null)
            }
          }
        )
      }
      is Authentication -> {
        context.renderChild(
          child = authenticationWorkflow,
          props = AuthenticationProps.Initialization
        ) { output ->
          action {
            state = when (output) {
              is AuthenticationOutput.Authenticated -> Running(authToken = output.authToken)
              AuthenticationOutput.Unauthenticated -> Error(errorMessage = "Could not authenticate...FIX ME")
            }
          }
        }
      }
      is Running -> {
        context.renderChild(
          child = arbiterWorkflow,
          props = ArbiterProps(authToken = renderState.authToken)
        ) { output ->
          action {
            state = Error(errorMessage = output.errorMessage)
          }
        }
      }
      is Error -> {
        TODO("Get info about bot and some other things to output as well")
        TODO("Print out error and shutdown")
      }
    }
  )

  override fun snapshotState(state: BotState): Snapshot? = null
}