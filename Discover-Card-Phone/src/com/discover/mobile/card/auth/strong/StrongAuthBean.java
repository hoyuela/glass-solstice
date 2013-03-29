/**
 * 
 */
package com.discover.mobile.card.auth.strong;

/**
 * This class is bean class to store StrongAuth data
 * 
 * @author Ravi Bhojani
 * 
 */
public class StrongAuthBean {

    private String deviceId;
    private String simId;
    private String subscriberId;

    /**
     * This will return subscriberId
     * 
     * @return subscriberId
     */
    public String getSubscriberId() {
        return subscriberId;
    }

    /**
     * This will set subscriberId
     * 
     * @param subscriberId
     */
    public void setSubscriberId(final String subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * This will get sim id
     * 
     * @return simId
     */
    public String getSimId() {
        return simId;
    }

    /**
     * This method will set simId
     * 
     * @param simId
     */
    public void setSimId(final String simId) {
        this.simId = simId;
    }

    /**
     * This will get deviceID
     * 
     * @return deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * This will set DeviceId
     * 
     * @param deviceId
     */
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }
}
