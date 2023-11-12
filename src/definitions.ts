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
  getBiometricMethod(): Promise<BiometricMethod>;
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

/**
 * Primary biometric method
 */
export enum BiometricMethod {
  FaceID,
}
