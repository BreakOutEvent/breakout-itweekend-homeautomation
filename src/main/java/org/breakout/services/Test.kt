package org.breakout.services

import org.springframework.stereotype.Component
import java.util.*
import kotlin.concurrent.timerTask

@Component
class Test {

    constructor() {
        val controller = HueController()
        val timer = Timer().schedule(timerTask { controller.alert() }, 0, 5000)
    }
}
