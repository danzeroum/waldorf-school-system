import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'token_storage.g.dart';

@riverpod
TokenStorage tokenStorage(TokenStorageRef ref) => TokenStorage();

class TokenStorage {
  static const _keyAccess  = 'access_token';
  static const _keyRefresh = 'refresh_token';

  final _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
  );

  Future<void> salvarTokens(String access, String refresh) async {
    await _storage.write(key: _keyAccess,  value: access);
    await _storage.write(key: _keyRefresh, value: refresh);
  }

  Future<String?> getAccessToken()  => _storage.read(key: _keyAccess);
  Future<String?> getRefreshToken() => _storage.read(key: _keyRefresh);

  Future<void> limpar() => _storage.deleteAll();
}
