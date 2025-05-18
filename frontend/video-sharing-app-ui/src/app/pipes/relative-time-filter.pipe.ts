import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relativeTimeFilter'
})
export class RelativeTimeFilterPipe implements PipeTransform {

  transform(value: unknown, ...args: unknown[]): unknown {
    return null;
  }

}
