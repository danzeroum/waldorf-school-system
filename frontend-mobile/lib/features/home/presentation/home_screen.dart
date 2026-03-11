import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import '../../../core/auth/auth_service.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Início'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout_outlined),
            onPressed: () async {
              await ref.read(authServiceProvider).logout();
              if (context.mounted) context.go('/login');
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {},
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Saudação
            Text(
              'Olá! Bem-vindo(a) 👋',
              style: theme.textTheme.titleLarge,
            ),
            Text(
              DateFormat("EEEE, d 'de' MMMM", 'pt_BR').format(DateTime.now()),
              style: theme.textTheme.bodySmall?.copyWith(color: Colors.grey[600]),
            ),
            const SizedBox(height: 24),

            // Atalhos
            Text('Acesso rápido', style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            GridView.count(
              crossAxisCount: 2,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              childAspectRatio: 1.3,
              children: [
                _AtalhoCard(icone: Icons.child_care, titulo: 'Meus Filhos',  cor: const Color(0xFF4A7C59), rota: '/filhos'),
                _AtalhoCard(icone: Icons.payments,   titulo: 'Financeiro',   cor: const Color(0xFF6B4423), rota: '/financeiro'),
                _AtalhoCard(icone: Icons.campaign,   titulo: 'Comunicados',  cor: const Color(0xFF1565C0), rota: '/comunicados'),
                _AtalhoCard(icone: Icons.event,      titulo: 'Calendário',   cor: const Color(0xFF6A1B9A), rota: '/calendario'),
              ],
            ),
            const SizedBox(height: 24),

            // Últimas notificações
            Text('Notificações recentes', style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            Card(
              child: ListTile(
                leading: const CircleAvatar(
                  backgroundColor: Color(0xFFFFEDD5),
                  child: Icon(Icons.schedule, color: Color(0xFFEA580C), size: 20),
                ),
                title: const Text('Mensalidade vencendo', style: TextStyle(fontSize: 14, fontWeight: FontWeight.w500)),
                subtitle: Text('Vence em 3 dias', style: TextStyle(fontSize: 12, color: Colors.grey[600])),
                trailing: const Icon(Icons.chevron_right, size: 18),
                onTap: () => context.go('/financeiro'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _AtalhoCard extends StatelessWidget {
  final IconData icone;
  final String titulo;
  final Color cor;
  final String rota;

  const _AtalhoCard({required this.icone, required this.titulo, required this.cor, required this.rota});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: () => context.go(rota),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: cor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(icone, color: cor, size: 22),
              ),
              const Spacer(),
              Text(titulo, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600)),
            ],
          ),
        ),
      ),
    );
  }
}
