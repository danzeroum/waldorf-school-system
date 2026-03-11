import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppTheme {
  static const _green  = Color(0xFF4A7C59);
  static const _cream  = Color(0xFFF5F0E8);
  static const _brown  = Color(0xFF6B4423);
  static const _gray900 = Color(0xFF1A1A2E);

  static ThemeData get light => ThemeData(
    useMaterial3: true,
    colorScheme: ColorScheme.fromSeed(
      seedColor: _green,
      background: _cream,
      surface: Colors.white,
    ),
    scaffoldBackgroundColor: _cream,
    textTheme: GoogleFonts.notoSansTextTheme().copyWith(
      headlineMedium: GoogleFonts.notoSerif(fontWeight: FontWeight.w600, color: _gray900),
      titleLarge:     GoogleFonts.notoSerif(fontWeight: FontWeight.w600, color: _gray900),
    ),
    appBarTheme: AppBarTheme(
      backgroundColor: Colors.white,
      foregroundColor: _gray900,
      elevation: 0,
      centerTitle: false,
      titleTextStyle: GoogleFonts.notoSerif(
        fontSize: 18, fontWeight: FontWeight.w600, color: _gray900,
      ),
    ),
    cardTheme: CardTheme(
      color: Colors.white,
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: Color(0xFFE8E0D0)),
      ),
      margin: const EdgeInsets.symmetric(vertical: 4),
    ),
    filledButtonTheme: FilledButtonThemeData(
      style: FilledButton.styleFrom(
        backgroundColor: _green,
        foregroundColor: Colors.white,
        minimumSize: const Size(double.infinity, 52),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        textStyle: const TextStyle(fontSize: 15, fontWeight: FontWeight.w600),
      ),
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: Colors.white,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFDDD5C5)),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFDDD5C5)),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: _green, width: 2),
      ),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
    ),
  );
}
