package com.landonpatmore.yahoofantasybot.bot.workflows.rendering

sealed class StartupRendering {
    object Success : StartupRendering()
    class Failure(val error: String) : StartupRendering()
}