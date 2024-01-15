import { Component, Input } from '@angular/core';
import { CommentDTO } from '../../dto/video-dto';

@Component({
  selector: 'app-show-comments',
  templateUrl: './show-comments.component.html',
  styleUrl: './show-comments.component.css',
})
export class ShowCommentsComponent {
  @Input()
  comment!: CommentDTO;

  disLikeComment() {
    throw new Error('Method not implemented.');
  }
  likeComment() {
    throw new Error('Method not implemented.');
  }
}
