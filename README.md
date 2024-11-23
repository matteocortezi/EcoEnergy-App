# EcoEnergy App

O **EcoEnergy App** é uma aplicação que ajuda os usuários a economizar energia, oferecendo funcionalidades para o cadastro e acompanhamento de contas de energia, além de práticas para redução de consumo. O projeto utiliza as tecnologias **Kotlin** e **Firebase**.

---

## ⚙️ Tecnologias Utilizadas
- **Kotlin**
- **Firebase**

---

## 🗂 Organização do Projeto

Após baixar o repositório, **separe a pasta `Api-kotlin` do restante do projeto**. Isso é necessário porque a API e o App precisam ser executados separadamente. Caso não faça isso, o aplicativo não funcionará corretamente.

---

## 📱 Funcionalidades do App

### **Telas**
1. **Cadastro de Usuário**
   - Permite que o usuário se cadastre no sistema. 
   - **Recomendação:** Use um email válido para que a funcionalidade de recuperação de senha funcione corretamente.

2. **Login**
   - Tela onde o usuário pode acessar o app ao inserir suas credenciais.
   - Também oferece acesso às telas de **Cadastro de Usuário** e **Esqueci a Senha**.

3. **Esqueci a Senha**
   - Permite ao usuário recuperar sua senha ao informar o email cadastrado.
   - Após isso, um email será enviado para redefinição da senha, e o usuário será redirecionado para a tela de login.

4. **Menu**
   - Exibe práticas, mudanças e consumo básico para economia de energia.
   - Possui um botão para cadastrar contas de energia.
   - Inclui uma **Barra de Progresso**, que indica a porcentagem de economia de energia comparando a conta do mês atual com a do mês anterior.

5. **Contas**
   - Exibe todas as contas de energia cadastradas.
   - Permite apagar ou editar as contas.

6. **Perfil**
   - Exibe as informações do usuário cadastrado.

7. **Drawer**
   - Disponível no menu superior esquerdo.
   - Dá acesso às telas de **Menu** e **Contas de Energia**.
   - Possui a funcionalidade de **Modo Escuro**, alterando o tema do app para cores escuras.

---

## 📖 Guia de Uso do App

### **Passo a Passo**
1. **Cadastro:**
   - Clique em "Cadastrar" e insira seus dados.
   - Use um email válido para testar a funcionalidade de recuperação de senha.

2. **Login:**
   - Faça login no app com os dados cadastrados.

3. **Cadastro de Contas:**
   - Na tela **Menu**, clique no botão para cadastrar contas de energia.
   - Cadastre duas contas (uma do mês anterior e outra do mês atual) para testar a Barra de Progresso.
   - Atualize a tela para visualizar as mudanças.

4. **Visualização de Contas:**
   - Clique no botão **Contas** para visualizar as contas cadastradas.
   - Edite ou exclua contas para observar as mudanças na Barra de Progresso.

5. **Perfil:**
   - Clique no botão no canto superior direito para visualizar seus dados cadastrados.

6. **Práticas e Consumo:**
   - Na tela **Menu**, clique nos botões de práticas, mudanças e consumo para concluir tarefas relacionadas à economia de energia.

---

## 🌐 Guia de Uso da API

### **Rotas Disponíveis**
- `http://localhost:8080/user` - Gerenciamento de usuários.
- `http://localhost:8080/conta` - Gerenciamento de contas de energia.

### **Exemplos de Requisições**

#### **Usuários (`/user`)**

- **GET (Buscar usuário)Ç**
URL: http://localhost:8080/user

- **POST (Criar usuário):**
  URL: http://localhost:8080/user
  ```json
  {
    "dtNascimento": "1990-01-01",
    "email": "test@example.com",
    "nome": "Test User",
    "telefone": "123456789",
    "password": "password123"
  }
- **PUT (Atualizar usuário):**
URL: http://localhost:8080/user/123456
  ```json
  {
  "dtNascimento": "1991-01-01",
  "email": "updated@example.com",
  "nome": "Updated User",
  "telefone": "987654321",
  "password": "newpassword"
  }

- **DELETE (Deletar usuário):**
URL: http://localhost:8080/user/123456

#### **Contas de Energia (`/conta`)**

- **GET (Buscar conta)Ç**
URL: http://localhost:8080/conta

- **POST (Criar conta):**
  URL: http://localhost:8080/conta
  ```json
  {
  "value": "200.50",
  "month": "Janeiro",
  "notes": "Conta referente ao mês de janeiro"
}
- **PUT (Atualizar conta):**
URL: http://localhost:8080/conta/123456
  ```json
  {
  "dtNascimento": "1991-01-01",
  "email": "updated@example.com",
  "nome": "Updated User",
  "telefone": "987654321",
  "password": "newpassword"
  }
- **DELETE (Deletar conta):**
URL: http://localhost:8080/conta/123456
