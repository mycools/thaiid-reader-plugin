import { WebPlugin } from '@capacitor/core';

import type { ThaiIDReaderPlugin } from './definitions';

export class ThaiIDReaderWeb extends WebPlugin implements ThaiIDReaderPlugin {
  async reader(options: { value: string }): Promise<{ value: string }> {
    console.log('rederWEB', options );
    return options;
  }
}
