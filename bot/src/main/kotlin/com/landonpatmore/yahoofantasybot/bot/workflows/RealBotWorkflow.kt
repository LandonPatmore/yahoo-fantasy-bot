package com.landonpatmore.yahoofantasybot.bot.workflows

import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.BotRendering
import com.landonpatmore.yahoofantasybot.bot.workflows.state.BotState
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow

class RealBotWorkflow : BotWorkflow, StatefulWorkflow<Nothing, BotState, Nothing, BotRendering>() {
    override fun initialState(props: Nothing, snapshot: Snapshot?): BotState = BotState.Startup

    override fun render(
        renderProps: Nothing,
        renderState: BotState,
        context: RenderContext
    ): BotRendering = when (renderState) {
        BotState.Startup -> {
            // TODO: Render startup workflow
            TODO()
        }
        BotState.Authentication -> {
            // TODO: Render auth workflow
            TODO()
        }
        BotState.Running -> {
            TODO("Render running workflow")
        }
        BotState.Error -> {
            TODO("Print out error and shutdown")
        }
    }

    override fun snapshotState(state: BotState): Snapshot? = null
}