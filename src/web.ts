import { WebPlugin } from '@capacitor/core';
import type { ThaiIDReaderPlugin } from './definitions';
export class ThaiIDReaderWeb extends WebPlugin implements ThaiIDReaderPlugin {
  async reader(): Promise<{ value: string }> {
    throw this.unimplemented('reader() Not implemented on web.');
  }
  async findReader(): Promise<{ value: string }> {
    throw this.unimplemented('findReader() Not implemented on web.');
  }
  async getNameReader():  Promise<{ value: string }> {
    throw this.unimplemented('getNameReader() Not implemented on web.');
  }
  async powerOn():  Promise<{ value: string }> {
    throw this.unimplemented('powerOn() Not implemented on web.');
  }
}
