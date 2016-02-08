package org.breakout.controller

import org.breakout.services.HueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hue")
class HueController @Autowired constructor(val hueService: HueService) {


    @RequestMapping("/", method = arrayOf(GET))
    fun buzz() {
        hueService.alert()
    }

}

