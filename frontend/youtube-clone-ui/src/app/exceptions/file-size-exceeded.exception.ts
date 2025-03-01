import { GenericException } from './generic.exception';

export class FileSizeExceededException implements GenericException {
  name: string;
  message = 'Exception occurred!';
  severity = 'error';
  messageContent = 'Uploading file size limit exceeded!';

  public GenericException() {
    this.name = FileSizeExceededException.name;
  }
}
