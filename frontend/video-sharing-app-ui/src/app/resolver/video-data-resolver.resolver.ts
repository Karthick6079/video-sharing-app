import { ResolveFn } from '@angular/router';
import { VideoService } from '../services/video/video.service';
import { inject } from '@angular/core';
import { VideoDto } from '../dto/video-dto';
import { filter, take } from 'rxjs';

export const videoDataResolverResolver: ResolveFn<VideoDto> = (
  route,
  state,
  videoService: VideoService = inject(VideoService)
) => {
  const videoId: string = String(route.paramMap.get('videoId'));

  return videoService.getVideo(videoId).pipe(
    filter<VideoDto>((video: VideoDto) => !!video),
    take(1)
  );
};
