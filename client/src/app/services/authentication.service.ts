import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RegisterRequest } from '../models/register-request';
import { AuthenticationResponse } from '../models/authentication-response';
import { tap } from 'rxjs/operators';



@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private authUrl = environment.apiUrl.auth;
  private readonly ACCESS_TOKEN_KEY = 'access_token';

  constructor(private http: HttpClient) { }

  register(request: RegisterRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.authUrl}/register`, request)
      .pipe(
        tap(response => this.saveAccessToken(response.access_token))
      );
  }

  private saveAccessToken(accessToken: string): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  clearAccessToken(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
  }
}
