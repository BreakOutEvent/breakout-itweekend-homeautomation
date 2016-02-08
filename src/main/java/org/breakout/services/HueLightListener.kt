package org.breakout.services

import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.model.PHBridgeResource
import com.philips.lighting.model.PHHueError
import com.philips.lighting.model.PHLight

class HueLightListener: PHLightListener {
    override fun onSuccess() {
        throw UnsupportedOperationException()
    }

    override fun onStateUpdate(p0: MutableMap<String, String>?, p1: MutableList<PHHueError>?) {
        throw UnsupportedOperationException()
    }

    override fun onError(p0: Int, p1: String?) {
        throw UnsupportedOperationException()
    }

    override fun onReceivingLightDetails(p0: PHLight?) {
        throw UnsupportedOperationException()
    }

    override fun onReceivingLights(p0: MutableList<PHBridgeResource>?) {
        throw UnsupportedOperationException()
    }

    override fun onSearchComplete() {
        throw UnsupportedOperationException()
    }
}
