import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NgxFileDropModule } from 'ngx-file-drop';
import { AuthModule, LogLevel } from 'angular-auth-oidc-client';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from 'angular-auth-oidc-client';
import { UploadComponent } from './components/upload/upload.component';
import { EditVideoMetadataComponent } from './components/edit-video-metadata/edit-video-metadata.component';
import { StudioComponent } from './components/studio/studio.component';
import { StepsComponent } from './components/steps/steps.component';
import { StepsModule } from 'primeng/steps';
import { ToastModule } from 'primeng/toast';
import { FileUploadModule } from 'primeng/fileupload';
import { MessageService } from 'primeng/api';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatMenuModule } from '@angular/material/menu';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { VideoPlayerComponent } from './components/video-player/video-player.component';
import { VgCoreModule } from '@videogular/ngx-videogular/core';
import { VgControlsModule } from '@videogular/ngx-videogular/controls';
import { VgOverlayPlayModule } from '@videogular/ngx-videogular/overlay-play';
import { VgBufferingModule } from '@videogular/ngx-videogular/buffering';
import { ReactiveFormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { ChipsModule } from 'primeng/chips';
import { ButtonModule } from 'primeng/button';
import { HomeComponent } from './components/home/home.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { FeaturedComponent } from './components/featured/featured.component';
import { SubscriptionsComponent } from './components/subscriptions/subscriptions.component';
import { HistoryComponent } from './components/history/history.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { VideoCardComponent } from './components/video-card/video-card.component';
import { MenuModule } from 'primeng/menu';
import { DividerModule } from 'primeng/divider';
import { ChipModule } from 'primeng/chip';
import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { WatchComponent } from './components/watch/watch.component';
import { SplitButtonModule } from 'primeng/splitbutton';
import { ChannelInfoComponent } from './components/channel-info/channel-info.component';
import { InplaceModule } from 'primeng/inplace';
import { CommentComponent } from './components/comment/comment.component';
import { MatInputModule } from '@angular/material/input';
import { ShowCommentsComponent } from './components/show-comments/show-comments.component';
import { SuggestionComponent } from './components/suggestion/suggestion.component';
import { ThumbnailVideoPlayerComponent } from './components/thumbnail-video-player/thumbnail-video-player.component';
import { RegisterUserComponent } from './components/register-user/register-user.component';
import { LoadingAnimationComponent } from './components/loading-animation/loading-animation.component';
import { RelativeTimeFilterPipe } from './pipes/relative-time-filter.pipe';
import { TimeagoModule } from 'ngx-timeago';
import { RouteReuseStrategy } from '@angular/router';
import { CustomRouteReuseStrategy } from './services/custom-routing-strategy';
import { HomepageSkeletonComponent } from './components/homepage-skeleton/homepage-skeleton.component';
import { ShortsComponent } from './components/shorts/shorts.component';
import { ShortsPageComponent } from './components/shorts-page/shorts-page.component';
import { ShortsPlayerComponent } from './components/shorts-player/shorts-player.component';
import { ShortsVidInfoComponent } from './components/shorts-vid-info/shorts-vid-info.component';
import { DialogModule } from 'primeng/dialog';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { TooltipModule } from 'primeng/tooltip';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    UploadComponent,
    EditVideoMetadataComponent,
    StudioComponent,
    StepsComponent,
    VideoPlayerComponent,
    HomeComponent,
    SidebarComponent,
    FeaturedComponent,
    SubscriptionsComponent,
    HistoryComponent,
    UserProfileComponent,
    VideoCardComponent,
    WatchComponent,
    ChannelInfoComponent,
    CommentComponent,
    ShowCommentsComponent,
    SuggestionComponent,
    ThumbnailVideoPlayerComponent,
    RegisterUserComponent,
    LoadingAnimationComponent,
    RelativeTimeFilterPipe,
    HomepageSkeletonComponent,
    ShortsComponent,
    ShortsPageComponent,
    ShortsPlayerComponent,
    ShortsVidInfoComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    NgxFileDropModule,
    StepsModule,
    ToastModule,
    FileUploadModule,
    BrowserAnimationsModule,
    MatMenuModule,
    InputTextModule,
    InputTextareaModule,
    VgBufferingModule,
    VgControlsModule,
    VgCoreModule,
    VgOverlayPlayModule,
    ReactiveFormsModule,
    DropdownModule,
    ChipsModule,
    ButtonModule,
    MenuModule,
    DividerModule,
    ChipModule,
    AvatarModule,
    AvatarGroupModule,
    SplitButtonModule,
    InplaceModule,
    MatInputModule,
    TooltipModule,
    DialogModule,
    OverlayPanelModule,
    TimeagoModule.forRoot(),
    AuthModule.forRoot({
      config: {
        authority: 'https://karthick-v.us.auth0.com',
        redirectUrl: 'http://localhost:4200/register-user',
        postLogoutRedirectUri: 'http://localhost:4200/home/featured',
        clientId: 'w4oDolUdBgotpHMD1VLgTwNW46KDNr5E',
        scope:
          'openid profile email offline_access given_name family_name nickname phone address picture',
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        logLevel: LogLevel.Debug,
        secureRoutes: ['http://localhost:8080/'],
        customParamsAuthRequest: {
          audience: 'http://localhost:8080',
        },
      },
    }),
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    MessageService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
