import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-server-error-message',
  templateUrl: './server-error-message.component.html',
  styleUrls: ['./server-error-message.component.css']
})
export class ServerErrorMessageComponent implements OnInit {

  @Input('server-messages-error') serverErrorMessages: string[] = null;

  constructor() { }

  ngOnInit() {
  }

}
