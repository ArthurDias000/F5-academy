package com.example.f5acadmmy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    // Campos da tela de login
    private EditText login;
    private EditText senha;
    private CheckBox checkboxLembrar;

    // Instância do banco de dados
    private BancoDeDados BD;

    // Nome do arquivo de preferências (para lembrar login)
    private static final String PREFS_NAME = "PrefsLogin";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Deixa a tela fullscreen (sem barra de status)

        setContentView(R.layout.login_main); // Define o layout da tela

        // Ajusta os paddings para não sobrepor conteúdo com barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Conecta os componentes do layout com o código
        login = findViewById(R.id.campologin);
        senha = findViewById(R.id.camposenha);
        checkboxLembrar = findViewById(R.id.checkboxLembrar);

        // Inicializa o banco de dados
        BD = new BancoDeDados(this);

        // Carrega as credenciais salvas (se houver)
        carregarPreferencias();

        //////// TRECHO DE CÓDIGO USADO APENAS POR ADMINISTRADORES //////
       //  BD.listarUsuarios(); // Exibe todos os usuários no Logcat (para testes)
        // BD.apagarTodosUsuarios(); // Apaga todos os usuários do banco (use com cuidado!)
    }

    // Método executado ao clicar no botão "Entrar"
    public void entrar(View v) {
        String email = login.getText().toString().trim().toLowerCase();
        String senhaDigitada = senha.getText().toString().trim();

        // Verifica se os campos estão preenchidos
        if (email.isEmpty() || senhaDigitada.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Abertura de conexão de leitura com o banco
        SQLiteDatabase conexao = BD.getReadableDatabase();

        try {
            // Consulta se existe usuário com esse e-mail e senha
            Cursor cursor = conexao.rawQuery(
                    "SELECT count(_id) FROM usuario WHERE email = ? AND senha = ?",
                    new String[]{email, senhaDigitada}
            );

            cursor.moveToFirst();
            int resultado = cursor.getInt(0);
            cursor.close();
            conexao.close();

            if (resultado > 0) {
                // Se achou o usuário, salva as preferências (se marcado) e vai para a tela principal
                salvarPreferencias(email, senhaDigitada, checkboxLembrar.isChecked());

                Intent intent = new Intent(this, listaClientes.class);
                startActivity(intent);
                finish();
            } else {
                // Usuário não encontrado
                Toast.makeText(this, "Usuário não encontrado!\nCrie uma conta!", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro de conexão!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Abre a tela de cadastro
    public void cadastrar(View v) {
        Intent intent = new Intent(this, cadastro.class);
        startActivity(intent);
    }

    // Salva o e-mail e senha localmente (se o checkbox estiver marcado)
    private void salvarPreferencias(String email, String senha, boolean lembrar) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (lembrar) {
            editor.putString("email", email);
            editor.putString("senha", senha);
            editor.putBoolean("lembrar", true);
        } else {
            editor.clear(); // Se não for pra lembrar, apaga qualquer dado salvo
        }

        editor.apply(); // Aplica as mudanças
    }

    // Carrega os dados salvos (se o usuário marcou "lembrar")
    private void carregarPreferencias() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean lembrar = preferences.getBoolean("lembrar", false);

        if (lembrar) {
            String email = preferences.getString("email", "");
            String senhaSalva = preferences.getString("senha", "");
            login.setText(email);
            senha.setText(senhaSalva);
            checkboxLembrar.setChecked(true); // Marca o checkbox novamente
        }
    }
}
