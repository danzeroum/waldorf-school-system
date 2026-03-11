import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'wld-root',
  template: `<router-outlet></router-outlet>`,
  standalone: false,
})
export class AppComponent {
  title = 'Escola Waldorf';
}
