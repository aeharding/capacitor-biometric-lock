import Foundation
//import LocalAuthentication

@objc public class BiometricLock: NSObject {
    var config: BiometricLockConfiguration

//    let biometryErrorCodeMap: [Int: String] = [
//      0: "",
//      LAError.appCancel.rawValue: "appCancel",
//      LAError.authenticationFailed.rawValue: "authenticationFailed",
//      LAError.invalidContext.rawValue: "invalidContext",
//      LAError.notInteractive.rawValue: "notInteractive",
//      LAError.passcodeNotSet.rawValue: "passcodeNotSet",
//      LAError.systemCancel.rawValue: "systemCancel",
//      LAError.userCancel.rawValue: "userCancel",
//      LAError.userFallback.rawValue: "userFallback",
//      LAError.biometryLockout.rawValue: "biometryLockout",
//      LAError.biometryNotAvailable.rawValue: "biometryNotAvailable",
//      LAError.biometryNotEnrolled.rawValue: "biometryNotEnrolled"
//    ]

    override init() {
        // Instantiate BiometricLockConfig when BiometricLock is created
        self.config = BiometricLockConfiguration()
        super.init()
    }

    // Method to configure BiometricLock with options
    func configure(with options: BiometricLockConfiguration) {
        // Implement the logic to configure BiometricLock with the provided options
        self.config = options
        options.save()
    }

    // Method to get the current configuration
    func getConfiguration() -> BiometricLockConfiguration {
        // Implement the logic to retrieve and return the current configuration
        return config
    }

    // Method to get the primary biometric method
    func getBiometricMethod() -> Int {
        return 0;
//      let context = LAContext()
//      var error: NSError?
//      let available = context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error)
//
//      if !available {
//          return 0;
//      }
//
//      return context.biometryType.rawValue
    }
}
