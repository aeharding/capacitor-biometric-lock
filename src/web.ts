import { WebPlugin } from '@capacitor/core';

import type {
  BiometricLockConfiguration,
  BiometricLockPlugin,
  BiometricMethod,
} from './definitions';

export class BiometricLockWeb extends WebPlugin implements BiometricLockPlugin {
  async configure(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async getConfiguration(): Promise<BiometricLockConfiguration> {
    throw this.unimplemented('Not implemented on web.');
  }

  async getBiometricMethod(): Promise<BiometricMethod> {
    throw this.unimplemented('Not implemented on web.');
  }
}
