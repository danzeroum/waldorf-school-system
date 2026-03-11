import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  success(msg: string) {
    this.snackBar.open(msg, 'Fechar', { duration: 3000, panelClass: ['snack-success'] });
  }
  error(msg: string) {
    this.snackBar.open(msg, 'Fechar', { duration: 5000, panelClass: ['snack-error'] });
  }
  info(msg: string) {
    this.snackBar.open(msg, 'Fechar', { duration: 3000 });
  }
}
