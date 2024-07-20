import { Component, Input, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { FileUploadHandlerEvent } from 'primeng/fileupload';
import { MessageService } from 'primeng/api';
import { VideoService } from '../../services/video/video.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { VideoDataService } from '../../data/video-data.service';
import { UploadVideoResponse } from '../../dto/upload-video-response';
import { VideoDto, VideoStatus } from '../../dto/video-dto';

@Component({
  selector: 'app-edit-video-metadata',
  templateUrl: './edit-video-metadata.component.html',
  styleUrl: './edit-video-metadata.component.css',
  providers: [MessageService],
})
export class EditVideoMetadataComponent implements OnInit {
  thumbnailUrl: string = '';
  videoUrl: string =
    'https://youtube-clone-avk.s3.ap-south-1.amazonaws.com/6bce94ea-4113-40f9-990f-73619037c122.mp4';
  videoId: string = '';
  loading: boolean = false;
  fileUploaded: boolean = false;
  private formData: FormData | undefined;
  editVideoDetailsForm!: FormGroup;
  videoStatus!: VideoStatus[];
  uploadVideoResponse!: UploadVideoResponse;
  showCancel: boolean = true;

  isApiUploadingImage = false;
  isApiUploadingDetails = false;

  constructor(
    private videoService: VideoService,
    private messageService: MessageService,
    private fb: FormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private dataService: VideoDataService
  ) {
    this.editVideoDetailsForm = this.fb.group({
      id: [''],
      title: ['', Validators.required],
      description: ['', Validators.required],
      tags: ['', Validators.required],
      videoStatus: ['', Validators.required],
      videoUrl: [''],
      thumbnailUrl: [''],
    });

    this.videoStatus = [
      { name: 'Public', code: 'PUBLIC' },
      { name: 'Private', code: 'PRIVATE' },
      { name: 'Unlisted', code: 'UNLISTED' },
    ];

    const videoId = this.activatedRoute.snapshot.params['videoId'];
    this.videoId = videoId ? videoId : '';
    this.uploadVideoResponse = this.dataService.getVideoResponse();
    if (this.uploadVideoResponse) {
      this.videoUrl = this.uploadVideoResponse.videoUrl;
    }
  }

  ngOnInit(): void {}

  onUpload(event: FileUploadHandlerEvent) {
    for (let file of event.files) {
      this.fileUploaded = true;
      this.formData = new FormData();
      this.formData.append('file', file, file.name);
    }
    this.uploadImage();
  }
  onSelectFile() {
    this.fileUploaded = true;
  }

  onClear() {
    this.fileUploaded = false;
  }

  uploadImage() {
    this.thumbnailUrl = '';
    this.showCancel = false;
    this.isApiUploadingImage = true;
    if (this.formData) {
      this.videoService
        .uploadThumnail(this.formData, this.videoId)
        .subscribe((response) => {
          this.thumbnailUrl = response.videoUrl;
          this.isApiUploadingImage = false;
          this.successMessage('Thumbnail uploaded!');
        });
    }
  }

  successMessage(message: string) {
    this.messageService.add({
      severity: 'success',
      summary: 'Uploaded',
      detail: message,
      life: 3000,
    });
  }

  submit() {
    this.updateFormDetails();
    const requestBody: string = this.convertFormValuesToResponseBody();
    this.isApiUploadingDetails = true;
    this.videoService.editVideoMeta(requestBody).subscribe((response) => {
      this.successMessage('Video Uploaded!');
      this.isApiUploadingDetails = false;
      const navigateUrl = `/watch/${response.id}`;
      this.router.navigateByUrl(navigateUrl);
    });
  }

  updateFormDetails() {
    let video: VideoStatus =
      this.editVideoDetailsForm.get('videoStatus')?.value;
    this.editVideoDetailsForm.patchValue({
      id: this.videoId,
      videoUrl: this.videoUrl,
      thumbnailUrl: this.thumbnailUrl,
      videoStatus: video.code,
    });
  }

  convertFormValuesToResponseBody() {
    const str = this.editVideoDetailsForm.value;
    const videoDto = JSON.stringify(str);
    return videoDto;
  }
}
