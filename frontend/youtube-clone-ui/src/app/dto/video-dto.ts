export interface VideoDto {
  id: string;
  title: string;
  description: string;
  tags: string[];
  videoStatus: VideoStatus;
  videoUrl: string;
  thumbnailUrl: string;
}

export interface VideoStatus {
  name: string;
  code: string;
}
