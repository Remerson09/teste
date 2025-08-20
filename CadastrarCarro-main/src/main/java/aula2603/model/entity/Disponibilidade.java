package aula2603.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medico_id_pessoa", nullable = false)
    private Medico medico;

    @NotNull
    @Column(name = "data_inicial")
    private LocalDate dataInicial;

    @NotNull
    @Column(name = "data_final")
    private LocalDate dataFinal;

    @NotNull
    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @NotNull
    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    private LocalTime intervaloInicio;
    private LocalTime intervaloFim;

    @NotNull
    @Column(name = "tempo_consulta")
    private LocalTime tempoConsulta;

}