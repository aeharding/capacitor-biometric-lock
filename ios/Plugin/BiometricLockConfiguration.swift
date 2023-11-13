//
//  BiometricLockConfiguration.swift
//  Plugin
//
//  Created by Alexander Harding on 11/12/23.
//  Copyright © 2023 Max Lynch. All rights reserved.
//

import Foundation

let BIOMETRIC_CONFIG_KEY = "__capacitor_biometricLockConfiguration"

public struct BiometricLockConfiguration: Codable {
    var enabled = false
    var timeoutInSeconds: Int = 0
    var appName: String = ""
    var retryButtonColor: String = "000000"
}

// Extension to handle UserDefaults operations for BiometricLockConfig
extension BiometricLockConfiguration {

    // Function to save the BiometricLockConfig to UserDefaults
    func save() {
        let data = try? JSONEncoder().encode(self)
        UserDefaults.standard.set(data, forKey: BIOMETRIC_CONFIG_KEY)
        UserDefaults.standard.synchronize()
    }

    // Function to retrieve the BiometricLockConfig from UserDefaults
    static func load() -> BiometricLockConfiguration? {
        if let data = UserDefaults.standard.data(forKey: BIOMETRIC_CONFIG_KEY) {
            return try? JSONDecoder().decode(BiometricLockConfiguration.self, from: data)
        }
        return nil
    }
}
