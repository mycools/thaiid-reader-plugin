export interface ThaiIDReaderPlugin {
  reader(options: { value: string }): Promise<{ value: string }>;
}
