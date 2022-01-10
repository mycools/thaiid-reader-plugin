export interface ThaiIDReaderPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
