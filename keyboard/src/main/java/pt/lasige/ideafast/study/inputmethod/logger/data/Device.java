package pt.lasige.ideafast.study.inputmethod.logger.data;

public class Device {
    String app;
    String brand;
    String device;
    String model;
    String release;
    int sdk;
    String loggedInAt;

    public Device(String app, String brand, String device, String model, String release, int sdk, String loggedInAt) {
        this.app = app;
        this.brand = brand;
        this.device = device;
        this.model = model;
        this.release = release;
        this.sdk = sdk;
        this.loggedInAt = loggedInAt;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public int getSdk() {
        return sdk;
    }

    public void setSdk(int sdk) {
        this.sdk = sdk;
    }

    public String getLoggedInAt() {
        return loggedInAt;
    }

    public void setLoggedInAt(String loggedInAt) {
        this.loggedInAt = loggedInAt;
    }
}
