import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, map } from 'rxjs';

export interface EnderecoViaCep {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

export interface EnderecoForm {
  cep: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  estado: string;
}

@Injectable({ providedIn: 'root' })
export class BuscaCepService {
  constructor(private http: HttpClient) {}

  buscar(cep: string): Observable<EnderecoForm | null> {
    const cepLimpo = cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) return of(null);

    return this.http
      .get<EnderecoViaCep>(`https://viacep.com.br/ws/${cepLimpo}/json/`)
      .pipe(
        map(resp => {
          if (resp.erro) return null;
          return {
            cep: resp.cep,
            logradouro: resp.logradouro,
            bairro: resp.bairro,
            cidade: resp.localidade,
            estado: resp.uf,
          };
        }),
        catchError(() => of(null))
      );
  }
}
