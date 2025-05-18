export class GenericException implements Error {
  name: string;
  message = 'Error Occurred!';
  severity = 'error';
  messageContent = 'Unexcepted error occurred. Please try again!!';

  public GenericException() {
    this.name = GenericException.name;
  }
}
