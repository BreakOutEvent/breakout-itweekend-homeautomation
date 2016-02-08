package org.breakout.services

import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHBridgeSearchManager
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHLight
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.timerTask

@Service
class HueService {

    private val sdk: PHHueSDK
    private val listener: PHSDKListener
    private val logger = LoggerFactory.getLogger(HueListener::class.java)

    init {
        this.sdk = PHHueSDK.getInstance()
        this.listener = HueListener(this.sdk)
        sdk.notificationManager.registerSDKListener(listener)
        val searchManager = sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE) as PHBridgeSearchManager
        searchManager.search(true, true)
    }

    fun alert() {
        alertLightOn()
        playAlertSound()
        Timer().schedule(timerTask {
            alertLightOff()
        }, 400)
    }

    private fun playAlertSound() {
        val clip = AudioSystem.getClip()
        val inputStream = ClassLoader.getSystemResourceAsStream("horn.wav")
        val audioInputStream = AudioSystem.getAudioInputStream(inputStream)
        clip.open(audioInputStream)
        clip.start()
    }

    private fun alertLightOn() {
        getAllLights().forEach { pair ->
            // A listener could be attached her, omitted for simplicity
            pair.first.updateLightState(pair.second, LightState.Alarm)
        }
    }

    private fun getAllLights(): List<Pair<PHBridge, PHLight>> {
        val bridge = sdk.selectedBridge

        if (sdk.selectedBridge == null) {
            logger.error("Not connected to bridge. Returning empty list of lights")
            return emptyList()
        }

        val cache = bridge.resourceCache
        return cache.allLights.map { Pair(bridge, it) }
    }

    private fun alertLightOff() {
        getAllLights().forEach { pair ->
            // A listener could be attached her, omitted for simplicity
            pair.first.updateLightState(pair.second, LightState.Off)
        }
    }


    /**
     * Connect to the last known access point.
     * This method is triggered by the Connect to Bridge button but it can equally be used to automatically connect to a bridge.
     */
    fun connectToLastKnownAccessPoint(): Boolean {
        val username = username
        val lastIpAddress = lastIpAddress

        if (username == null || lastIpAddress == null) {
            println("Missing Last Username or Last IP.  Last known connection not found.")
            return false
        }
        val accessPoint = PHAccessPoint()
        accessPoint.ipAddress = lastIpAddress
        accessPoint.username = username
        sdk.connect(accessPoint)
        return true
    }
}
