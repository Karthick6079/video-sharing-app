import { Component, Input, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';

@Component({
  selector: 'app-steps',
  templateUrl: './steps.component.html',
  styleUrl: './steps.component.css',
  providers: [MessageService],
})
export class StepsComponent implements OnInit {
  constructor(private messageService: MessageService) {}

  @Input()
  items: MenuItem[] | undefined;

  ngOnInit(): void {}
}
