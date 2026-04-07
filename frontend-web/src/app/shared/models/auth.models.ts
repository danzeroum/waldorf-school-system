export interface LoginRequest {
  email:    string;
  password: string;
}
export interface LoginResponse {
  accessToken:  string;
  refreshToken: string;
  usuario:      UsuarioLogado;
}
export interface UsuarioLogado {
  id:     number;
  nome:   string;
  email:  string;
  perfis: string[];
}
export interface RefreshTokenRequest {
  refreshToken: string;
}
export enum TipoPerfil {
  ADMIN = 'ADMIN',
  SECRETARIA = 'SECRETARIA',
  DIRETOR = 'DIRETOR',
  PROFESSOR = 'PROFESSOR',
  PAIS = 'PAIS',
  RESPONSAVEL = 'RESPONSAVEL',
  FINANCEIRO = 'FINANCEIRO',
  COORDENADOR = 'COORDENADOR',
  FUNCIONARIO = 'FUNCIONARIO',
}
export interface PerfilUsuario {
  id: number;
  nome: string;
  descricao: string;
  permissoes: string[];
}
export interface JwtPayload {
  sub: string;
  iat: number;
  exp: number;
}
