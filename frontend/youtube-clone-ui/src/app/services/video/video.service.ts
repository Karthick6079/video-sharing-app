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
  private UPLOAD_URL: string = '/upload';

  private ThUMBNAIL_URL: string = '/thumbnail';

  private EDIT_META_DATA_URL: string = '/editMetadata';

  private ALL_VIDEOS_URL: string = '/all';

  private GET_VIDEDO_URL: string = '/watch/';

  constructor(private http: HttpClient) {}

  uploadVideo(formData: FormData): Observable<UploadVideoResponse> {
    return this.http.post<UploadVideoResponse>(
      this.getVideoBaseUrl() + this.UPLOAD_URL,
      formData
    );
  }

  uploadThumnail(
    formData: FormData,
    videoId: string
  ): Observable<UploadVideoResponse> {
    formData.append('videoId', videoId);
    return this.http.post<UploadVideoResponse>(
      this.getVideoBaseUrl() + this.ThUMBNAIL_URL,
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
      this.getVideoBaseUrl() + this.EDIT_META_DATA_URL,
      requestBody,
      httpOptions
    );
  }

  getVideoBaseUrl(): string {
    return AppSettings.HOST + AppSettings.SERVICE_NAME + '/video';
  }

  getVideos(): Observable<VideoDto[]> {
    return this.http.get<VideoDto[]>(
      this.getVideoBaseUrl() + this.ALL_VIDEOS_URL
    );
  }

  getVideo(videoId: string): Observable<VideoDto> {
    return this.http.get<VideoDto>(
      this.getVideoBaseUrl() + this.GET_VIDEDO_URL + videoId
    );
  }
}
