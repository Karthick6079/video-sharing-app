import { Component, ViewEncapsulation } from '@angular/core';
import { NgxFileDropEntry } from 'ngx-file-drop';
import { VideoService } from '../../services/video/video.service';
import { MessageService } from 'primeng/api';
import { FileUploadHandlerEvent } from 'primeng/fileupload';

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

  fileUploaded: boolean = false;

  uploadedFiles: any[] = [];

  constructor(
    private videoService: VideoService,
    private messageService: MessageService
  ) {}

  private formData: FormData | undefined;

  onUpload(event: FileUploadHandlerEvent) {
    console.log(event);
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
    if (this.formData) {
      this.videoService.uploadVideo(this.formData).subscribe((response) => {
        this.videoId = response.videoId;
        this.videoUrl = response.videoUrl;
        this.successMessage();
      });
    }
  }

  successMessage() {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail: 'File uploaded',
    });
  }
}
