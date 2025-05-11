import { ErrorHandler, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})
export class GlobalerrorhandlerService implements ErrorHandler {
  constructor(private messageService: MessageService) {}

  handleError(error: any): void {
    console.log("error", error);
   
    this.messageService.add({
      severity: error.severity ? error.severity: 'error',
      summary: error.message ? error.message: 'Error',
      detail: error.messageContent ? error.messageContent: "Error occurred!!",
      sticky: true,
    });
  }
}
