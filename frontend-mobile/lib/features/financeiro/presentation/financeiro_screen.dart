import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import '../providers/financeiro_provider.dart';
import '../models/parcela_model.dart';

class FinanceiroScreen extends ConsumerStatefulWidget {
  const FinanceiroScreen({super.key});

  @override
  ConsumerState<FinanceiroScreen> createState() => _FinanceiroScreenState();
}

class _FinanceiroScreenState extends ConsumerState<FinanceiroScreen> {
  String _filtro = 'TODOS';
  final _fmt = NumberFormat.currency(locale: 'pt_BR', symbol: 'R\$');
  final _fmtData = DateFormat('dd/MM/yyyy');

  @override
  Widget build(BuildContext context) {
    final parcelasAsync = ref.watch(parcelasProvider(_filtro == 'TODOS' ? null : _filtro));
    return Scaffold(
      appBar: AppBar(
        title: const Text('Financeiro'),
        leading: BackButton(onPressed: () => context.go('/home')),
      ),
      body: Column(
        children: [
          // Filtros
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: ['TODOS', 'PENDENTE', 'VENCIDA', 'PAGA'].map((s) =>
                  Padding(
                    padding: const EdgeInsets.only(right: 8),
                    child: ChoiceChip(
                      label: Text(s),
                      selected: _filtro == s,
                      onSelected: (_) => setState(() => _filtro = s),
                    ),
                  ),
                ).toList(),
              ),
            ),
          ),
          const SizedBox(height: 8),
          // Lista
          Expanded(
            child: parcelasAsync.when(
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (e, _) => _erroOffline(ref),
              data: (parcelas) => parcelas.isEmpty
                  ? const Center(child: Text('Nenhuma parcela encontrada.'))
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      itemCount: parcelas.length,
                      itemBuilder: (_, i) => _ParcelaCard(parcela: parcelas[i], fmt: _fmt, fmtData: _fmtData),
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _erroOffline(WidgetRef ref) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.wifi_off, size: 48, color: Colors.orange),
          const SizedBox(height: 12),
          const Text('Sem conexão — exibindo dados locais'),
          const SizedBox(height: 12),
          FilledButton.tonal(
            onPressed: () => ref.invalidate(parcelasProvider),
            child: const Text('Tentar novamente'),
          ),
        ],
      ),
    );
  }
}

class _ParcelaCard extends StatelessWidget {
  final ParcelaModel parcela;
  final NumberFormat fmt;
  final DateFormat fmtData;
  const _ParcelaCard({required this.parcela, required this.fmt, required this.fmtData});

  Color get _cor => switch (parcela.status) {
    'PAGA'     => const Color(0xFF4A7C59),
    'VENCIDA'  => Colors.red,
    'PENDENTE' => const Color(0xFFEA580C),
    _          => Colors.grey,
  };

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Row(
          children: [
            Container(
              width: 42, height: 42,
              decoration: BoxDecoration(
                color: _cor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(10),
              ),
              child: Icon(Icons.receipt_long, color: _cor, size: 20),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(parcela.descricao, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
                  const SizedBox(height: 2),
                  Text(
                    'Vence: ${fmtData.format(parcela.dataVencimento)}',
                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                  ),
                ],
              ),
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(fmt.format(parcela.valor), style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 14)),
                Container(
                  margin: const EdgeInsets.only(top: 4),
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: _cor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(parcela.status, style: TextStyle(fontSize: 10, color: _cor, fontWeight: FontWeight.w600)),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
