import Foundation
import LocalAuthentication
import UIKit

enum AuthResult {
    case success
    case fail
}

@objc public class BiometricLock: NSObject {
    private let plugin: BiometricLockPlugin
    var config: BiometricLockConfiguration
    var inBackground = true
    var authResult: AuthResult?
    var dateSentToBackground: Date?

    init(plugin: BiometricLockPlugin) {
        self.plugin = plugin
        self.config = BiometricLockConfiguration.load() ?? BiometricLockConfiguration()

        super.init()

        showPrivacyProtectionWindowIfNeeded()
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
      let context = LAContext()
      var error: NSError?
      let available = context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error)

      if !available {
          return 0;
      }

      return context.biometryType.rawValue
    }

    @objc public func handleDidEnterBackgroundNotification() {
        guard config.enabled else { return }

        inBackground = true
        authResult = nil
        dateSentToBackground = Date()
        showPrivacyProtectionWindowIfNeeded()
    }

    @objc public func handleDidBecomeActiveNotification() {
        switch authResult {
        case .success:
            hidePrivacyProtectionWindow()
            dateSentToBackground = nil
            authResult = nil
        case .fail:
            guard inBackground else { return }
            inBackground = false;

            authenticate()
        case nil:
            guard inBackground else { return }
            inBackground = false

            if let dateSentToBackground = dateSentToBackground, isDateGreaterThanDurationAgo(date: dateSentToBackground, durationInSeconds: config.timeoutInSeconds) {
                hidePrivacyProtectionWindow()
                self.dateSentToBackground = nil
                authResult = nil
                return
            }

            dateSentToBackground = nil
            authenticate()
        }
    }

    private func authenticate() {
        guard config.enabled else { return }

        let context = LAContext()

        privacyProtectionVC?.onReset()

        context.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: "Authentication required to unlock the app") { success, error in
            if success {
                self.authResult = .success
            } else {
                self.authResult = .fail

                DispatchQueue.main.async {
                    self.privacyProtectionVC?.onFail()
                }
          }
        }
    }

    private var privacyProtectionWindow: UIWindow?
    private var privacyProtectionVC: PrivacyProtectionViewController?

    private func showPrivacyProtectionWindowIfNeeded() {
        guard config.enabled else { return }

        privacyProtectionWindow = UIWindow(frame: UIScreen.main.bounds)
        privacyProtectionVC = PrivacyProtectionViewController(appName: config.appName, retryButtonColor: config.retryButtonColor)
        privacyProtectionWindow?.rootViewController = privacyProtectionVC
        privacyProtectionWindow?.windowLevel = .alert + 1
        privacyProtectionWindow?.makeKeyAndVisible()
        // Set the onRetry closure to handle the retry action
        privacyProtectionVC?.onRetry = { [weak self] in
            self?.authenticate()
        }
    }

    private func hidePrivacyProtectionWindow() {
        guard self.privacyProtectionWindow != nil else { return }

        UIView.animate(withDuration: 0.3, animations: {
            self.privacyProtectionWindow?.alpha = 0.0
        }) { _ in
            self.privacyProtectionWindow?.isHidden = true
            self.privacyProtectionWindow?.alpha = 1.0 // Reset alpha for future presentations
            self.privacyProtectionWindow = nil
        }
    }
}

class PrivacyProtectionViewController: UIViewController {
    // Add this closure property to allow handling of the retry action externally
    var onRetry: (() -> Void)?
    let appName: String
    let retryButtonColor: String

    init(appName: String, retryButtonColor: String) {
        self.appName = appName
        self.retryButtonColor = retryButtonColor

        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // Add the blurred background
        view.addSubview(blurEffectView)
        view.sendSubviewToBack(blurEffectView)

        // Set up the unlock button
        configureUnlockButton()
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        // Update the frame of the blur effect view
        blurEffectView.frame = view.bounds

        unlockButton.translatesAutoresizingMaskIntoConstraints = false

        unlockButton.contentEdgeInsets = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 12)

        // Add constraints to make the width dynamic based on content
        NSLayoutConstraint.activate([
            unlockButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            unlockButton.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            unlockButton.widthAnchor.constraint(greaterThanOrEqualToConstant: 50), // Set a minimum width
            unlockButton.heightAnchor.constraint(equalToConstant: 45)
        ])
    }

    private lazy var blurEffectView: UIVisualEffectView = {
        let blurEffect = UIBlurEffect(style: .dark)
        let blurEffectView = UIVisualEffectView(effect: blurEffect)
        blurEffectView.frame = view.bounds
        return blurEffectView
    }()

    public func onFail() {
        unlockButton.isHidden = false
    }

    public func onReset() {
        unlockButton.isHidden = true
    }

    private func configureUnlockButton() {
        unlockButton.setTitle("Unlock \(appName)", for: .normal)
        unlockButton.titleLabel?.font = UIFont.preferredFont(forTextStyle: .body)
        unlockButton.setTitleColor(.white, for: .normal)
        unlockButton.backgroundColor = UIColor(hexString: retryButtonColor)
        unlockButton.layer.cornerRadius = 8
        unlockButton.isHidden = true

        view.addSubview(unlockButton)
    }

    private lazy var unlockButton: UIButton = {
        let unlockButton = UIButton(type: .system)
        unlockButton.addTarget(self, action: #selector(unlockButtonTapped), for: .touchUpInside)
        return unlockButton
    }()

    @objc private func unlockButtonTapped() {
        onRetry?()
    }
}


extension UIColor {
    convenience init?(hexString: String) {
        var formattedHexString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        formattedHexString = formattedHexString.replacingOccurrences(of: "#", with: "")

        var rgb: UInt64 = 0

        guard Scanner(string: formattedHexString).scanHexInt64(&rgb) else {
            return nil
        }

        self.init(
            red: CGFloat((rgb & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgb & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgb & 0x0000FF) / 255.0,
            alpha: 1.0
        )
    }
}

func isDateGreaterThanDurationAgo(date: Date, durationInSeconds: Int) -> Bool {
    let currentDate = Date()
    let targetDate = currentDate.addingTimeInterval(-TimeInterval(durationInSeconds))

    return date > targetDate
}
