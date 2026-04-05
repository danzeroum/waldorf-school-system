import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'wld-header',
  templateUrl: './header.component.html',
  standalone: false,
})
export class HeaderComponent {
  @Input() sidebarAberta = true;
  @Output() toggleSidebar = new EventEmitter<void>();

  menuUsuarioAberto = signal(false);

  constructor(readonly authService: AuthService) {}

  toggle(): void {
    this.toggleSidebar.emit();
  }

  toggleMenuUsuario(): void {
    this.menuUsuarioAberto.update(v => !v);
  }

  logout(): void {
    this.menuUsuarioAberto.set(false);
    this.authService.logout();
  }
}
