package com.landonpatmore.yahoofantasybot.bot.workflows.impl

import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.landonpatmore.yahoofantasybot.bot.workflows.interfaces.ArbiterWorkflow
import com.landonpatmore.yahoofantasybot.bot.workflows.output.ArbiterOutput
import com.landonpatmore.yahoofantasybot.bot.workflows.output.AuthenticationOutput.Authenticated
import com.landonpatmore.yahoofantasybot.bot.workflows.output.AuthenticationOutput.Unauthenticated
import com.landonpatmore.yahoofantasybot.bot.workflows.props.ArbiterProps
import com.landonpatmore.yahoofantasybot.bot.workflows.props.AuthenticationProps
import com.landonpatmore.yahoofantasybot.bot.workflows.state.ArbiterState
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.Worker
import com.squareup.workflow1.action
import com.squareup.workflow1.runningWorker
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

class RealArbiterWorkflow(
  private val authenticationWorkflow: RealAuthenticationWorkflow,
  private val oauthService: OAuth20Service
) : ArbiterWorkflow, StatefulWorkflow<ArbiterProps, ArbiterState, ArbiterOutput, Unit>() {
  override fun initialState(
    props: ArbiterProps,
    snapshot: Snapshot?
  ) = ArbiterState.RetrieveData(authToken = props.authToken)

  override fun render(
    renderProps: ArbiterProps,
    renderState: ArbiterState,
    context: RenderContext
  ) = when (renderState) {
    is ArbiterState.ReAuthenticate -> context.renderChild(
      child = authenticationWorkflow,
      props = AuthenticationProps.Token(authToken = renderProps.authToken)
    ) { output ->
      action {
        when (output) {
          is Authenticated -> state = ArbiterState.RetrieveData(
            authToken = output.authToken,
            tokenValid = true
          )
          Unauthenticated -> setOutput(ArbiterOutput(errorMessage = "It ain't authed bruv, why"))
        }
      }
    }
    is ArbiterState.RetrieveData -> when (renderState.tokenValid) {
      true -> context.runningWorker(
        worker = Worker.timer(15_000)
      ) {
        action {
          grabData(state.authToken.second, "")
        }
      }
      false -> context.actionSink.send(
        action {
          state = ArbiterState.ReAuthenticate(authToken = renderState.authToken)
        }
      )
    }
    is ArbiterState.SendMessage -> TODO()
  }

  override fun snapshotState(state: ArbiterState): Snapshot? = null

  private fun grabData(token: OAuth2AccessToken, url: String): Document {
    println("Grabbing Data...")
    val request = OAuthRequest(Verb.GET, url)
    oauthService.signRequest(token, request)
    val response = oauthService.execute(request)
    println("Data grabbed.")
    return Jsoup.parse(response.body, "", Parser.xmlParser())
  }
}