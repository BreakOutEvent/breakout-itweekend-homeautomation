package org.breakout.services

import org.springframework.stereotype.Component
import java.util.*
import javax.sound.sampled.AudioSystem

@Component
class Test {

    constructor() {
        val controller = Controller()
        controller.findBridges()
        controller.connectToLastKnownAccessPoint()
        val timer = Timer()
        val task = RandomLightTask(controller)
        val task2 = LightOffTask(controller)
        timer.schedule(task, 0, 10000)
        timer.schedule(task2, 800, 10000)
    }
}

class RandomLightTask(val controller: Controller) : TimerTask() {
    override fun run() {
        playSound()
        try {
            controller.randomLights()
        } catch (e: Exception) {
            println("err")
        }


    }
}

class LightOffTask(val controller: Controller) : TimerTask() {
    override fun run() {
        try {
            controller.lightsOff()
        } catch (e: Exception) {
            println("Propably lights not connected yet")
        }
    }
}


fun playSound() {
    val clip = AudioSystem.getClip()
    val inputStream = ClassLoader.getSystemResourceAsStream("horn.wav")
    val audioInputStream = AudioSystem.getAudioInputStream(inputStream)
    clip.open(audioInputStream)
    clip.start()
}
