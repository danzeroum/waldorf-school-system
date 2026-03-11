import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/filhos_provider.dart';

class FilhoDetalheScreen extends ConsumerWidget {
  final int alunoId;
  const FilhoDetalheScreen({super.key, required this.alunoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final alunoAsync = ref.watch(alunoDetalheProvider(alunoId));
    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalhes'),
        leading: BackButton(onPressed: () => context.go('/filhos')),
      ),
      body: alunoAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
        data: (aluno) => ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Header
            Center(
              child: Column(
                children: [
                  CircleAvatar(
                    radius: 40,
                    backgroundColor: const Color(0xFF4A7C59).withOpacity(0.15),
                    child: Text(
                      aluno.nome[0].toUpperCase(),
                      style: const TextStyle(fontSize: 32, color: Color(0xFF4A7C59), fontWeight: FontWeight.w700),
                    ),
                  ),
                  const SizedBox(height: 12),
                  Text(aluno.nome, style: Theme.of(context).textTheme.titleLarge),
                  Text(aluno.turmaNome ?? 'Sem turma',
                      style: TextStyle(color: Colors.grey[600], fontSize: 14)),
                ],
              ),
            ),
            const SizedBox(height: 24),
            // Informações
            _InfoCard(titulo: 'Dados Escolares', itens: [
              _InfoItem('Matrícula', aluno.matricula),
              _InfoItem('Ano de ingresso', aluno.anoIngresso.toString()),
              _InfoItem('Turma', aluno.turmaNome ?? '—'),
            ]),
            const SizedBox(height: 12),
            _InfoCard(titulo: 'Desenvolvimento Waldorf', itens: [
              _InfoItem('Temperamento', aluno.temperamento ?? '—'),
            ]),
          ],
        ),
      ),
    );
  }
}

class _InfoCard extends StatelessWidget {
  final String titulo;
  final List<_InfoItem> itens;
  const _InfoCard({required this.titulo, required this.itens});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(titulo, style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13, color: Color(0xFF4A7C59))),
            const Divider(height: 16),
            ...itens.map((item) => Padding(
              padding: const EdgeInsets.symmetric(vertical: 4),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(item.label, style: TextStyle(color: Colors.grey[600], fontSize: 13)),
                  Text(item.valor, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
                ],
              ),
            )),
          ],
        ),
      ),
    );
  }
}

class _InfoItem {
  final String label, valor;
  const _InfoItem(this.label, this.valor);
}
