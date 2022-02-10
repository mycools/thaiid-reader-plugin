export interface ThaiIDReaderPlugin {
  reader(): Promise<{ value: string }>;
  findReader(): Promise<{ value: string }>;
  getNameReader(): Promise<{ value: string }>;
  powerOn(): Promise<{ value: string }>;
  close(): Promise<{ value: string }>;
}
