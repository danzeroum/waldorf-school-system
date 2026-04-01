package com.waldorf.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSecretariaDTO {

    private long totalAlunosAtivos;
    private long matriculasAtivas;
    private long contratosPendentes;
    private long mensalidadesAtrasadas;
    private long lgpdPendentes;
    private long notificacoesNaoLidas;
    private long turmasAtivas;
}
