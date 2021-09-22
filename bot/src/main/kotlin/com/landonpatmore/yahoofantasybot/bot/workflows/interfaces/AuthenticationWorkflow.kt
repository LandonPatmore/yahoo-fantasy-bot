package com.landonpatmore.yahoofantasybot.bot.workflows.interfaces

import com.landonpatmore.yahoofantasybot.bot.workflows.output.AuthenticationOutput
import com.landonpatmore.yahoofantasybot.bot.workflows.props.AuthenticationProps
import com.squareup.workflow1.Workflow

interface AuthenticationWorkflow : Workflow<AuthenticationProps, AuthenticationOutput, Unit>