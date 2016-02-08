package org.breakout.services

import com.philips.lighting.model.PHLightState

object LightState {

    object Alarm : PHLightState() {
        init {
            this.isOn = true
            this.hue = 0
            this.brightness = 254
        }
    }

    object Off : PHLightState() {
        init {
            this.isOn = false
        }
    }
}
