package org.breakout.services

import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHHueParsingError
import org.slf4j.LoggerFactory

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

    override fun onConnectionResumed(bridge: PHBridge) {

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
