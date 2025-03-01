import { ErrorHandler, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';
import { GenericException } from '../exceptions/generic.exception';

@Injectable({
  providedIn: 'root',
})
export class GlobalerrorhandlerService implements ErrorHandler {
  constructor(private messageService: MessageService) {}

  handleError(error: GenericException): void {
    console.log(error);
    this.messageService.add({
      severity: error.severity,
      summary: error.message,
      detail: error.messageContent,
    });
  }
}
