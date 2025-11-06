package br.com.healthsync.model;

import java.time.LocalDate;

public class Paciente {
    private long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;

    // Construtores, Getters e Setters
    public Paciente() {
        super();
    }

    public Paciente(String nome, String cpf, LocalDate dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return "Paciente{" + "id=" + id + ", nome='" + nome + '\'' + ", cpf='" + cpf + '\'' + ", dataNascimento=" + dataNascimento + '}';
    }
}