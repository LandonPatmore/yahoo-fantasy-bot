package com.landonpatmore.yahoofantasybot.bot.workflows.interfaces

import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.BotRendering
import com.squareup.workflow1.Workflow

interface BotWorkflow : Workflow<Unit, Unit, BotRendering>