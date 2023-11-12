import { WebPlugin } from '@capacitor/core';

import type { BiometricLockPlugin } from './definitions';

export class BiometricLockWeb extends WebPlugin implements BiometricLockPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
