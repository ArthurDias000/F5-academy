# 📱 F5 Academy - App de Gerenciamento de Clientes

Aplicativo Android desenvolvido para gerenciar clientes e pagamentos de uma academia. Criado no Android Studio com Java, seguindo o padrão de Material Design, usando banco de dados local SQLite.

---

## 🖼️ Imagens do App

OBS: foi colocado apenas 3 prints das telas para demostração

### Tela Inicial
<img src="https://github.com/user-attachments/assets/0873869c-6173-41ab-8bc1-3db795a244cc" width="300"/>

### Cadastro de Cliente
<img src="https://github.com/user-attachments/assets/53a38922-f910-462c-8dfd-10622bdfb915" width="300"/>

### Lista de Clientes
<img src="https://github.com/user-attachments/assets/331099c5-78be-4c2e-bd61-ee0b1698583f" width="300"/>


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






