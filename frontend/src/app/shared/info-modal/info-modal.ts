import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-info-modal',
  imports: [],
  templateUrl: './info-modal.html',
})
export class InfoModal {
  @Input() title = '';
  @Input() message = '';
  @Input() closeLabel = 'Entendido';
  @Output() close = new EventEmitter<void>();
}
