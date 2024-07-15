import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';

import { AuthenticationService } from '../../services/authentication.service';

import { AuthenticationRoutingModule } from './authentication-routing.module';

@NgModule({
  declarations: [],
  imports: [CommonModule,
    AuthenticationRoutingModule,
    FormsModule
  ],
  providers: [provideHttpClient(),
  AuthenticationService
  ],
})
export class AuthenticationModule {}
