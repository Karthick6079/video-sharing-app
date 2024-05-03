import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppSettings } from '../../constants/AppSettings';
import { CommentDTO } from '../../dto/video-dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private VIDEO_URL: string = '/watch/';

  constructor(private httpclient: HttpClient) {}

  getVideoBaseUrl(): string {
    return AppSettings.HOST + AppSettings.SERVICE_NAME + '/video';
  }

  addComment(videoId: string, commentBody: any) {
    return this.httpclient.post<CommentDTO>(
      this.getVideoBaseUrl() + this.VIDEO_URL + videoId + '/comment',
      commentBody
    );
  }

  getComments(
    videoId: string,
    page: number,
    size: number
  ): Observable<Record<string, Object>> {
    let params = new HttpParams().set('page', page).set('size', size);

    const httpOptions = {
      params: params,
    };
    return this.httpclient.get<Record<string, Object>>(
      this.getVideoBaseUrl() + this.VIDEO_URL + videoId + '/comments',
      httpOptions
    );
  }
}
