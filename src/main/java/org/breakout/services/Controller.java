package org.breakout.services;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Controller {

    private static final int MAX_HUE = 65535;
    private PHHueSDK phHueSDK;

    private static String username;
    private static String lastIpAddress;

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
            accessPointsList.forEach(list -> {
                System.out.println(list.getMacAddress());
                phHueSDK.startPushlinkAuthentication(list);
            });

        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            phHueSDK.startPushlinkAuthentication(accessPoint);
            System.out.println("Push connect button on hue");
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            Controller.username = username;
            Controller.lastIpAddress = lastIpAddress;
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {
            System.out.println(code + " " + message);
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError : parsingErrorsList) {
                System.out.println("ParsingError : " + parsingError.getMessage());
            }
        }
    };

    public Controller() {
        this.phHueSDK = PHHueSDK.getInstance();
        phHueSDK.getNotificationManager().registerSDKListener(listener);
    }

    public void findBridges() {
        phHueSDK = PHHueSDK.getInstance();
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    public PHSDKListener getListener() {
        return listener;
    }

    public void setListener(PHSDKListener listener) {
        this.listener = listener;
    }

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        List<PHLight> allLights = cache.getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(true);
            lightState.setHue(0);
            lightState.setBrightness(254);
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this
            // simpler form.
        }
    }

    public void lightsOff() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();
        List<PHLight> allLights = cache.getAllLights();

        allLights.forEach(light -> {
            PHLightState lightState = new PHLightState();
            lightState.setOn(false);
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this
        });
    }



    /**
     * Connect to the last known access point.
     * This method is triggered by the Connect to Bridge button but it can equally be used to automatically connect to a bridge.
     */
    public boolean connectToLastKnownAccessPoint() {
        String username = Controller.username;
        String lastIpAddress = Controller.lastIpAddress;

        if (username == null || lastIpAddress == null) {
            System.out.println("Missing Last Username or Last IP.  Last known connection not found.");
            return false;
        }
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        phHueSDK.connect(accessPoint);
        return true;
    }

    public void enableFindBridgesButton() {
//        desktopView.getFindBridgesButton().setEnabled(true);
    }

    public void showProgressBar() {
//        desktopView.getFindingBridgeProgressBar().setVisible(true);
    }
}
