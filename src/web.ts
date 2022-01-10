import { WebPlugin } from '@capacitor/core';

import type { ThaiIDReaderPlugin } from './definitions';

export class ThaiIDReaderWeb extends WebPlugin implements ThaiIDReaderPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
