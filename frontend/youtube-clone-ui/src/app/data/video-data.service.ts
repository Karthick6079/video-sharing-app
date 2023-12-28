import { Injectable } from '@angular/core';
import { UploadVideoResponse } from '../dto/upload-video-response';

@Injectable({
  providedIn: 'root',
})
export class VideoDataService {
  uploadedVideoResponse!: UploadVideoResponse;

  constructor() {}

  setVideoUploadResonse(uploadedVideoResponse: UploadVideoResponse) {
    this.uploadedVideoResponse = uploadedVideoResponse;
  }

  getVideoResponse(): UploadVideoResponse {
    return this.uploadedVideoResponse;
  }
}
