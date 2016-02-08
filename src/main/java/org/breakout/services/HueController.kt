package org.breakout.services

import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHBridgeSearchManager
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHHueParsingError
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState
import org.slf4j.LoggerFactory
import java.util.*
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.timerTask


var username: String? = null
var lastIpAddress: String? = null

const val MAX_HUE = 65535

class HueListener(val sdk: PHHueSDK) : PHSDKListener {

    private val logger = LoggerFactory.getLogger(HueListener::class.java)

    override fun onAccessPointsFound(accessPointsList: List<PHAccessPoint>) {
        accessPointsList.forEach { list ->
            logger.info("Found bridge with ID ${list.bridgeId} at ${list.ipAddress}")
            logger.info("Push connect button to start")
            sdk.startPushlinkAuthentication(list)
        }
    }

    override fun onAuthenticationRequired(accessPoint: PHAccessPoint) {
        sdk.startPushlinkAuthentication(accessPoint)
        println("Push connect button on hue")
    }

    override fun onBridgeConnected(bridge: PHBridge, username: String) {
        sdk.selectedBridge = bridge
        sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL.toLong())
        lastIpAddress = bridge.resourceCache.bridgeConfiguration.ipAddress
        logger.info("Connected to bridge")
    }

    override fun onCacheUpdated(arg0: List<Int>, arg1: PHBridge) {
    }

    override fun onConnectionLost(accessPoint: PHAccessPoint) {

    }

    override fun onConnectionResumed(arg0: PHBridge) {
    }

    override fun onError(code: Int, message: String) {
        logger.error("Code $code: $message")
    }

    override fun onParsingErrors(parsingErrorsList: List<PHHueParsingError>) {
        for (parsingError in parsingErrorsList) {
            logger.error("ParsingError : " + parsingError.message)
        }
    }
}

class HueController {

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
            pair.first.updateLightState(pair.second, Alarm)
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
            pair.first.updateLightState(pair.second, Off)
        }
    }

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
