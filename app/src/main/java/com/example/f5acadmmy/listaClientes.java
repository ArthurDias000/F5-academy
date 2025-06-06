package com.example.f5acadmmy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class listaClientes extends AppCompatActivity {
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Cliente> listaClientes;
    private BancoDeDados bancoDeDados;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final int REQUISICAO_CADASTRO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_de_clientes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bancoDeDados = new BancoDeDados(this);
        listaClientes = new ArrayList<>(bancoDeDados.buscarTodosClientes());

        recyclerView = findViewById(R.id.Tela1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(listaClientes);
        recyclerView.setAdapter(adapter);

        fabAdd = findViewById(R.id.floatingActionButton2);
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(listaClientes.this, CadastroCliente.class);
            startActivityForResult(intent, REQUISICAO_CADASTRO);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recarregarClientes();
    }

    private void recarregarClientes() {
        listaClientes.clear();
        listaClientes.addAll(bancoDeDados.buscarTodosClientes());
        adapter.notifyDataSetChanged();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        private final List<Cliente> itens;

        MyAdapter(List<Cliente> itens) {
            this.itens = itens;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_lista, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            Cliente cliente = itens.get(position);
            holder.textItem.setText(cliente.nome);

            holder.itemView.setOnClickListener(v -> abrirDialogVisualizacao(cliente));

            holder.itemView.setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add(Menu.NONE, 1, 1, "Atualizar");
                popup.getMenu().add(Menu.NONE, 2, 2, "Excluir");

                popup.setOnMenuItemClickListener(item -> {
                    int pos = holder.getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return false;

                    if (item.getItemId() == 1) {
                        abrirDialogEdicao(cliente, pos);
                        return true;
                    } else if (item.getItemId() == 2) {
                        new AlertDialog.Builder(listaClientes.this)
                                .setTitle("Confirmar exclusão")
                                .setMessage("Tem certeza que deseja excluir o cliente '" + cliente.nome + "'?")
                                .setPositiveButton("Excluir", (dialog, which) -> {
                                    Cliente clienteRemovido = cliente;
                                    String cpfRemovido = cliente.cpf;
                                    itens.remove(pos);
                                    notifyItemRemoved(pos);

                                    Snackbar.make(recyclerView, "Cliente removido: " + clienteRemovido.nome, Snackbar.LENGTH_LONG)
                                            .setAction("Desfazer", v1 -> {
                                                bancoDeDados.inserirCliente(clienteRemovido);
                                                itens.add(pos, clienteRemovido);
                                                notifyItemInserted(pos);
                                                Toast.makeText(listaClientes.this, "Cliente restaurado", Toast.LENGTH_SHORT).show();
                                            }).show();

                                    bancoDeDados.excluirClientePorCpf(cpfRemovido);
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                        return true;
                    }
                    return false;
                });
                popup.show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return itens.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView textItem;

            MyHolder(View itemView) {
                super(itemView);
                textItem = itemView.findViewById(R.id.textItem);
            }
        }

        private void abrirDialogVisualizacao(Cliente cliente) {
            View dialogView = LayoutInflater.from(listaClientes.this).inflate(R.layout.dialog_editar_cliente, null);
            EditText[] campos = preencherCampos(dialogView, cliente);

            campos[1].post(() -> aplicarMascaraCpf(campos[1]));
            campos[2].post(() -> aplicarMascaraTelefone(campos[2]));
            campos[3].post(() -> aplicarMascaraValor(campos[3]));

            for (EditText campo : campos) {
                campo.setEnabled(false);
                campo.setFocusable(false);
                campo.setClickable(false);
                if (campo.getText().toString().trim().isEmpty()) {
                    campo.setHint("");
                    campo.setText("Não informado");
                    campo.setTextColor(ContextCompat.getColor(listaClientes.this, android.R.color.darker_gray));
                }
            }

            dialogView.findViewById(R.id.btnSalvar).setVisibility(View.GONE);
            new AlertDialog.Builder(listaClientes.this).setView(dialogView).create().show();
        }
    }

    private void abrirDialogEdicao(Cliente cliente, int pos) {
        View dialogView = LayoutInflater.from(listaClientes.this).inflate(R.layout.dialog_editar_cliente, null);
        EditText[] campos = preencherCampos(dialogView, cliente);

        campos[1].post(() -> aplicarMascaraCpf(campos[1]));
        campos[2].post(() -> aplicarMascaraTelefone(campos[2]));
        campos[2].setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); // Limita caracteres visíveis com máscara
        campos[3].post(() -> aplicarMascaraValor(campos[3]));

        campos[4].setFocusable(false);
        campos[4].setOnClickListener(v -> showDatePicker(campos[4], true));
        campos[5].setFocusable(false);
        campos[5].setOnClickListener(v -> showDatePicker(campos[5], false));

        String cpfOriginal = cliente.cpf;

        AlertDialog dialog = new AlertDialog.Builder(listaClientes.this).setView(dialogView).create();
        dialog.show();

        MaterialButton btnSalvar = dialogView.findViewById(R.id.btnSalvar);
        btnSalvar.setVisibility(View.VISIBLE);
        btnSalvar.setOnClickListener(v -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar nascimento = Calendar.getInstance();
                nascimento.setTime(sdf.parse(campos[4].getText().toString().trim()));

                if (nascimento.after(Calendar.getInstance())) {
                    Toast.makeText(listaClientes.this, "A data de nascimento não pode ser no futuro.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String nomeDigitado = campos[0].getText().toString().trim();
                if (nomeDigitado.isEmpty()) {
                    Toast.makeText(listaClientes.this, "O nome não pode ser vazio.", Toast.LENGTH_SHORT).show();
                    return;
                }

                cliente.nome = nomeDigitado;
                cliente.cpf = campos[1].getText().toString().replaceAll("[^\\d]", "").trim();
                cliente.telefone = campos[2].getText().toString().replaceAll("[^\\d]", "").trim();
                cliente.valor = campos[3].getText().toString().replaceAll("[^\\d]", "").trim();
                cliente.dataNascimento = campos[4].getText().toString().trim();
                cliente.dataPagamento = campos[5].getText().toString().trim();
                cliente.doenca = campos[6].getText().toString().trim();

                bancoDeDados.atualizarCliente(cpfOriginal, cliente);
                adapter.notifyItemChanged(pos);
                Toast.makeText(listaClientes.this, "Dados atualizados!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(listaClientes.this, "Data inválida.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private EditText[] preencherCampos(View view, Cliente cliente) {
        EditText nome = view.findViewById(R.id.editNome);
        EditText cpf = view.findViewById(R.id.editCpf);
        EditText telefone = view.findViewById(R.id.editTelefone);
        EditText valor = view.findViewById(R.id.editValor);
        EditText nascimento = view.findViewById(R.id.editNascimento);
        EditText pagamento = view.findViewById(R.id.editPagamento);
        EditText doenca = view.findViewById(R.id.editDoenca);

        nome.setText(cliente.nome);
        cpf.setText(cliente.cpf);
        telefone.setText(cliente.telefone);
        valor.setText(cliente.valor);
        nascimento.setText(cliente.dataNascimento);
        pagamento.setText(cliente.dataPagamento);
        doenca.setText(cliente.doenca);

        return new EditText[]{nome, cpf, telefone, valor, nascimento, pagamento, doenca};
    }

    private void aplicarMascaraCpf(EditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formatted = new StringBuilder();

                int i = 0;
                while (i < str.length() && i < 11) {
                    if (i == 3 || i == 6) formatted.append(".");
                    else if (i == 9) formatted.append("-");
                    formatted.append(str.charAt(i));
                    i++;
                }

                isUpdating = true;
                campo.setText(formatted.toString());
                campo.setSelection(formatted.length());
            }
        });
    }

    private void aplicarMascaraTelefone(EditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = s.toString().replaceAll("[^\\d]", "");
                if (str.length() > 11) {
                    str = str.substring(0, 11);
                }

                StringBuilder formatted = new StringBuilder();
                int len = str.length();
                if (len >= 2) {
                    formatted.append("(").append(str.substring(0, 2)).append(") ");
                    if (len > 2 && len <= 6)
                        formatted.append(str.substring(2));
                    else if (len <= 10)
                        formatted.append(str.substring(2, 6)).append("-").append(str.substring(6));
                    else if (len > 10)
                        formatted.append(str.substring(2, 7)).append("-").append(str.substring(7));
                } else {
                    formatted.append(str);
                }

                isUpdating = true;
                campo.setText(formatted.toString());
                campo.setSelection(formatted.length());
            }
        });
    }

    private void aplicarMascaraValor(EditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    campo.removeTextChangedListener(this);

                    String clean = s.toString().replaceAll("[R$,.\\s]", "");
                    if (clean.isEmpty()) clean = "0";

                    try {
                        double parsed = Double.parseDouble(clean) / 100.0;
                        String formatted = String.format(Locale.getDefault(), "R$ %,.2f", parsed);

                        current = formatted;
                        campo.setText(formatted);
                        campo.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        campo.setText("");
                    }

                    campo.addTextChangedListener(this);
                }
            }
        });
    }

    private void showDatePicker(EditText campo, boolean isNascimento) {
        Calendar calendario = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendario.set(year, month, dayOfMonth);
            campo.setText(dateFormat.format(calendario.getTime()));
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
    }
}
