import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-edit-video-metadata',
  templateUrl: './edit-video-metadata.component.html',
  styleUrl: './edit-video-metadata.component.css',
})
export class EditVideoMetadataComponent implements OnInit {
  editVideoForm!: FormGroup;
  formGroup: FormGroup | undefined;

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      text: new FormControl<string | null>(null),
      title: new FormControl<string | null>(null),
      description: new FormControl<string | null>(null),
      tags: new FormControl<string | null>(null),
      videoStatus: new FormControl<string | null>(null),
    });

    this.editVideoForm = new FormGroup({
      title: new FormControl(),
      description: new FormControl(),
      tags: new FormControl(),
      videoStatus: new FormControl(),
    });
  }
}
