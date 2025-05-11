import { Component, ViewEncapsulation } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { MessageService } from 'primeng/api';
import { FileUploadHandlerEvent } from 'primeng/fileupload';
import { Router, ActivatedRoute, UrlSegment } from '@angular/router';
import { UploadVideoResponse } from '../../dto/upload-video-response';
import { VideoDataService } from '../../data/video-data.service';
import { UserService } from '../../services/user/user.service';
import { UploadVideoService } from '../../services/uploadvideo/uploadvideo.service';
import { firstValueFrom } from 'rxjs';

interface UploadEvent {
  originalEvent: Event;
  files: File[];
}

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css',
  encapsulation: ViewEncapsulation.Emulated,
  providers: [MessageService],
})
export class UploadComponent {
  videoId!: string;
  videoUrl!: string;
  isApiUploading = false;

  fileUploaded: boolean = false;
  showCancel: boolean = true;

  uploadedFiles: any[] = [];

  constructor(
    private videoService: VideoService,
    private messageService: MessageService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private dataService: VideoDataService,
    private userService: UserService,
    private uploadVideoService: UploadVideoService
  ) {}

  private formData: FormData | undefined;

  onUpload(event: FileUploadHandlerEvent) {
    for (let file of event.files) {
      this.uploadedFiles.push(file);
      this.fileUploaded = true;
      this.showCancel = false;
      this.isApiUploading = true;
      this.onFileSelected(file);
      // this.formData = new FormData();
      // this.formData.append('file', file, file.name);
    }

    // this.uploadVideo();
  }

  onSelectFile() {
    this.fileUploaded = true;
  }

  onClear() {
    this.fileUploaded = false;
  }

  uploadVideo() {
    this.showCancel = false;
    this.isApiUploading = true;
    if (this.formData) {
      this.videoService
        .uploadVideo(this.formData)
        .subscribe((response: UploadVideoResponse) => {
          this.videoId = response.videoId;
          this.dataService.setVideoUploadResonse(response);
          this.isApiUploading = false;
          this.userService.studioStepperIndex.next(1);
          this.successMessage();
          this.navigateToEditVideoMetaData();
        });
    }
  }

  navigateToEditVideoMetaData() {
    this.router
      .navigate(['../edit-video-info', this.videoId], {
        relativeTo: this.activatedRoute,
      })
      .then((value) => {});
  }

  successMessage() {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail: 'File uploaded',
    });
  }

  navigateToNext() {
    this.videoId = '05ef9cf2-d5e1-4772-835b-85dc3c4a5cf5';
    this.navigateToEditVideoMetaData();
  }

  async onFileSelected(file: File) {
    if (!file) return;

    const partSize = 10 * 1024 * 1024; // 10MB
    const numParts = Math.ceil(file.size / partSize);

    console.log(file.name)

    const { key, uploadId } = await firstValueFrom(this.uploadVideoService.initiateUpload(file.name))

    console.log("The multipart upload initiated")

    const parts: { partNumber: number; entityTag: string }[] = [];

    for (let part = 0; part < numParts; part++) {
      const start = part * partSize;
      const end = Math.min(start + partSize, file.size);
      console.log(start, end);
      const blobPart = file.slice(start, end);
      const partNumber = part + 1;

      const {url} = await firstValueFrom (this.uploadVideoService
        .getPresignedUrl(key, uploadId, partNumber))
        
      console.log('Presigned URL:', url);
  

      const response = await fetch(url, {
        method: 'PUT',
        body: blobPart
      });

      if (!response.ok) {
        throw new Error(`Upload failed for part ${partNumber}`);
      }

      const entityTag = response.headers.get('ETag')!.replace(/"/g, '');
      parts.push({ partNumber, entityTag });
    }

    console.log("parts: ", parts)

    const response:UploadVideoResponse =  await firstValueFrom(this.uploadVideoService.completeUpload(key, uploadId, parts));
    this.handleResponse(response)
    console.log('Upload complete!');
  }

  handleResponse(response: UploadVideoResponse){
    this.videoId = response.videoId;
    this.dataService.setVideoUploadResonse(response);
    this.isApiUploading = false;
    this.userService.studioStepperIndex.next(1);
    this.successMessage();
    this.navigateToEditVideoMetaData();
  }
}
