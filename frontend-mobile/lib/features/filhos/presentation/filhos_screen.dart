import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:shimmer/shimmer.dart';
import '../providers/filhos_provider.dart';

class FilhosScreen extends ConsumerWidget {
  const FilhosScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final filhosAsync = ref.watch(filhosProvider);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Meus Filhos'),
        leading: BackButton(onPressed: () => context.go('/home')),
      ),
      body: filhosAsync.when(
        loading: () => _Skeleton(),
        error: (e, _) => Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.error_outline, size: 48, color: Colors.red),
              const SizedBox(height: 12),
              Text('Erro ao carregar filhos', style: TextStyle(color: Colors.grey[700])),
              const SizedBox(height: 12),
              FilledButton.tonal(
                onPressed: () => ref.invalidate(filhosProvider),
                child: const Text('Tentar novamente'),
              ),
            ],
          ),
        ),
        data: (filhos) => filhos.isEmpty
            ? const Center(child: Text('Nenhum aluno vinculado.'))
            : ListView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: filhos.length,
                itemBuilder: (_, i) {
                  final f = filhos[i];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 12),
                    child: ListTile(
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      leading: CircleAvatar(
                        backgroundColor: const Color(0xFF4A7C59).withOpacity(0.15),
                        child: Text(
                          f.nome.isNotEmpty ? f.nome[0].toUpperCase() : '?',
                          style: const TextStyle(color: Color(0xFF4A7C59), fontWeight: FontWeight.w700),
                        ),
                      ),
                      title: Text(f.nome, style: const TextStyle(fontWeight: FontWeight.w600)),
                      subtitle: Text('${f.turmaNome ?? "Sem turma"} • Matrícula ${f.matricula}',
                          style: TextStyle(fontSize: 12, color: Colors.grey[600])),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () => context.go('/filhos/${f.id}'),
                    ),
                  );
                },
              ),
      ),
    );
  }
}

class _Skeleton extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Shimmer.fromColors(
      baseColor: Colors.grey[200]!,
      highlightColor: Colors.grey[100]!,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: 3,
        itemBuilder: (_, __) => Container(
          margin: const EdgeInsets.only(bottom: 12),
          height: 76,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
          ),
        ),
      ),
    );
  }
}
