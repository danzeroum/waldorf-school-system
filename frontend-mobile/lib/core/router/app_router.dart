import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/home/presentation/home_screen.dart';
import '../../features/filhos/presentation/filhos_screen.dart';
import '../../features/filhos/presentation/filho_detalhe_screen.dart';
import '../../features/financeiro/presentation/financeiro_screen.dart';
import '../auth/auth_service.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  final authService = ref.watch(authServiceProvider);

  return GoRouter(
    initialLocation: '/home',
    redirect: (context, state) async {
      final logado = await authService.estaLogado();
      final naLogin = state.matchedLocation == '/login';
      if (!logado && !naLogin) return '/login';
      if (logado && naLogin)  return '/home';
      return null;
    },
    routes: [
      GoRoute(path: '/login',  builder: (_, __) => const LoginScreen()),
      GoRoute(path: '/home',   builder: (_, __) => const HomeScreen()),
      GoRoute(path: '/filhos', builder: (_, __) => const FilhosScreen()),
      GoRoute(
        path: '/filhos/:id',
        builder: (_, state) => FilhoDetalheScreen(
          alunoId: int.parse(state.pathParameters['id']!),
        ),
      ),
      GoRoute(path: '/financeiro', builder: (_, __) => const FinanceiroScreen()),
    ],
  );
});
