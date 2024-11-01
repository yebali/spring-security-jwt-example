package com.yebali.auth.util

import mu.KotlinLogging

abstract class Logger {
    val logger = KotlinLogging.logger(this.javaClass.name)
}
