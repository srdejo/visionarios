import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-bottom-sheet',
  imports: [],
  templateUrl: './bottom-sheet.html',
})
export class BottomSheet {
  @Input() title = '';
  @Output() close = new EventEmitter<void>();
}
