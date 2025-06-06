package com.example.f5acadmmy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BancoDeDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "banco_de_dados";
    private static final int VERSAO = 3;

    private static final String TABELA_CLIENTE = "cliente";
    private static final String TABELA_USUARIO = "usuario";

    public BancoDeDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabela de clientes
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABELA_CLIENTE + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL, " +
                        "cpf TEXT NOT NULL UNIQUE, " +
                        "telefone TEXT, " +
                        "valor TEXT, " +
                        "nascimento TEXT, " +
                        "pagamento TEXT, " +
                        "doenca TEXT)"
        );

        // Tabela de usuários (login)
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABELA_USUARIO + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "senha TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Em caso de atualização futura
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_CLIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIO);
        onCreate(db);
    }

    // ========================= CLIENTES ===========================

    public long inserirCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("nome", cliente.nome);
        valores.put("cpf", cliente.cpf);
        valores.put("telefone", cliente.telefone);
        valores.put("valor", cliente.valor);
        valores.put("nascimento", cliente.dataNascimento);
        valores.put("pagamento", cliente.dataPagamento);
        valores.put("doenca", cliente.doenca);

        return db.insert(TABELA_CLIENTE, null, valores);
    }

    public List<Cliente> buscarTodosClientes() {
        List<Cliente> clientes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_CLIENTE + " ORDER BY nome ASC", null);

        if (cursor.moveToFirst()) {
            do {
                Cliente cliente = new Cliente(
                        cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                        cursor.getString(cursor.getColumnIndexOrThrow("cpf")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("valor")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nascimento")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pagamento")),
                        cursor.getString(cursor.getColumnIndexOrThrow("doenca"))
                );
                clientes.add(cliente);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return clientes;
    }

    // Atualiza todos os dados com base no CPF original
    public int atualizarCliente(String cpfOriginal, Cliente clienteAtualizado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("nome", clienteAtualizado.nome);
        valores.put("cpf", clienteAtualizado.cpf);
        valores.put("telefone", clienteAtualizado.telefone);
        valores.put("valor", clienteAtualizado.valor);
        valores.put("nascimento", clienteAtualizado.dataNascimento);
        valores.put("pagamento", clienteAtualizado.dataPagamento);
        valores.put("doenca", clienteAtualizado.doenca);

        return db.update(TABELA_CLIENTE, valores, "cpf = ?", new String[]{cpfOriginal});
    }

    public int excluirClientePorCpf(String cpf) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhasAfetadas = db.delete(TABELA_CLIENTE, "cpf = ?", new String[]{cpf});

        if (linhasAfetadas > 0) {
            Log.d("CLIENTE_LOG", "Cliente com CPF " + cpf + " foi excluído com sucesso.");
        } else {
            Log.d("CLIENTE_LOG", "Nenhum cliente encontrado com CPF " + cpf + " para exclusão.");
        }

        return linhasAfetadas;
    }

    public Cliente buscarClientePorCpf(String cpf) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_CLIENTE + " WHERE cpf = ?", new String[]{cpf});

        Cliente cliente = null;
        if (cursor.moveToFirst()) {
            cliente = new Cliente(
                    cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cpf")),
                    cursor.getString(cursor.getColumnIndexOrThrow("telefone")),
                    cursor.getString(cursor.getColumnIndexOrThrow("valor")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nascimento")),
                    cursor.getString(cursor.getColumnIndexOrThrow("pagamento")),
                    cursor.getString(cursor.getColumnIndexOrThrow("doenca"))
            );
        }

        cursor.close();
        return cliente;
    }

    // Exibe todos os dados dos clientes cadastrados no Logcat (para testes)
    public void listarClientes() {
        List<Cliente> lista = buscarTodosClientes();
        if (lista.isEmpty()) {
            Log.d("CLIENTE_LOG", "Nenhum cliente cadastrado.");
            return;
        }

        for (Cliente c : lista) {
            Log.d("CLIENTE_LOG",
                    "Nome: " + c.nome +
                            ", CPF: " + c.cpf +
                            ", Telefone: " + c.telefone +
                            ", Valor: " + c.valor +
                            ", Nascimento: " + c.dataNascimento +
                            ", Pagamento: " + c.dataPagamento +
                            ", Doença: " + c.doenca);
        }
    }

    // ========================= USUÁRIOS ===========================

    public long inserirUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verifica se já existe usuário com o e-mail
        if (verificarUsuarioPorEmail(usuario.getEmail()) != null) {
            return -1; // E-mail já existe
        }

        ContentValues valores = new ContentValues();
        valores.put("nome", usuario.getNome());
        valores.put("email", usuario.getEmail());
        valores.put("senha", usuario.getSenha());

        return db.insert(TABELA_USUARIO, null, valores);
    }

    public Usuario verificarUsuarioPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_USUARIO + " WHERE email = ?", new String[]{email});

        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = new Usuario(
                    cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("senha"))
            );
        }

        cursor.close();
        return usuario;
    }

/*
    // Método para apagar todos os usuários e exibir os nomes no Log
    public void apagarTodosUsuarios() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Passo 1: Recuperar todos os usuários antes de deletar
        Cursor cursor = db.query(TABELA_USUARIO, null, null, null, null, null, null);
        StringBuilder usuariosApagados = new StringBuilder("Usuários apagados:\n");

        if (cursor.moveToFirst()) {
            do {
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                usuariosApagados.append("- ").append(nome).append(" (").append(email).append(")\n");
            } while (cursor.moveToNext());
        } else {
            usuariosApagados.append("Nenhum usuário encontrado.\n");
        }
        cursor.close();

        // Passo 2: Deletar todos os usuários
        int linhas = db.delete(TABELA_USUARIO, null, null);

        // Passo 3: Log com os dados dos usuários deletados
        Log.d("BANCO_DEBUG", usuariosApagados.toString());
        Log.d("BANCO_DEBUG", "Total de usuários apagados: " + linhas);
    }
*/

    // Exibe todos os usuários cadastrados no Logcat (para testes)
    // Exibe todos os usuários cadastrados no Logcat (para testes)

    /*
    public void listarUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_USUARIO, null);

        if (cursor.moveToFirst()) {
            do {
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String senha = cursor.getString(cursor.getColumnIndexOrThrow("senha"));
                Log.d("USUARIO_LOG", "Nome: " + nome + ", Email: " + email + ", Senha: " + senha);
            } while (cursor.moveToNext());
        } else {
            Log.d("USUARIO_LOG", "Nenhum usuário cadastrado.");
        }

        cursor.close();
    } */


}
