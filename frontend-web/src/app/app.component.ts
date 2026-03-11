import { Component } from '@angular/core';

@Component({
  selector: 'wld-root',
  template: '<router-outlet></router-outlet>',
  standalone: false,
})
export class AppComponent {
  title = 'waldorf-school-system';
}
