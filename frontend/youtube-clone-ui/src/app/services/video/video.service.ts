import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { Observable } from 'rxjs';
import { UploadVideoResponse } from '../../dto/upload-video-response';
import { FormGroup } from '@angular/forms';
import { VideoDto } from '../../dto/video-dto';
@Injectable({
  providedIn: 'root',
})
export class VideoService {
  private uploadUrl: string = '/upload';

  private thumbnailUrl: string = '/thumbnail';

  private editMetadataUrl: string = '/editMetadata';

  constructor(private http: HttpClient) {}

  uploadVideo(formData: FormData): Observable<UploadVideoResponse> {
    return this.http.post<UploadVideoResponse>(
      this.getUrl() + this.uploadUrl,
      formData
    );
  }

  uploadThumnail(
    formData: FormData,
    videoId: string
  ): Observable<UploadVideoResponse> {
    formData.append('videoId', videoId);
    return this.http.post<UploadVideoResponse>(
      this.getUrl() + this.thumbnailUrl,
      formData
    );
  }

  editVideoMeta(requestBody: string): Observable<VideoDto> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };

    return this.http.put<VideoDto>(
      this.getUrl() + this.editMetadataUrl,
      requestBody,
      httpOptions
    );
  }

  getUrl(): string {
    return AppSettings.HOST + AppSettings.SERVICE_NAME + '/video';
  }
}
