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
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { ShortsPageComponent } from './components/shorts-page/shorts-page.component';
import { SearchResultsComponent } from './components/search-results/search-results.component';

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
        path: 'shorts',
        component: ShortsPageComponent,
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
      {
        path: 'search-results',
        component: SearchResultsComponent,
      },
    ],
  },
  {
    path: 'login/callback',
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
  {
    path: '**',
    component: HomeComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [provideRouter(routes, withComponentInputBinding())],
})
export class AppRoutingModule {}
