import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { Observable } from 'rxjs';
import { UploadVideoResponse } from '../../dto/upload-video-response';
@Injectable({
  providedIn: 'root',
})
export class VideoService {
  private uploadUrl: String = '/upload';

  constructor(private http: HttpClient) {}

  uploadVideo(formData: FormData): Observable<UploadVideoResponse> {
    return this.http.post<UploadVideoResponse>(this.getUrl(), formData);
  }

  getUrl(): string {
    return (
      AppSettings.HOST + AppSettings.SERVICE_NAME + '/video' + this.uploadUrl
    );
  }
}
