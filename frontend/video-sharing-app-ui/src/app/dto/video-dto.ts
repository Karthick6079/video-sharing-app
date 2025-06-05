export interface VideoDto {
  id: string;
  videoId: string;
  userId: string;
  title: string;
  description: string;
  likes: number;
  dislikes: number;
  views: number;
  tags: string[];
  videoStatus: string;
  videoUrl: string;
  thumbnailUrl: string;
  publishedAt: string;
  username: string;
  userDisplayName: string;
  userPicture: string;
  channelSubscribersCount: number;
  isCurrentUserSubscribedToChannel: boolean
}

export interface WatchedVideoDTO extends VideoDto {
  watchedAt: string | number | Date;
}

export interface LikedVideoDTO extends VideoDto {
  likedAt: string | number | Date;
  likedTopics: string[];
}

export interface UserDto {
  id?: string;
  firstName?: string;
  lastName?: string;
  name?: string;
  picture?: string;
  sub?: string;
  email?: string;
  nickname?: string;
  subscribedToUsers: any[];
  subscribers?: any[];
  videoHistory?: any[];
  likedVideos?: any[];
  dislikedVideos?: any[];
  subscribedToCount?: number;
  subscribersCount?: number;
}

export interface ChannelInfoDTO {
  name: string;
  displayName: string;
  subscribersCount: number;
  userSubscribed: boolean
}

export interface CommentDTO {
  id: string;
  text: string;
  userId: string;
  likes: number;
  dislikes: number;
  videoId: string;
  picture: string;
  username: string;
  createdAt: number;
}

export interface VideoStatus {
  name: string;
  code: string;
}
