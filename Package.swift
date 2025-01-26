// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorBiometricLock",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorBiometricLock",
            targets: ["BiometricLockPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "BiometricLockPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BiometricLockPlugin"),
        .testTarget(
            name: "BiometricLockPluginTests",
            dependencies: ["BiometricLockPlugin"],
            path: "ios/Tests/BiometricLockPluginTests")
    ]
)