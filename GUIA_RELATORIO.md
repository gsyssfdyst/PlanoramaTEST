# 📊 Guia Passo-a-Passo: Gerar Relatório HTML dos Testes

## ⚠️ PROBLEMA COMUM
Se você executou:
```bash
mvn clean test surefire-report:report
```

E recebeu o erro:
```
ERROR] The goal you specified requires a project to execute but there is no POM in this directory
```

**A causa é**: Você está no diretório **errado**.

---

## ✅ SOLUÇÃO: Passo 1 - Ir para o diretório correto

```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
```

**Verifique** se está no diretório correto:
```bash
pwd
# Deve mostrar: /Users/programador-rocha/Documents/PlanoramaWeb/demo

ls pom.xml
# Deve mostrar: pom.xml
```

---

## ✅ SOLUÇÃO: Passo 2 - Executar os testes

```bash
mvn clean test
```

Aguarde até ver:
```
[INFO] BUILD SUCCESS
[INFO] Tests run: 164, Failures: 0, Errors: 0, Skipped: 0
```

---

## ✅ SOLUÇÃO: Passo 3 - Gerar o relatório HTML

Após os testes passarem, execute:

```bash
mvn surefire-report:report
```

Você verá:
```
[INFO] BUILD SUCCESS
[INFO] Generating "Surefire Report" report...
```

---

## ✅ SOLUÇÃO: Passo 4 - Abrir o relatório

O arquivo HTML foi gerado em:
```
target/site/surefire-report.html
```

### Para abrir no macOS:
```bash
open target/site/surefire-report.html
```

### Para ver o arquivo direto:
```bash
cat target/site/surefire-report.html | head -50
```

### Para verificar se o arquivo existe:
```bash
ls -lh target/site/surefire-report.html
```

---

## 🎯 Comando Completo (Tudo de uma vez)

Se quer fazer tudo em um único comando:

```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo && \
mvn clean test && \
mvn surefire-report:report && \
open target/site/surefire-report.html
```

---

## 📋 Checklist

- [ ] Estou no diretório `/Users/programador-rocha/Documents/PlanoramaWeb/demo`
- [ ] O arquivo `pom.xml` existe nesse diretório
- [ ] Executei `mvn clean test` com sucesso
- [ ] Executei `mvn surefire-report:report` com sucesso
- [ ] O arquivo `target/site/surefire-report.html` foi criado
- [ ] Abri o relatório HTML no navegador

---

## 📊 Conteúdo do Relatório HTML

O relatório inclui:
- ✅ Resumo de testes (total, sucesso, falhas)
- ✅ Estatísticas por pacote
- ✅ Estatísticas por classe de teste
- ✅ Tempo de execução de cada teste
- ✅ Detalhes de erros (se houver)

---

## 🔗 Localização dos Arquivos

| Arquivo | Caminho |
|---------|---------|
| **POM** | `/Users/programador-rocha/Documents/PlanoramaWeb/demo/pom.xml` |
| **Testes** | `/Users/programador-rocha/Documents/PlanoramaWeb/demo/src/test/` |
| **Relatório HTML** | `/Users/programador-rocha/Documents/PlanoramaWeb/demo/target/site/surefire-report.html` |
| **Resultados brutos** | `/Users/programador-rocha/Documents/PlanoramaWeb/demo/target/surefire-reports/` |

---

## 🐛 Se ainda não funcionar

Tente limpar tudo:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
rm -rf target/
mvn clean test
mvn surefire-report:report
```

Ou use o script automático:
```bash
bash /Users/programador-rocha/Documents/PlanoramaWeb/demo/executar_testes.sh
```

---

**Última atualização**: 21 de janeiro de 2026
