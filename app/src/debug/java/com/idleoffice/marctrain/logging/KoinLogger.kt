package com.idleoffice.marctrain.logging

import org.koin.android.logger.AndroidLogger
import org.koin.log.Logger

class KoinLogger : Logger by AndroidLogger()