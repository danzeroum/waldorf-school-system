import { Injectable } from '@angular/core';
import { JwtPayload } from '@models/auth.models';

@Injectable({ providedIn: 'root' })
export class JwtService {

  decode(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = parts[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded) as JwtPayload;
    } catch {
      return null;
    }
  }

  isExpired(token: string): boolean {
    const payload = this.decode(token);
    if (!payload) return true;
    const now = Math.floor(Date.now() / 1000);
    // Margem de 30 segundos para evitar race conditions
    return payload.exp < (now + 30);
  }

  expiresInSeconds(token: string): number {
    const payload = this.decode(token);
    if (!payload) return 0;
    const now = Math.floor(Date.now() / 1000);
    return Math.max(0, payload.exp - now);
  }

  getPerfis(token: string): string[] {
    return this.decode(token)?.perfis ?? [];
  }

  getSubject(token: string): string | null {
    return this.decode(token)?.sub ?? null;
  }
}
