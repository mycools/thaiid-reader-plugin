import { registerPlugin } from '@capacitor/core';

import type { ThaiIDReaderPlugin } from './definitions';

const ThaiIDReader = registerPlugin<ThaiIDReaderPlugin>('ThaiIDReader', {
  web: () => import('./web').then(m => new m.ThaiIDReaderWeb()),
});

export * from './definitions';
export { ThaiIDReader };
