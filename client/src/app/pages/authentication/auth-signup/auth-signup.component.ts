import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RegisterRequest } from '../../../models/register-request';
import { AuthenticationService } from '../../../services/authentication.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-signup',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule, CommonModule], 
  templateUrl: './auth-signup.component.html',
  styleUrls: ['./auth-signup.component.scss']
})
export default class AuthSignupComponent implements OnInit {
  signupForm: FormGroup;
  errorMessage: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthenticationService) {}

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      const registerRequest: RegisterRequest = this.signupForm.value;
      this.authService.register(registerRequest).subscribe({
        next: response => {
          console.log('Registration successful', response);
          this.errorMessage = null;
        },
        error: error => {
          this.errorMessage = error.error;
        }
      });
    }
  }

  clearErrorMessage(): void {
    this.errorMessage = null;
  }
}
