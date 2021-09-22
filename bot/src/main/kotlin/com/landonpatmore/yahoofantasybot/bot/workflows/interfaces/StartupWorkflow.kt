package com.landonpatmore.yahoofantasybot.bot.workflows.interfaces

import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.StartupRendering
import com.squareup.workflow1.Workflow

interface StartupWorkflow : Workflow<Unit, Nothing, StartupRendering>