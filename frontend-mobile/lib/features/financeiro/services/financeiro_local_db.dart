import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import '../models/parcela_model.dart';

class FinanceiroLocalDb {
  static final FinanceiroLocalDb instance = FinanceiroLocalDb._();
  FinanceiroLocalDb._();
  Database? _db;

  Future<Database> get db async {
    _db ??= await _inicializar();
    return _db!;
  }

  Future<Database> _inicializar() async {
    final caminho = join(await getDatabasesPath(), 'waldorf_financeiro.db');
    return openDatabase(
      caminho,
      version: 1,
      onCreate: (db, _) => db.execute('''
        CREATE TABLE parcelas (
          id             INTEGER PRIMARY KEY,
          descricao      TEXT NOT NULL,
          valor          REAL NOT NULL,
          dataVencimento TEXT NOT NULL,
          status         TEXT NOT NULL,
          formaPagamento TEXT
        )
      '''),
    );
  }

  Future<void> upsertParcelas(List<ParcelaModel> parcelas) async {
    final database = await db;
    final batch = database.batch();
    for (final p in parcelas) {
      batch.insert('parcelas', p.toMap(), conflictAlgorithm: ConflictAlgorithm.replace);
    }
    await batch.commit(noResult: true);
  }

  Future<List<ParcelaModel>> listarParcelas({String? status}) async {
    final database = await db;
    final rows = await database.query(
      'parcelas',
      where: status != null ? 'status = ?' : null,
      whereArgs: status != null ? [status] : null,
      orderBy: 'dataVencimento ASC',
    );
    return rows.map(ParcelaModel.fromMap).toList();
  }
}
