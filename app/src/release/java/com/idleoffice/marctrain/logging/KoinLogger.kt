package com.idleoffice.marctrain.logging

import org.koin.log.Logger

class KoinLogger : Logger {
    override fun debug(msg: String) {}

    override fun err(msg: String) {}

    override fun info(msg: String) {}
}