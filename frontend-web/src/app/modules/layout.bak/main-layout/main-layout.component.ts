import { Component, signal } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'wld-main-layout',
  templateUrl: './main-layout.component.html',
  standalone: false,
})
export class MainLayoutComponent {
  sidebarAberta = signal(true);

  constructor(readonly authService: AuthService) {}

  toggleSidebar(): void {
    this.sidebarAberta.update(v => !v);
  }
}
