import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../network/api_client.dart';
import 'token_storage.dart';

part 'auth_service.g.dart';

@riverpod
AuthService authService(AuthServiceRef ref) => AuthService(
  ref.read(apiClientProvider),
  ref.read(tokenStorageProvider),
);

class AuthService {
  final ApiClient _api;
  final TokenStorage _storage;

  AuthService(this._api, this._storage);

  Future<bool> estaLogado() async {
    final token = await _storage.getAccessToken();
    if (token == null) return false;
    return !JwtDecoder.isExpired(token);
  }

  Future<void> login(String email, String senha) async {
    final resp = await _api.login({'email': email, 'password': senha});
    await _storage.salvarTokens(resp['accessToken'], resp['refreshToken']);
  }

  Future<void> logout() async {
    await _storage.limpar();
  }

  Future<Map<String, dynamic>?> getUsuarioLogado() async {
    final token = await _storage.getAccessToken();
    if (token == null) return null;
    return JwtDecoder.decode(token);
  }
}
