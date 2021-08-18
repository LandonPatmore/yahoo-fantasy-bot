package com.landonpatmore.yahoofantasybot.bot.workflows

import com.landonpatmore.yahoofantasybot.bot.workflows.rendering.BotRendering
import com.squareup.workflow1.Workflow

interface BotWorkflow : Workflow<Nothing, Nothing, BotRendering>