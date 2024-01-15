export interface VideoDto {
  id: string;
  title: string;
  description: string;
  tags: string[];
  videoStatus: VideoStatus;
  videoUrl: string;
  thumbnailUrl: string;
  userDTO: UserDto;
  viewCount: number;
  likes: number;
  dislikes: number;
  publishedDateAndTime: string;
}

export interface UserDto {
  id: string;
  firstName: string;
  lastName: string;
  name: string;
  picture: string;
  sub: string;
  email: string;
  nickname: string;
  subscribedToUsers: any[];
  subscribers: any[];
  videoHistory: any[];
  likedVideos: any[];
  dislikedVideos: any[];
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
  commentCreatedTime: number;
}

export interface VideoStatus {
  name: string;
  code: string;
}
