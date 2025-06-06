package com.example.f5acadmmy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class cadastro extends AppCompatActivity {

    // Campos de entrada do formulário
    private EditText nome;
    private EditText email;
    private EditText senha;
    private EditText confirmar_senha;

    // Referência para o banco de dados
    private BancoDeDados BD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ativa o modo edge-to-edge (tela inteira moderna)
        setContentView(R.layout.activity_cadastro); // Define o layout da tela

        // Ajusta o padding para evitar sobreposição com barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Conecta os campos do layout com as variáveis do Java
        nome = findViewById(R.id.camponome);
        email = findViewById(R.id.campologin);
        senha = findViewById(R.id.camposenha);
        confirmar_senha = findViewById(R.id.campo_confirmar_senha);
        BD = new BancoDeDados(this); // Inicializa o banco de dados
    }

    // Método chamado ao clicar no botão "Cadastrar"
    public void cb_cadastro(View v) {
        // Lê os dados dos campos e remove espaços extras
        String n = nome.getText().toString().trim();
        String e = email.getText().toString().trim().toLowerCase();
        String s = senha.getText().toString().trim();
        String cs = confirmar_senha.getText().toString().trim();

        // Validação: todos os campos devem estar preenchidos
        if (n.isEmpty() || e.isEmpty() || s.isEmpty() || cs.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            return;
        }

        // Validação: senha e confirmação devem ser iguais
        if (!s.equals(cs)) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_LONG).show();
            return;
        }

        // Cria um novo objeto Usuario
        Usuario novoUsuario = new Usuario(0, n, e, s);

        // Tenta inserir o usuário no banco
        long id = BD.inserirUsuario(novoUsuario);

        if (id != -1) {
            // Sucesso: mostra mensagem e volta para a tela de login
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();
            Intent t = new Intent(this, login.class);
            startActivity(t);
            finish();
        } else {
            // Falha: e-mail já cadastrado
            Toast.makeText(this, "Erro ao cadastrar! E-mail já existe!", Toast.LENGTH_LONG).show();
        }
    }
}
