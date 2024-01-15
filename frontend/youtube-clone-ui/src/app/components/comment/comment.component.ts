import { Component, Input, OnInit } from '@angular/core';
import { LoginService } from '../../services/login/login.service';
import { MessageService } from 'primeng/api';
import { FormControl } from '@angular/forms';
import { VideoService } from '../../services/video/video.service';
import { CommentDTO, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.css',
})
export class CommentComponent implements OnInit {
  constructor(
    private loginService: LoginService,
    private messageService: MessageService,
    private videoService: VideoService,
    private oidcSecurityService: OidcSecurityService
  ) {}

  public isAuthenticated: boolean = false;

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );
  }

  @Input()
  video!: VideoDto;

  comment!: string;

  commentDto!: CommentDTO;

  isNewCommentAdded = false;

  cancelComment() {
    this.comment = '';
  }
  addComment(comment: string) {
    if (this.showLoginMessageIfNot()) {
      this.isNewCommentAdded = false;
      this.videoService
        .addComment(this.video.id, this.prepareRequestForComment(comment))
        .subscribe((comment) => {
          this.commentDto = comment;
          this.isNewCommentAdded = true;
        });
    }
  }

  prepareRequestForComment(comment: string) {
    const commentBody = {
      text: comment,
      videoId: this.video.id,
      userId: this.video.userDTO.id,
    };
    return commentBody;
  }

  showLoginMessageIfNot() {
    if (!this.isAuthenticated) {
      this.setLoginMessage('Please login before share your feedback!');
      return false;
    }
    return true;
  }

  setLoginMessage(message: string) {
    this.messageService.add({
      severity: 'info',
      summary: 'Info',
      detail: message,
      sticky: false,
      life: 5000,
      key: 'tc',
    });
  }
}
