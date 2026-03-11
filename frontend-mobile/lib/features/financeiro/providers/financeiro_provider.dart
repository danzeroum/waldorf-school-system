import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../../../core/network/api_client.dart';
import '../models/parcela_model.dart';
import '../services/financeiro_local_db.dart';

part 'financeiro_provider.g.dart';

@riverpod
Future<List<ParcelaModel>> parcelas(ParcelasRef ref, String? status) async {
  final api = ref.read(apiClientProvider);
  final db  = FinanceiroLocalDb.instance;
  try {
    final dados   = await api.listarParcelas(status: status);
    final parcelas = dados.map((d) => ParcelaModel.fromJson(d as Map<String, dynamic>)).toList();
    // Persiste localmente para offline
    await db.upsertParcelas(parcelas);
    return parcelas;
  } catch (_) {
    // Falha de rede → retorna cache local
    return db.listarParcelas(status: status);
  }
}
