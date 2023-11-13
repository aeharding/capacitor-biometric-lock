export interface BiometricLockPlugin {
  /**
   * Set plugin configuration
   *
   * @since 1.0.0
   */
  configure(options?: BiometricLockConfiguration): Promise<void>;

  /**
   * Get plugin configuration
   *
   * @since 1.0.0
   */
  getConfiguration(): Promise<BiometricLockConfiguration>;

  /**
   * Get primary biometric method
   *
   * @since 1.0.0
   */
  getBiometricMethod(): Promise<BiometricMethodResult>;
}

/**
 * Persisted configuration of the plugin
 */
export interface BiometricLockConfiguration {
  enabled: boolean;

  timeoutInSeconds?: number;

  appName?: string;

  /**
   * Hex color code e.g. "00ffee"
   */
  retryButtonColor?: string;
}

export interface BiometricMethodResult {
  biometricMethod: BiometricMethod;
}

/**
 * Primary biometric method
 */
export enum BiometricMethod {
  /**
   * No biometry is available
   */
  none = 0,

  /**
   * iOS Touch ID is available
   */
  touchId = 1,

  /**
   * iOS Face ID is available
   */
  faceId = 2,

  /**
   * Android fingerprint authentication is available
   */
  fingerprintAuthentication = 3,

  /**
   * Android face authentication is available
   */
  faceAuthentication = 4,

  /**
   * Android iris authentication is available
   */
  irisAuthentication = 5,
}
