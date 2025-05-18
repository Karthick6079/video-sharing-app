import { GenericException } from './generic.exception';

export class AWSUploadException implements GenericException {
  name: string;
  message = 'Error occurred!';
  severity = 'error';
  messageContent =
    'Exception occured during the file upload. Please try again!';

  public GenericException() {
    this.name = AWSUploadException.name;
  }
}
