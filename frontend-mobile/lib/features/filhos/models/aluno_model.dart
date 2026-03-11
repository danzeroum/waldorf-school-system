class AlunoModel {
  final int    id;
  final String matricula;
  final String nome;
  final String? turmaNome;
  final int    anoIngresso;
  final String? temperamento;
  final bool   ativo;

  const AlunoModel({
    required this.id,
    required this.matricula,
    required this.nome,
    this.turmaNome,
    required this.anoIngresso,
    this.temperamento,
    required this.ativo,
  });

  factory AlunoModel.fromJson(Map<String, dynamic> j) => AlunoModel(
    id:           j['id'] as int,
    matricula:    j['matricula'] as String,
    nome:         j['nome'] as String,
    turmaNome:    j['turmaNome'] as String?,
    anoIngresso:  j['anoIngresso'] as int,
    temperamento: j['temperamento'] as String?,
    ativo:        j['ativo'] as bool? ?? true,
  );
}
