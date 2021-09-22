package com.landonpatmore.yahoofantasybot.bot.workflows.props

import com.github.scribejava.core.model.OAuth2AccessToken

data class ArbiterProps(val authToken: Pair<Long, OAuth2AccessToken>)