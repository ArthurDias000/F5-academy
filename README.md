# 📱 F5 Academy - App de Gerenciamento de Clientes

Aplicativo Android desenvolvido para gerenciar clientes e pagamentos de uma academia. Criado no Android Studio com Java, seguindo o padrão de Material Design, usando banco de dados local SQLite.

---

## 🖼️ Imagens do App

### Tela Inicial!

[tela_inicial png](https://github.com/user-attachments/assets/74f6c2f8-4ddf-4519-b981-50c44848269b)

### Cadastro de Cliente
![cadastro_cliente png](https://github.com/user-attachments/assets/53a38922-f910-462c-8dfd-10622bdfb915)

### Lista de Clientes

![tela_lista png](https://github.com/user-attachments/assets/331099c5-78be-4c2e-bd61-ee0b1698583f)

> Coloque seus prints de tela na pasta `imagens/` do repositório e substitua os nomes acima se necessário.

---

## ✅ Funcionalidades

- 📋 Cadastro de clientes com nome, CPF, telefone, datas e comorbidades
- 📆 Seleção de datas com `DatePickerDialog`
- 📞 Máscaras aplicadas nos campos de CPF e telefone
- 🧾 Lista com popup para **editar e excluir clientes**
- 💾 Dados salvos localmente com **SQLite**
- 🔍 Filtro e busca de clientes por nome ou CPF
- 🖌️ Interface moderna com **Material Design**

---

## 🛠️ Tecnologias Utilizadas

- Java
- Android Studio
- Material Components
- SQLite
- RecyclerView
- DatePickerDialog
- MaskFormatter (ou `TextWatcher` personalizado para CPF e telefone)

---

## 📦 Estrutura do Projeto

F5Academy/
├── app/
│ ├── java/com/example/f5acadmmy/
│ │ ├── MainActivity.java
│ │ ├── CadastroCliente.java
│ │ ├── listaClientes.java
│ │ └── BancoDeDados.java
│ └── res/
│ ├── layout/
│ │ ├── activity_cadastro_cliente.xml
│ │ ├── activity_lista_clientes.xml
│ │ └── item_cliente.xml
│ ├── values/
│ └── drawable/
├── imagens/
│ ├── tela_inicial.png
│ ├── cadastro_cliente.png
│ └── lista_clientes.png
├── .gitignore
└── README.md

## 🚀 Como Executar

1. Clone o projeto:
   ```bash
   git clone https://github.com/SEU_USUARIO/NOME_DO_REPOSITORIO.git


