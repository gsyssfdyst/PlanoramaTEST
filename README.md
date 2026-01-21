# 📚 Planorama - Sistema de Planejamento Acadêmico

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

</div>

O **Planorama** é um sistema web projetado para auxiliar estudantes no **planejamento e acompanhamento dos estudos**.  
A plataforma permite criar **planos de estudo personalizados**, registrar sessões e visualizar estatísticas de desempenho de forma intuitiva.

Desenvolvido em **Java + Spring Boot** com **Thymeleaf** para renderização no servidor, oferecendo uma experiência de usuário fluida e responsiva.

---

## ✨ Funcionalidades Principais

### 👩‍🎓 Para Estudantes
- 🔐 **Autenticação Segura**: Cadastro e login com controle de acesso baseado em roles.  
- 📝 **Criação de Planejamentos**: Defina nome, período (início e fim) e organize sua rotina de estudos.  
- 📚 **Gerenciamento de Matérias e Assuntos**: Estruture cada plano em tópicos de estudo hierárquicos.  
- ⏱ **Registro de Sessões de Estudo**: Salve tempo dedicado e anotações por assunto.  
- 📊 **Acompanhamento de Progresso**: Visualize barras de progresso por matéria e plano.  
- 📈 **Painel de Desempenho**: Gráficos e estatísticas de tempo de estudo por matéria.  
- ⚙️ **Gerenciamento de Conta**: Atualize nome, e-mail e foto de perfil.  

### 🛠 Para Administradores
- 📋 **Dashboard Administrativo**: Visão geral do sistema e métricas gerais.  
- 👥 **Gerenciamento de Usuários**: Listar, editar e remover contas de usuários.  
- 🗂 **Gerenciamento de Planos**: Monitorar todos os planejamentos criados na plataforma.  

---

## 🏗 Arquitetura

A aplicação segue o padrão **MVC (Model-View-Controller)** com separação clara de responsabilidades.  

### 🔧 Stack Tecnológico - Backend
| Tecnologia | Descrição |
|-----------|-----------|
| [Java 21](https://www.oracle.com/java/) | Linguagem de programação |
| [Spring Boot](https://spring.io/projects/spring-boot) | Framework web e injeção de dependência |
| [Spring Security](https://spring.io/projects/spring-security) | Autenticação e autorização |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | Persistência de dados |
| [Hibernate](https://hibernate.org/) | Implementação JPA/ORM |
| [H2 Database](https://www.h2database.com/) | Banco em memória (dev/teste) |
| [Maven](https://maven.apache.org/) | Build e gerenciamento de dependências |

### 🎨 Stack Tecnológico - Frontend
| Tecnologia | Descrição |
|-----------|-----------|
| [Thymeleaf](https://www.thymeleaf.org/) | Templates dinâmicos no servidor |
| HTML5 | Estrutura semântica |
| CSS3 | Estilização responsiva |
| JavaScript | Interatividade e validações |

---

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

- **Java 21 LTS** ou superior ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Git** (opcional, para clonar o repositório)

---

## 🚀 Como Executar

### 1️⃣ Clonar ou Baixar o Projeto

```bash
# Clone o repositório
git clone https://github.com/gsyssfdyst/PlanoramaTEST.git
cd PlanoramaWeb
```

### 2️⃣ Navegar até a Pasta do Projeto

```bash
# Para Windows
cd demo

# Ou especifique o caminho completo
cd C:/Users/usuario/planoramaweb/PlanoramaWeb/demo/
```

### 3️⃣ Executar a Aplicação

**Opção 1: Com Maven**
```bash
mvn clean spring-boot:run
```

**Opção 2: Build e Executar JAR**
```bash
mvn clean package
java -jar target/app.jar
```

### 4️⃣ Acessar a Aplicação

Após a inicialização, acesse no navegador:

```
http://localhost:8081
```

---

## 🧪 Testes

Execute os testes automatizados com:

```bash
mvn test
```

Para visualizar o relatório de testes:

```bash
# Windows
executar_testes.sh

# Gerar relatório em HTML
python gerar_relatorio.py
```

Os relatórios gerados estarão disponíveis em `target/surefire-reports/`.

---

## 📁 Estrutura do Projeto

```
PlanoramaWeb/
├── demo/                          # Módulo principal da aplicação
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/web/...      # Código-fonte (Controllers, Services, etc)
│   │   │   └── resources/
│   │   │       ├── application.yaml
│   │   │       ├── static/        # Arquivos estáticos (CSS, JS)
│   │   │       └── templates/     # Templates Thymeleaf (HTML)
│   │   └── test/
│   │       ├── java/web/...      # Testes automatizados
│   │       └── resources/
│   ├── pom.xml                    # Configuração Maven
│   ├── target/                    # Artifacts compilados
│   └── RELATORIO_TESTES.md        # Relatório de testes
├── data/                          # Dados e configurações
├── COMANDOS_TESTES.md             # Referência de comandos de teste
├── GUIA_RELATORIO.md              # Guia para gerar relatórios
└── README.md                      # Este arquivo
```

---

## 🔐 Segurança

- Autenticação via Spring Security com controle de acesso baseado em roles (USER/ADMIN)
- Senhas criptografadas no banco de dados
- Proteção CSRF em formulários
- Validação de entrada em todas as requisições

---

## 📚 Documentação Adicional

- [Guia de Testes](./COMANDOS_TESTES.md) - Instruções para executar os testes
- [Guia de Relatórios](./GUIA_RELATORIO.md) - Como gerar e interpretar relatórios
- [Relatório de Testes](./demo/RELATORIO_TESTES.md) - Resultados dos testes automatizados

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 📧 Contato e Suporte

Para dúvidas ou sugestões, entre em contato com a equipe de desenvolvimento.

---

<div align="center">

**Desenvolvido com ❤️ para auxiliar no sucesso acadêmico dos estudantes**

</div>  


