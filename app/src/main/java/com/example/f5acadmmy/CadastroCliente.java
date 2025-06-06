package com.example.f5acadmmy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CadastroCliente extends AppCompatActivity {

    // Campos de entrada
    private TextInputLayout layoutNome, layoutCpf, layoutTelefone, layoutValor;
    private TextInputLayout layoutDescricaoDoenca, layoutDataNascimento, layoutDataPagamento;
    private TextInputEditText edtNome, edtCpf, edtTelefone, edtValor;
    private TextInputEditText edtDescricaoDoenca, edtDataNascimento, edtDataPagamento;
    private RadioGroup radioDoenca;

    // Formatação de data e moeda
    private final Calendar calendar = Calendar.getInstance();
    private final Locale localeBR = new Locale("pt", "BR");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", localeBR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.cadastro_cliente);

        inicializarCampos();
        configurarCalendarios();
        configurarRadioGroup();

        aplicarMascaraMonetaria(edtValor);
        aplicarMascaraCpf(edtCpf);
        aplicarMascaraTelefone(edtTelefone);

        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCadastrar.setOnClickListener(v -> {
            if (validarCampos()) {
                Cliente cliente = new Cliente(
                        Objects.requireNonNull(edtNome.getText()).toString().trim(),
                        Objects.requireNonNull(edtCpf.getText()).toString().trim(),
                        Objects.requireNonNull(edtTelefone.getText()).toString().trim(),
                        Objects.requireNonNull(edtValor.getText()).toString().trim(),
                        Objects.requireNonNull(edtDataNascimento.getText()).toString().trim(),
                        Objects.requireNonNull(edtDataPagamento.getText()).toString().trim(),
                        (radioDoenca.getCheckedRadioButtonId() == R.id.rbSimDoenca)
                                ? Objects.requireNonNull(edtDescricaoDoenca.getText()).toString().trim()
                                : "Nenhuma"
                );

                if (salvarCliente(cliente)) {
                    listarClientesNoLog();
                    Toast.makeText(this, "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("cliente", cliente);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao cadastrar cliente.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Inicializa os campos de entrada
    private void inicializarCampos() {
        layoutNome = findViewById(R.id.layoutNome);
        layoutCpf = findViewById(R.id.layoutCpf);
        layoutTelefone = findViewById(R.id.layoutTelefone);
        layoutValor = findViewById(R.id.layoutValor);
        layoutDataNascimento = findViewById(R.id.layoutDataNascimento);
        layoutDataPagamento = findViewById(R.id.layoutDataPagamento);
        layoutDescricaoDoenca = findViewById(R.id.layoutDescricaoDoenca);

        edtNome = findViewById(R.id.edtNome);
        edtCpf = findViewById(R.id.edtCpf);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtValor = findViewById(R.id.edtValor);
        edtDataNascimento = findViewById(R.id.edtDataNascimento);
        edtDataPagamento = findViewById(R.id.edtDataPagamento);
        edtDescricaoDoenca = findViewById(R.id.edtDescricaoDoenca);

        radioDoenca = findViewById(R.id.radioDoenca);
    }

    // Configura os calendários para os campos de data
    private void configurarCalendarios() {
        edtDataNascimento.setOnClickListener(v -> showDatePickerForField(edtDataNascimento));
        layoutDataNascimento.setEndIconOnClickListener(v -> showDatePickerForField(edtDataNascimento));
        edtDataPagamento.setOnClickListener(v -> showDatePickerForField(edtDataPagamento));
        layoutDataPagamento.setEndIconOnClickListener(v -> showDatePickerForField(edtDataPagamento));
    }

    // Configura a exibição do campo "comorbidade"
    private void configurarRadioGroup() {
        atualizarVisibilidadeDoenca(radioDoenca.getCheckedRadioButtonId());
        radioDoenca.setOnCheckedChangeListener((group, checkedId) -> atualizarVisibilidadeDoenca(checkedId));
    }

    // Mostra o DatePickerDialog para o campo de data
    private void showDatePickerForField(TextInputEditText campo) {
        final Calendar tempCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            tempCalendar.set(year, month, dayOfMonth);
            campo.setText(dateFormat.format(tempCalendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (campo.getId() == R.id.edtDataNascimento) {
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        dialog.show();
    }

    // Mostra ou oculta campo de descrição de comorbidade
    private void atualizarVisibilidadeDoenca(int checkedId) {
        if (checkedId == R.id.rbSimDoenca) {
            layoutDescricaoDoenca.setVisibility(View.VISIBLE);
            edtDescricaoDoenca.requestFocus();
        } else {
            layoutDescricaoDoenca.setVisibility(View.GONE);
            edtDescricaoDoenca.setText("");
        }
    }

    // Valida todos os campos obrigatórios antes de salvar
    private boolean validarCampos() {
        boolean valido = true;

        if (Objects.requireNonNull(edtNome.getText()).toString().trim().isEmpty()) {
            layoutNome.setError("Preencha o nome completo");
            valido = false;
        } else layoutNome.setError(null);

        if (Objects.requireNonNull(edtCpf.getText()).toString().replaceAll("[^\\d]", "").length() != 11) {
            layoutCpf.setError("Informe um CPF válido");
            valido = false;
        } else layoutCpf.setError(null);

        if (Objects.requireNonNull(edtTelefone.getText()).toString().replaceAll("[^\\d]", "").length() < 10) {
            layoutTelefone.setError("Informe um telefone válido");
            valido = false;
        } else layoutTelefone.setError(null);

        if (Objects.requireNonNull(edtValor.getText()).toString().trim().isEmpty()) {
            layoutValor.setError("Informe o valor a ser pago");
            valido = false;
        } else layoutValor.setError(null);

        if (Objects.requireNonNull(edtDataNascimento.getText()).toString().trim().isEmpty()) {
            layoutDataNascimento.setError("Selecione a data de nascimento");
            valido = false;
        } else layoutDataNascimento.setError(null);

        if (Objects.requireNonNull(edtDataPagamento.getText()).toString().trim().isEmpty()) {
            layoutDataPagamento.setError("Selecione a data de pagamento");
            valido = false;
        } else layoutDataPagamento.setError(null);

        if (radioDoenca.getCheckedRadioButtonId() == R.id.rbSimDoenca) {
            if (Objects.requireNonNull(edtDescricaoDoenca.getText()).toString().trim().isEmpty()) {
                layoutDescricaoDoenca.setError("Descreva as comorbidades");
                valido = false;
            } else layoutDescricaoDoenca.setError(null);
        } else layoutDescricaoDoenca.setError(null);

        return valido;
    }

    // Máscara para valor monetário
    private void aplicarMascaraMonetaria(TextInputEditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    campo.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");
                    try {
                        double parsed = Double.parseDouble(cleanString) / 100.0;
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeBR);
                        String formatted = format.format(parsed);
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

    // Máscara para CPF
    private void aplicarMascaraCpf(TextInputEditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String str = s.toString().replaceAll("[^\\d]", "");
                if (isUpdating) { isUpdating = false; return; }
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < str.length() && i < 11; i++) {
                    if (i == 3 || i == 6) formatted.append(".");
                    else if (i == 9) formatted.append("-");
                    formatted.append(str.charAt(i));
                }
                isUpdating = true;
                campo.setText(formatted.toString());
                campo.setSelection(formatted.length());
            }
        });
    }

    // Máscara para telefone
    private void aplicarMascaraTelefone(TextInputEditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String str = s.toString().replaceAll("[^\\d]", "");
                if (isUpdating) { isUpdating = false; return; }
                StringBuilder formatted = new StringBuilder();
                if (str.length() >= 1) formatted.append("(");
                for (int i = 0; i < str.length() && i < 11; i++) {
                    if (i == 2) formatted.append(") ");
                    else if (i == 7) formatted.append("-");
                    formatted.append(str.charAt(i));
                }
                isUpdating = true;
                campo.setText(formatted.toString());
                campo.setSelection(formatted.length());
            }
        });
    }

    // Salva cliente no banco de dados
    private boolean salvarCliente(Cliente cliente) {
        BancoDeDados db = new BancoDeDados(this);
        long id = db.inserirCliente(cliente);
        return id != -1;
    }

    // Lista todos os clientes no log (debug)
    private void listarClientesNoLog() {
        BancoDeDados db = new BancoDeDados(this);
        db.listarClientes();
    }
}