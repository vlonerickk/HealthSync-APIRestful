package br.com.healthsync.model;

import java.time.LocalDateTime;

public class Agendamento {
    private long id;
    private Paciente paciente;
    private Especialidade especialidade;
    private LocalDateTime dataHora;
    private String nomeMedico;

    // Construtores, Getters e Setters
    public Agendamento() {}

    public Agendamento(Paciente paciente, Especialidade especialidade, LocalDateTime dataHora, String nomeMedico) {
        this.paciente = paciente;
        this.especialidade = especialidade;
        this.dataHora = dataHora;
        this.nomeMedico = nomeMedico;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public Especialidade getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    public String getNomeMedico() {
        return nomeMedico;
    }
    public void setNomeMedico(String nomeMedico) {
        this.nomeMedico = nomeMedico;
    }

    @Override
    public String toString() {
        return "Agendamento{" + "id=" + id + ", paciente=" + paciente.getNome() + ", especialidade=" + especialidade + ", dataHora=" + dataHora + ", medico='" + nomeMedico + '\'' + '}';
    }
}