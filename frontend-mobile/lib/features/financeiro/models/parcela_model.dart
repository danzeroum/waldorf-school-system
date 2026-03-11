class ParcelaModel {
  final int      id;
  final String   descricao;
  final double   valor;
  final DateTime dataVencimento;
  final String   status;
  final String?  formaPagamento;
  final bool     offline;

  const ParcelaModel({
    required this.id,
    required this.descricao,
    required this.valor,
    required this.dataVencimento,
    required this.status,
    this.formaPagamento,
    this.offline = false,
  });

  factory ParcelaModel.fromJson(Map<String, dynamic> j) => ParcelaModel(
    id:             j['id'] as int,
    descricao:      j['descricao'] as String,
    valor:          (j['valor'] as num).toDouble(),
    dataVencimento: DateTime.parse(j['dataVencimento'] as String),
    status:         j['status'] as String,
    formaPagamento: j['formaPagamento'] as String?,
  );

  Map<String, dynamic> toMap() => {
    'id': id, 'descricao': descricao, 'valor': valor,
    'dataVencimento': dataVencimento.toIso8601String(),
    'status': status, 'formaPagamento': formaPagamento ?? '',
  };

  factory ParcelaModel.fromMap(Map<String, dynamic> m) => ParcelaModel(
    id:             m['id'] as int,
    descricao:      m['descricao'] as String,
    valor:          m['valor'] as double,
    dataVencimento: DateTime.parse(m['dataVencimento'] as String),
    status:         m['status'] as String,
    formaPagamento: m['formaPagamento'] as String?,
    offline:        true,
  );
}
