import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'indianFormatViewCount',
})
export class IndianFormatViewCount implements PipeTransform {
  private thousand: number = 1000;
  private lakh: number = 100000;
  private crore: number = 10000000;

  transform(value: number, additionalText: string, showOnlyNumberIterals?: boolean): string {
    let result;
    if (value == null || value == undefined) {
      return ""
    }
    if(showOnlyNumberIterals){
      result  = Intl.NumberFormat('en-IN', { maximumFractionDigits: 2 }).format(+value);
      return result + additionalText;
    }

    if (value >= this.thousand && value < this.lakh) {
      result = value / this.thousand;
      return this.addStringToNumber(result, 'K', additionalText);
    } else if (value >= this.lakh && value < this.crore) {
      result = value / this.lakh;
      return this.addStringToNumber(result, ' lakh', additionalText);
    } else if (value >= this.crore) {
      result = value / this.crore;
      return this.addStringToNumber(result, ' crore', additionalText);
    } else {
      result = value;
      return result + additionalText;
    }
  }

  addStringToNumber(
    result: number,
    type: string,
    additionalText: string
  ): string {
    let res: string = result.toPrecision(2);
    return res + type + additionalText;
  }
}
