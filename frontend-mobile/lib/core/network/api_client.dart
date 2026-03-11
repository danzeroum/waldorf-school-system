import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:logger/logger.dart';
import '../auth/token_storage.dart';

final apiClientProvider = Provider<ApiClient>((ref) {
  final storage = ref.read(tokenStorageProvider);
  return ApiClient(storage);
});

class ApiClient {
  late final Dio _dio;
  final TokenStorage _storage;
  final _log = Logger();

  static const _baseUrl = String.fromEnvironment(
    'API_URL',
    defaultValue: 'http://10.0.2.2:8080/api/v1',
  );

  ApiClient(this._storage) {
    _dio = Dio(BaseOptions(
      baseUrl: _baseUrl,
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 30),
      headers: {'Content-Type': 'application/json'},
    ));
    _dio.interceptors.add(_AuthInterceptor(_storage, _dio, _log));
  }

  // --- Auth ---
  Future<Map<String, dynamic>> login(Map<String, dynamic> body) async {
    final r = await _dio.post('/auth/login', data: body);
    return r.data as Map<String, dynamic>;
  }

  // --- Alunos ---
  Future<List<dynamic>> listarFilhos() async {
    final r = await _dio.get('/alunos/meus-filhos');
    return r.data as List<dynamic>;
  }

  Future<Map<String, dynamic>> buscarAluno(int id) async {
    final r = await _dio.get('/alunos/$id');
    return r.data as Map<String, dynamic>;
  }

  // --- Financeiro ---
  Future<List<dynamic>> listarParcelas({String? status}) async {
    final r = await _dio.get('/finance/invoices', queryParameters: {
      if (status != null) 'status': status,
    });
    return r.data as List<dynamic>;
  }

  Future<Map<String, dynamic>> pagarParcela(int id, Map<String, dynamic> body) async {
    final r = await _dio.post('/finance/invoices/$id/pay', data: body);
    return r.data as Map<String, dynamic>;
  }
}

class _AuthInterceptor extends Interceptor {
  final TokenStorage _storage;
  final Dio _dio;
  final Logger _log;

  _AuthInterceptor(this._storage, this._dio, this._log);

  @override
  Future<void> onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    final token = await _storage.getAccessToken();
    if (token != null) options.headers['Authorization'] = 'Bearer $token';
    handler.next(options);
  }

  @override
  Future<void> onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode == 401) {
      try {
        final refresh = await _storage.getRefreshToken();
        if (refresh != null) {
          final r = await _dio.post('/auth/refresh', data: {'refreshToken': refresh});
          await _storage.salvarTokens(r.data['accessToken'], r.data['refreshToken']);
          final retryOpts = err.requestOptions;
          retryOpts.headers['Authorization'] = 'Bearer ${r.data["accessToken"]}';
          final retryResp = await _dio.fetch(retryOpts);
          return handler.resolve(retryResp);
        }
      } catch (_) {
        await _storage.limpar();
      }
    }
    handler.next(err);
  }
}
