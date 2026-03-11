/** Modelos TypeScript compartilhados — Auth e Segurança */

export type Perfil = 'ADMIN' | 'DIRETOR' | 'SECRETARIA' | 'PROFESSOR' | 'PAIS';

export interface TokenPayload {
  sub:     string;    // userId
  nome:    string;
  email:   string;
  perfis:  Perfil[];
  iat:     number;
  exp:     number;
}

export interface LoginRequest {
  email:    string;
  password: string;
}

export interface LoginResponse {
  accessToken:  string;
  refreshToken: string;
  expiresIn:    number;
  usuario: {
    id:     number;
    nome:   string;
    email:  string;
    perfis: Perfil[];
  };
}

export interface UsuarioLogado {
  id:     number;
  nome:   string;
  email:  string;
  perfis: Perfil[];
}
