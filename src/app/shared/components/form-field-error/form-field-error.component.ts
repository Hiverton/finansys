import { Component, OnInit, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { identifierModuleUrl } from '@angular/compiler';

@Component({
  selector: 'app-form-field-error',
  template: `
    <p class="text-danger">
      {{errorMessage}}
    </p>
  `,
  styleUrls: ['./form-field-error.component.css']
})
export class FormFieldErrorComponent implements OnInit {

  @Input('form-control') formControl: FormControl;

  constructor() { }

  ngOnInit() {
  }

  public get errorMessage(): string | null {
    if(this.mustShowMessageError()){
      return this.getErrorMessage();
    } else {
      return null;
    }
  }

  private mustShowMessageError(): boolean {
    return this.formControl.invalid && this.formControl.touched
  }

  private getErrorMessage(): string | null {

    if(this.formControl.errors.required) {
      return "dado obrigatório"
    
    } else if(this.formControl.errors.email) {
      return "formato de e-mail inválido"

    } else if(this.formControl.errors.minlength) {
      const requiredLength = this.formControl.errors.minlength.requiredLength
      return `deve ter no minimo ${requiredLength} caracteres`;

    }else if(this.formControl.errors.maxlength) {
      const requiredLength = this.formControl.errors.maxlength.requiredLength
      return `deve ter no máximo ${requiredLength} caracteres`;
    }

  }
}
