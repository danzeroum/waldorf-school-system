// === MODELOS DE AUTENTICAÇÃO ===

export interface LoginRequest {
  login: string;
  senha: string;
  lembrarMe?: boolean;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  usuario: UsuarioLogado;
}

export interface UsuarioLogado {
  id: number;
  login: string;
  nomeCompleto: string;
  email: string;
  perfis: PerfilUsuario[];
  contextos: ContextoUsuario[];
  fotoPerfil?: string;
  primeiroAcesso: boolean;
}

export interface PerfilUsuario {
  id: number;
  nome: string;
  descricao: string;
  permissoes: string[];
}

export interface ContextoUsuario {
  tipo: 'SECRETARIA' | 'PROFESSOR' | 'PAIS' | 'ADMIN' | 'DIRECAO';
  referenciaId: number;
  referenciaDescricao: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface MfaRequest {
  login: string;
  codigoTotp: string;
}

// === MODELOS DE USUÁRIO ===

export enum TipoPerfil {
  ADMIN = 'ADMIN',
  SECRETARIA = 'SECRETARIA',
  PROFESSOR = 'PROFESSOR',
  PAIS = 'PAIS',
  DIRECAO = 'DIRECAO',
  FUNCIONARIO = 'FUNCIONARIO',
}

export interface JwtPayload {
  sub: string;
  iat: number;
  exp: number;
  perfis: string[];
  contextos: ContextoUsuario[];
  primeiroAcesso: boolean;
}
