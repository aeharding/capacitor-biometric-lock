//
//  BiometricLockConfiguration.swift
//  Plugin
//
//  Created by Alexander Harding on 11/12/23.
//  Copyright © 2023 Max Lynch. All rights reserved.
//

import Foundation

public struct BiometricLockConfiguration: Codable {
    var enabled = true
    var timeoutInSeconds: Int = 0
    var appName: String = ""
    var retryButtonColor: String = "000000"
}

// Extension to handle UserDefaults operations for BiometricLockConfig
extension BiometricLockConfiguration {

    // Function to save the BiometricLockConfig to UserDefaults
    func save() {
        let data = try? JSONEncoder().encode(self)
        UserDefaults.standard.set(data, forKey: "biometricLockConfiguration")
        UserDefaults.standard.synchronize()
    }

    // Function to retrieve the BiometricLockConfig from UserDefaults
    static func load() -> BiometricLockConfiguration? {
        if let data = UserDefaults.standard.data(forKey: "biometricLockConfiguration") {
            return try? JSONDecoder().decode(BiometricLockConfiguration.self, from: data)
        }
        return nil
    }
}
//
//// Example usage
//var config = BiometricLockConfig()
//
//// Save the config to UserDefaults
//config.save()
//
//// Load the config from UserDefaults
//if let loadedConfig = BiometricLockConfig.load() {
//    print("Loaded BiometricLockConfig:")
//    print("Enabled: \(loadedConfig.enabled)")
//    print("Timeout: \(loadedConfig.timeoutInSeconds) seconds")
//    print("App Name: \(loadedConfig.appName)")
//    print("Retry Button Color: \(loadedConfig.retryButtonColor)")
//} else {
//    print("BiometricLockConfig not found in UserDefaults.")
//}
