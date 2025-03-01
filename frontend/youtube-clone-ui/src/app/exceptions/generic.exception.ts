export class GenericException implements Error {
  name: string;
  message = 'Exception occurred!';
  severity = 'error';
  messageContent = 'Unexcepted error occurred. Please reload the page.';

  public GenericException() {
    this.name = GenericException.name;
  }
}
