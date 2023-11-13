import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(BiometricLockPlugin)
public class BiometricLockPlugin: CAPPlugin {
    private var implementation: BiometricLock?

    override public func load() {
        self.implementation = BiometricLock(plugin: self)

        NotificationCenter.default.addObserver(self, selector: #selector(self.handleDidBecomeActiveNotification),
                                               name: UIApplication.didBecomeActiveNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.handleDidEnterBackgroundNotification),
                                               name: UIApplication.didEnterBackgroundNotification, object: nil)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    @objc private func handleDidBecomeActiveNotification() {
        implementation?.handleDidBecomeActiveNotification()
    }

    @objc private func handleDidEnterBackgroundNotification() {
        implementation?.handleDidEnterBackgroundNotification()
    }

    @objc func configure(_ call: CAPPluginCall) {
        var config = BiometricLockConfiguration()
        config.timeoutInSeconds = call.getInt("timeoutInSeconds", config.timeoutInSeconds)
        config.enabled = call.getBool("enabled", config.enabled)
        config.appName = call.getString("appName", config.appName)
        config.retryButtonColor = call.getString("retryButtonColor", config.retryButtonColor)

        implementation?.configure(with: config)

        getConfiguration(call)
    }

    @objc func getConfiguration(_ call: CAPPluginCall) {
        let config = implementation?.getConfiguration() ?? BiometricLockConfiguration()

        call.resolve([
            "enabled": config.enabled,
            "timeoutInSeconds": config.timeoutInSeconds,
            "appName": config.appName,
            "retryButtonColor": config.retryButtonColor
        ])
    }

    @objc func getBiometricMethod(_ call: CAPPluginCall) {
        let method = implementation?.getBiometricMethod() ?? 0
        call.resolve(["biometricMethod": method])
    }
}
