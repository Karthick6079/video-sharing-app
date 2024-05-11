import { Component, ViewEncapsulation } from '@angular/core';
import { VideoService } from '../../services/video/video.service';
import { MessageService } from 'primeng/api';
import { FileUploadHandlerEvent } from 'primeng/fileupload';
import { Router, ActivatedRoute } from '@angular/router';
import { UploadVideoResponse } from '../../dto/upload-video-response';
import { VideoDataService } from '../../data/video-data.service';
import { UserService } from '../../services/user/user.service';

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
    private userService: UserService
  ) {}

  private formData: FormData | undefined;

  onUpload(event: FileUploadHandlerEvent) {
    for (let file of event.files) {
      this.uploadedFiles.push(file);
      this.fileUploaded = true;
      this.formData = new FormData();
      this.formData.append('file', file, file.name);
    }

    this.uploadVideo();
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
      .then((value) => {
        console.log('Navigate successfull');
      });
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
}
