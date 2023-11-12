import Foundation

@objc public class BiometricLock: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
