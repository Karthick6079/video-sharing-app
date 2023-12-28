import { NgModule } from '@angular/core';
import {
  RouterModule,
  Routes,
  provideRouter,
  withComponentInputBinding,
} from '@angular/router';
import { UploadComponent } from './components/upload/upload.component';
import { EditVideoMetadataComponent } from './components/edit-video-metadata/edit-video-metadata.component';
import { StudioComponent } from './components/studio/studio.component';
import { ReviewAndPublishComponent } from './components/review-and-publish/review-and-publish.component';
import { VideoPlayerComponent } from './components/video-player/video-player.component';

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
        path: 'edit-video-info/:videoId',
        component: EditVideoMetadataComponent,
      },
      {
        path: 'review-and-publish',
        component: ReviewAndPublishComponent,
      },
    ],
  },
  {
    path: 'video-player',
    component: VideoPlayerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [provideRouter(routes, withComponentInputBinding())],
})
export class AppRoutingModule {}
