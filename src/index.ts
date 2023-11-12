import { registerPlugin } from '@capacitor/core';

import type { BiometricLockPlugin } from './definitions';

const BiometricLock = registerPlugin<BiometricLockPlugin>('BiometricLock', {
  web: () => import('./web').then(m => new m.BiometricLockWeb()),
});

export * from './definitions';
export { BiometricLock };
