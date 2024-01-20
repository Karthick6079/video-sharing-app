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
import { HomeComponent } from './components/home/home.component';
import { FeaturedComponent } from './components/featured/featured.component';
import { SubscriptionsComponent } from './components/subscriptions/subscriptions.component';
import { HistoryComponent } from './components/history/history.component';
import { WatchComponent } from './components/watch/watch.component';
import { RegisterUserComponent } from './components/register-user/register-user.component';
import { videoDataResolverResolver } from './resolver/video-data-resolver.resolver';
import { HomepageSkeletonComponent } from './components/homepage-skeleton/homepage-skeleton.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    children: [
      {
        path: 'featured',
        component: FeaturedComponent,
      },
      {
        path: 'subscriptions',
        component: SubscriptionsComponent,
      },
      {
        path: 'user-profile',
        component: UserProfileComponent,
      },
      {
        path: 'history',
        component: HistoryComponent,
      },
    ],
  },
  {
    path: 'register-user',
    component: RegisterUserComponent,
  },

  {
    path: 'watch/:videoId',
    component: WatchComponent,
    resolve: { video: videoDataResolverResolver },
  },

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
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [provideRouter(routes, withComponentInputBinding())],
})
export class AppRoutingModule {}
