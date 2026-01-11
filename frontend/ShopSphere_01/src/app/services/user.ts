// src/app/services/user.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { User } from '../models/user';
import { environment } from '../../environments/environment';

/**
 * Handles operations related to the user profile.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Fetches the current user's profile from the server.
   * Ignores userId param as backend determines user from token.
   */
  getUser(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/profile`).pipe(
      map(user => {
        // Backend might return address as a JSON string
        if (user.address && typeof user.address === 'string') {
          try {
            user.address = JSON.parse(user.address);
          } catch (e) {
            console.warn('Could not parse user address', e);
          }
        }
        return user;
      })
    );
  }

  /**
   * Updates an existing user profile on the server.
   * @deprecated Backend endpoint for update not yet implemented.
   */
  updateUser(user: User): Observable<User> {
    // Backend expects specific DTO structure? 
    // Backend UserRequestDTO has: username, password, email, role, name, phone, address
    // Let's send what we have.
    const payload = {
      name: user.name,
      email: user.email,
      phone: user.phoneNumber,
      address: typeof user.address === 'object' ? JSON.stringify(user.address) : user.address
      // role, username, password usually not updatable here or need separate flow
    };

    return this.http.put<User>(`${this.apiUrl}/users/profile`, payload).pipe(
      map(updatedUser => {
        // Backend might return address as a JSON string
        if (updatedUser.address && typeof updatedUser.address === 'string') {
          try {
            updatedUser.address = JSON.parse(updatedUser.address);
          } catch (e) {
            console.warn('Could not parse user address', e);
          }
        }
        return updatedUser;
      })
    );
  }
}
