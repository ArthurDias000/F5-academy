package com.example.f5acadmmy;

import java.io.Serializable;

public class Cliente implements Serializable {
    public String nome;
    public String cpf;
    public String telefone;
    public String valor;
    public String dataNascimento;
    public String dataPagamento;
    public String doenca;

    // Construtor vazio necessário para uso com Firebase, SQLite e reflexão
    public Cliente() {
    }

    // Construtor com todos os campos
    public Cliente(String nome, String cpf, String telefone, String valor,
                   String dataNascimento, String dataPagamento, String doenca) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.valor = valor;
        this.dataNascimento = dataNascimento;
        this.dataPagamento = dataPagamento;
        this.doenca = doenca;
    }
}
