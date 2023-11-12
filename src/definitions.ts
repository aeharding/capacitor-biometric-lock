export interface BiometricLockPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
