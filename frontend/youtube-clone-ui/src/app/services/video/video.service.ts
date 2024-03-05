import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { Observable } from 'rxjs';
import { UploadVideoResponse } from '../../dto/upload-video-response';
import { FormGroup } from '@angular/forms';
import { CommentDTO, VideoDto } from '../../dto/video-dto';
@Injectable({
  providedIn: 'root',
})
export class VideoService {
  private UPLOAD_URL: string = '/upload';

  private ThUMBNAIL_URL: string = '/thumbnail';

  private EDIT_META_DATA_URL: string = '/editMetadata';

  private ALL_VIDEOS_URL: string = '/all';

  private SUGGESTED_VIDEO_URL: string = '/suggestion-videos';

  private GET_VIDEDO_URL: string = '/watch/';

  private GET_SHORT_VIDEDO_URL: string = '/short-video';

  private SUBSCRIPTIONS_VIDEO_URL: String = '/subscription-videos';

  constructor(private http: HttpClient) {}

  getVideoBaseUrl(): string {
    return AppSettings.HOST + AppSettings.SERVICE_NAME + '/video';
  }

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

  getVideos(): Observable<VideoDto[]> {
    return this.http.get<VideoDto[]>(
      this.getVideoBaseUrl() + this.ALL_VIDEOS_URL
    );
  }

  getSuggestedVideos(page: number, size: number): Observable<VideoDto[]> {
    let params = new HttpParams().set('page', page).set('size', size);

    const httpOptions = {
      params: params,
    };

    return this.http.get<VideoDto[]>(
      this.getVideoBaseUrl() + this.SUGGESTED_VIDEO_URL,
      httpOptions
    );
  }

  getVideo(videoId: string): Observable<VideoDto> {
    return this.http.get<VideoDto>(
      this.getVideoBaseUrl() + this.GET_VIDEDO_URL + videoId
    );
  }

  likeVideo(videoId: string): Observable<VideoDto> {
    return this.http.put<VideoDto>(
      this.getVideoBaseUrl() + this.GET_VIDEDO_URL + videoId + '/like',
      null
    );
  }

  dislikeVideo(videoId: string): Observable<VideoDto> {
    return this.http.put<VideoDto>(
      this.getVideoBaseUrl() + this.GET_VIDEDO_URL + videoId + '/dislike',
      null
    );
  }

  getShortsVideo(): Observable<VideoDto[]> {
    return this.http.get<VideoDto[]>(
      this.getVideoBaseUrl() + this.GET_SHORT_VIDEDO_URL
    );
  }

  getSubscriptionVideos(): Observable<VideoDto[]> {
    return this.http.post<VideoDto[]>(
      this.getVideoBaseUrl() + this.SUBSCRIPTIONS_VIDEO_URL,
      null
    );
  }
}
