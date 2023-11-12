import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(BiometricLockPlugin)
public class BiometricLockPlugin: CAPPlugin {
    private let implementation = BiometricLock()

    @objc func configure(_ call: CAPPluginCall) {
        guard let options = call.options else {
            call.reject("Options are missing")
            return
        }

        do {
            let configData = try JSONSerialization.data(withJSONObject: options, options: [])
            let config = try JSONDecoder().decode(BiometricLockConfiguration.self, from: configData)

            // Implement the configuration logic here using BiometricLockConfiguration
            implementation.configure(with: config)
            call.resolve()
        } catch {
            call.reject("Error decoding options: \(error.localizedDescription)")
        }
    }

    @objc func getConfiguration(_ call: CAPPluginCall) {
        let config = implementation.getConfiguration()
        call.resolve(["configuration": config])
    }

    @objc func getBiometricMethod(_ call: CAPPluginCall) {
        let method = implementation.getBiometricMethod()
        call.resolve(["biometricMethod": method])
    }
}
