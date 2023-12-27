import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UploadComponent } from './components/upload/upload.component';
import { EditVideoMetadataComponent } from './components/edit-video-metadata/edit-video-metadata.component';
import { StudioComponent } from './components/studio/studio.component';
import { ReviewAndPublishComponent } from './components/review-and-publish/review-and-publish.component';

const routes: Routes = [
  {
    path: 'studio',
    component: StudioComponent,
    children: [
      {
        path: 'upload',
        component: UploadComponent,
      },
      {
        path: 'editMetadata',
        component: EditVideoMetadataComponent,
      },
      {
        path: 'review-and-publish',
        component: ReviewAndPublishComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
