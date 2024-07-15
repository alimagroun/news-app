import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RegisterRequest } from '../../../models/register-request';
import { AuthenticationService } from '../../../services/authentication.service';


@Component({
  selector: 'app-auth-signup',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './auth-signup.component.html',
  styleUrls: ['./auth-signup.component.scss']
})
export default class AuthSignupComponent {

  newUser: RegisterRequest = {
    firstname: 'Ali',
    lastname: 'Marina',
    email: 'ali@gmail.com',
    password: 'test'
  }; 


  constructor(private authService: AuthenticationService) { }


  createUser() {
    this.authService.register(this.newUser).subscribe(() => {
      console.log('User created successfully!');
      // Handle success, e.g., show a success message
    }, error => {
      console.error('Error creating user:', error);
      // Handle error, e.g., show an error message
    });
  }
}






