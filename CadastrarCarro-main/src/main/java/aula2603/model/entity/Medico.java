package aula2603.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id_pessoa")
public class Medico extends Pessoa implements Serializable {

    @Column(name = "crm", nullable = false, unique = true)
    @NotBlank(message = "CRM é obrigatório")
    @Pattern(regexp = "CRM-\\d{1,5}", message = "Formato inválido. Use CRM-XXXXX (até 5 dígitos numéricos)")
    private String crm;

    @NotBlank(message = "Especialidade é obrigatória")
    private String especialidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMedico status = StatusMedico.ATIVO;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.PERSIST)
    private List<jpa.aula.model.entity.Consulta> consultaList;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.PERSIST)
    private List<Disponibilidade> disponibilidadeLista;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.PERSIST)
    private List<Agenda> agendaList;

    // Método personalizado (não precisa de getter/setter por causa do Lombok)
    public boolean isMedico() {
        return true;
    }

    public String dados() {
        return "Dr(a). " + getNome() + " - CRM: " + crm;
    }
}
