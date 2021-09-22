package com.landonpatmore.yahoofantasybot.bot.workflows.interfaces

import com.landonpatmore.yahoofantasybot.bot.workflows.output.ArbiterOutput
import com.landonpatmore.yahoofantasybot.bot.workflows.props.ArbiterProps
import com.squareup.workflow1.Workflow

interface ArbiterWorkflow : Workflow<ArbiterProps, ArbiterOutput, Unit>