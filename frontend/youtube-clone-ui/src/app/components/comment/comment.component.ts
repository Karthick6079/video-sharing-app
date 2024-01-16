import { Component, Input, OnInit } from '@angular/core';
import { LoginService } from '../../services/login/login.service';
import { MessageService } from 'primeng/api';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  RequiredValidator,
  Validators,
} from '@angular/forms';
import { VideoService } from '../../services/video/video.service';
import { CommentDTO, UserDto, VideoDto } from '../../dto/video-dto';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { CommentService } from '../../services/comment/comment.service';
import { UserService } from '../../services/user/user.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.css',
})
export class CommentComponent implements OnInit {
  @Input()
  video!: VideoDto;

  currentUser!: UserDto;

  comment!: string;

  commentDto!: CommentDTO;

  isNewCommentAdded = false;

  comments: CommentDTO[] = [];

  public isAuthenticated: boolean = false;

  commentForm!: FormGroup;

  constructor(
    private loginService: LoginService,
    private messageService: MessageService,
    private videoService: VideoService,
    private commentService: CommentService,
    private oidcSecurityService: OidcSecurityService,
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.commentForm = this.fb.group({
      comment: [''],
    });
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
      }
    );

    if (this.isAuthenticated) {
      this.currentUser = this.userService.getCurrentUser();
    }

    this.getComments(this.video.id);
  }

  cancelComment() {
    this.clearCommentSection();
  }

  addComment() {
    if (this.showLoginMessageIfNot()) {
      this.isNewCommentAdded = false;
      const comment = this.commentForm.get('comment')?.value;
      this.commentService
        .addComment(this.video.id, this.prepareRequestForComment(comment))
        .subscribe((comment) => {
          this.commentDto = comment;
          this.comments.splice(0, 0, this.commentDto);
          // this.comments.push(comment);
          this.isNewCommentAdded = true;
          this.clearCommentSection();
        });
    }
  }

  clearCommentSection() {
    this.commentForm.get('comment')?.reset();
  }

  getComments(videoId: string) {
    this.commentService
      .getComments(this.video.id)
      .subscribe((comments: CommentDTO[]) => {
        this.comments = comments;
        // this.isNewCommentAdded = true;
      });
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
