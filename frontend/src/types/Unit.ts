export interface ConvertedUnit {
  originalValue: number;
  originalUnit: string;
  originalUnitName: string;
  convertedValue: number;
  convertedUnit: string;
  convertedUnitName: string;
  formattedOriginalValue: string;
  formattedConvertedValue: string;
}

export interface ConvertiableUnit {
  id: string;
  symbol: string;
  name: string;
  system: string;
}
