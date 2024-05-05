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
import { IndianFormatViewCount } from '../../pipes/indianformatviewcount.pipe';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.css',
})
export class CommentComponent implements OnInit {
  @Input()
  video!: VideoDto;

  @Input()
  currentUser!: UserDto;

  userProfileUrl!: string;

  comment!: string;

  commentDto!: CommentDTO;

  isNewCommentAdded = false;

  comments: CommentDTO[] = [];

  isAuthenticated: boolean = false;

  commentForm!: FormGroup;

  numberOfComments: any = 0;

  page: number = 0;
  private SIZE: number = 5;

  showLoadCommentButton = false;

  commentsLoadingfromApi = false;

  constructor(
    private loginService: LoginService,
    private messageService: MessageService,
    private videoService: VideoService,
    private commentService: CommentService,
    private oidcSecurityService: OidcSecurityService,
    private fb: FormBuilder,
    private userService: UserService,
    private indianFormatViewCount: IndianFormatViewCount
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

    this.getComments(this.video.id, this.page);
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

  getComments(videoId: string, page: number) {
    this.commentsLoadingfromApi = true;
    this.commentService
      .getComments(this.video.id, page, this.SIZE)
      .subscribe((commentsMap: Record<string, any>) => {
        if (commentsMap['commentsList']) {
          const commentsfromApi: CommentDTO[] = commentsMap['commentsList'];

          const commentsString = JSON.stringify(commentsfromApi);

          const commentsJsObject: CommentDTO[] = JSON.parse(commentsString);

          if (commentsJsObject) {
            // if(commentsJsObject.length > 1) {
            //   this.showLoadCommentButton  = true;
            // }

            if (commentsJsObject.length < this.SIZE) {
              this.showLoadCommentButton = false;
            } else {
              this.showLoadCommentButton = true;
            }

            this.comments.push(...commentsJsObject);
            if (commentsMap['commentsCount']) {
              this.numberOfComments = commentsMap['commentsCount'];
            }

            this.commentsLoadingfromApi = false;
          }
        }
      });
  }

  loadComments() {
    this.page = this.page + 1;
    this.getComments(this.video.id, this.page);
  }

  prepareRequestForComment(comment: string) {
    const commentBody = {
      text: comment,
      videoId: this.video.id,
      userId: this.video.userId,
    };
    return commentBody;
  }

  showLoginMessageIfNot() {
    if (!this.isAuthenticated) {
      this.loginService.login();
      // this.setLoginMessage('Please login before share your feedback!');
      // return false;
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
