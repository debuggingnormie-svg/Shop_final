import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { Observable, of, tap, map, catchError } from 'rxjs';
import { environment } from '../../environments/environment';

interface AuthResponse {
  token: string;
  role: string;
  userId: number;
}

/**
 * Service responsible for managing user authentication, registration, and session state.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: User | null = null;
  private readonly tokenKey = 'auth_token';
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
    // Restore user from token if available
    const token = localStorage.getItem(this.tokenKey);
    if (token) {
      this.currentUser = this.decodeToken(token);
    }
  }

  /**
   * Register a new user via POST /api/auth/register
   */
  register(user: User): Observable<boolean> {
    const payload = {
      username: user.email, // Mapping email to username for now
      password: user.password,
      email: user.email,
      role: user.role,
      name: user.name,
      phone: user.phoneNumber,
      address: user.address ? JSON.stringify(user.address) : ''
    };

    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, payload).pipe(
      map(() => true),
      catchError(err => {
        console.error('Registration failed', err);
        return of(false);
      })
    );
  }

  /**
   * Login against POST /api/auth/login
   */
  login(email: string, password: string): Observable<boolean> {
    const payload = { username: email, password }; // Backend expects 'username'
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, payload).pipe(
      tap(response => this.setSession(response)),
      map(() => true),
      catchError(err => {
        console.error('Login failed', err);
        return of(false);
      })
    );
  }

  /**
   * Logs out the current user by clearing memory state and localStorage tokens.
   */
  logout(): void {
    this.currentUser = null;
    localStorage.removeItem(this.tokenKey);
  }

  /**
   * Returns the currently authenticated user or null.
   */
  getCurrentUser(): User | null {
    return this.currentUser;
  }

  /**
   * Checks if a user is currently logged in.
   */
  isLoggedIn(): boolean {
    return this.currentUser != null;
  }

  /**
   * Retrieves the current authentication token from storage.
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /** Update current user in memory (Used by profile updates) */
  updateCurrentUser(user: User): void {
    this.currentUser = user;
    // Note: We don't update the token here as the backend issues tokens only on login/refresh
  }

  private setSession(authResponse: AuthResponse): void {
    const token = authResponse.token;
    localStorage.setItem(this.tokenKey, token);

    // We decode the token or use the response to set the current user
    // The backend response gives us role and userId, but we might want to store more info
    // For now, we'll decode the token if it contains user info, or construct a partial user
    const decoded = this.decodeToken(token);
    if (decoded) {
      this.currentUser = decoded;
    } else {
      // Fallback if token doesn't have all info
      this.currentUser = {
        id: authResponse.userId,
        email: 'user@example.com', // Placeholder if not in token
        name: 'User',
        role: authResponse.role as any
      };
    }
  }

  private decodeToken(token: string): User | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;

      const payload = atob(parts[1]);
      const claims = JSON.parse(payload);

      // Map JWT claims to User object
      // Backend JWT usually contains sub (username/email), iat, exp, and maybe custom claims
      return {
        id: claims.userId || 0, // Ensure your backend puts userId in token claims
        email: claims.sub || '',
        name: claims.name || 'User',
        role: claims.role || 'CUSTOMER'
      };
    } catch (e) {
      console.error('Failed to decode token', e);
      return null;
    }
  }
}
