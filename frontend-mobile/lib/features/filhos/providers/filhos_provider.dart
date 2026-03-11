import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../../../core/network/api_client.dart';
import '../models/aluno_model.dart';

part 'filhos_provider.g.dart';

@riverpod
Future<List<AlunoModel>> filhos(FilhosRef ref) async {
  final api    = ref.read(apiClientProvider);
  final dados  = await api.listarFilhos();
  return dados.map((d) => AlunoModel.fromJson(d as Map<String, dynamic>)).toList();
}

@riverpod
Future<AlunoModel> alunoDetalhe(AlunoDetalheRef ref, int id) async {
  final api  = ref.read(apiClientProvider);
  final dado = await api.buscarAluno(id);
  return AlunoModel.fromJson(dado);
}
