import { Component, Input, forwardRef, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-password-input',
  imports: [FormsModule],
  templateUrl: './password-input.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PasswordInput),
      multi: true,
    },
  ],
})
export class PasswordInput implements ControlValueAccessor {
  @Input() placeholder = '';
  @Input() required = false;
  @Input() minLength: number | null = null;
  @Input() autocomplete = 'new-password';

  readonly visible = signal(false);
  readonly disabled = signal(false);
  value = '';

  private onChange: (value: string) => void = () => {};
  onTouched: () => void = () => {};

  toggleVisible(): void {
    this.visible.update((v) => !v);
  }

  handleInput(value: string): void {
    this.value = value;
    this.onChange(value);
  }

  writeValue(value: string): void {
    this.value = value ?? '';
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }
}
