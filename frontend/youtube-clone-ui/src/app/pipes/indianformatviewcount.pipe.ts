import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  standalone: true,
  name: 'indianFormatViewCount',
})
export class IndianFormatViewCount implements PipeTransform {
  private thousand: number = 1000;
  private lakh: number = 100000;
  private crore: number = 10000000;

  transform(value: number): string {
    let result;
    if (value) {
      if (value >= this.thousand && value < this.lakh) {
        result = value / this.thousand;
        return this.addStringToNumber(result, 'K');
      } else if (value >= this.lakh && value < this.crore) {
        result = value / this.lakh;
        return this.addStringToNumber(result, ' lakh');
      } else if (value >= this.crore) {
        result = value / this.crore;
        return this.addStringToNumber(result, ' crore');
      } else {
        result = value;
        return result + " views";
      }
    }

    return value;
  }

  addStringToNumber(result: number, type: string): string {
    let res: string = result.toPrecision(2);
    return res + type + ' views';
  }
}
