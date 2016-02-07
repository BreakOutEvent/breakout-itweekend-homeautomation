package org.breakout.services

import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHBridgeSearchManager
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.model.*
import java.util.Random

class Controller {
    private var phHueSDK: PHHueSDK? = null

    var listener: PHSDKListener = object : PHSDKListener {

        override fun onAccessPointsFound(accessPointsList: List<PHAccessPoint>) {
            accessPointsList.forEach { list ->
                println(list.macAddress)
                phHueSDK!!.startPushlinkAuthentication(list)
            }

        }

        override fun onAuthenticationRequired(accessPoint: PHAccessPoint) {
            phHueSDK!!.startPushlinkAuthentication(accessPoint)
            println("Push connect button on hue")
        }

        override fun onBridgeConnected(bridge: PHBridge, username: String) {
            phHueSDK!!.selectedBridge = bridge
            phHueSDK!!.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL.toLong())
            val lastIpAddress = bridge.resourceCache.bridgeConfiguration.ipAddress
            Controller.username = username
            Controller.lastIpAddress = lastIpAddress
        }

        override fun onCacheUpdated(arg0: List<Int>, arg1: PHBridge) {
        }

        override fun onConnectionLost(arg0: PHAccessPoint) {
        }

        override fun onConnectionResumed(arg0: PHBridge) {
        }

        override fun onError(code: Int, message: String) {
            println("Error $code: $message")
        }

        override fun onParsingErrors(parsingErrorsList: List<PHHueParsingError>) {
            for (parsingError in parsingErrorsList) {
                println("ParsingError : " + parsingError.message)
            }
        }
    }

    init {
        this.phHueSDK = PHHueSDK.getInstance()
        phHueSDK!!.notificationManager.registerSDKListener(listener)
    }

    fun findBridges() {
        phHueSDK = PHHueSDK.getInstance()
        val sm = phHueSDK!!.getSDKService(PHHueSDK.SEARCH_BRIDGE) as PHBridgeSearchManager
        sm.search(true, true)
    }

    fun randomLights() {
        val bridge = phHueSDK!!.selectedBridge
        val cache = bridge.resourceCache

        val allLights = cache.allLights
        val rand = Random()

        for (light in allLights) {
            val lightState = PHLightState()
            lightState.isOn = true
            lightState.hue = 0
            lightState.brightness = 254
            bridge.updateLightState(light, lightState) // If no bridge response is required then use this
            // simpler form.
        }
    }

    fun lightsOff() {
        val bridge = phHueSDK!!.selectedBridge
        val cache = bridge.resourceCache
        val allLights = cache.allLights

        allLights.forEach { light ->
            val lightState = PHLightState()
            lightState.isOn = false
            bridge.updateLightState(light, lightState) // If no bridge response is required then use this
        }
    }


    /**
     * Connect to the last known access point.
     * This method is triggered by the Connect to Bridge button but it can equally be used to automatically connect to a bridge.
     */
    fun connectToLastKnownAccessPoint(): Boolean {
        val username = Controller.username
        val lastIpAddress = Controller.lastIpAddress

        if (username == null || lastIpAddress == null) {
            println("Missing Last Username or Last IP.  Last known connection not found.")
            return false
        }
        val accessPoint = PHAccessPoint()
        accessPoint.ipAddress = lastIpAddress
        accessPoint.username = username
        phHueSDK!!.connect(accessPoint)
        return true
    }

    fun enableFindBridgesButton() {
        //        desktopView.getFindBridgesButton().setEnabled(true);
    }

    fun showProgressBar() {
        //        desktopView.getFindingBridgeProgressBar().setVisible(true);
    }

    companion object {

        private val MAX_HUE = 65535

        private var username: String? = null
        private var lastIpAddress: String? = null
    }
}
