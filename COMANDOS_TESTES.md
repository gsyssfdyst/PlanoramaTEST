# 📋 Comandos para Executar Testes - PlanoramaWeb

## ⚠️ IMPORTANTE: Diretório Correto
**SEMPRE execute os comandos no diretório do projeto:**
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
```

Não execute em `/Users/programador-rocha/Documents/PlanoramaWeb` (este não tem pom.xml)

---

## 🔴 EXECUTAR TODOS OS TESTES

### 1. Executar todos os testes com output completo:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test
```

### 2. Executar todos os testes e contar total:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test 2>&1 | grep "Tests run:" | tail -1
```

### 3. Executar todos os testes em modo quiet (menos verbose):
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test -q
```

### 4. Executar todos os testes e gerar relatório HTML:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test
mvn surefire-report:report
# Abrir em: target/site/surefire-report.html
```

---

## 🟡 EXECUTAR TESTES ESPECÍFICOS POR CT

### CT001 - Criar Usuário (6 testes)
```bash
mvn test -Dtest=CriarUsuarioCT001Test
```

### CT002 & CT005 - Usuário (20 testes)
```bash
mvn test -Dtest=UsuarioT005Test,CriarUsuarioControllerTest
```

### CT003 - Criar Planejamento (8 testes)
```bash
mvn test -Dtest=CriarPlanejamentoCT003Test
```

### CT004 - Registrar Estudo (8 testes)
```bash
mvn test -Dtest=RegistrarEstudoCT004Test
```

### CT006 - Remover Planejamento (3 testes)
```bash
mvn test -Dtest=PlanejamentoServiceImplCT006Test
```

### CT007 - Serviço Matéria (4 testes)
```bash
mvn test -Dtest=MateriaServiceImplCT007Test
```

### CT008 - Acesso Negado (12 testes)
```bash
mvn test -Dtest=AccessDeniedCT008Test
```

### CT009 - Autenticação Obrigatória (10 testes)
```bash
mvn test -Dtest=AuthenticationRequiredCT009Test
```

### CT010 - Editar Planejamento (4 testes)
```bash
mvn test -Dtest=PlanejamentoServiceImplCT010Test
```

### CT011 - Serviço Usuário (5 testes)
```bash
mvn test -Dtest=UsuarioServiceImplCT011Test
```

### CT012 - Atualizar Usuário (4 testes)
```bash
mvn test -Dtest=UsuarioServiceImplCT012Test
```

### CT013 - Buscar Planejamentos (14 testes)
```bash
mvn test -Dtest=PlanejamentoServiceImplCT013Test
```

### CT015 - Serviço Assunto (5 testes)
```bash
mvn test -Dtest=AssuntoServiceImplCT015Test
```

### CT016 - Arquivar Planejamento (6 testes)
```bash
mvn test -Dtest=PlanejamentoServiceImplCT016Test
```

### CT017 - Conclusão Planejamento (7 testes)
```bash
mvn test -Dtest=RegistrarEstudoServiceImplCT017Test
```

### CT018 & CT019 - Listagem e Seleção (5 testes)
```bash
mvn test -Dtest=PlanejamentoServiceImplCT018CT019Test
```

### CT020 - Refazer Planejamento (4 testes)
```bash
mvn test -Dtest=RegistrarEstudoServiceImplCT020Test
```

---

## 🟢 EXECUTAR GRUPOS DE TESTES

### Todos os testes de Service:
```bash
mvn test -Dtest=*ServiceImpl*Test
```

### Todos os testes de Controller:
```bash
mvn test -Dtest=*Controller*Test
```

### Todos os testes de Segurança:
```bash
mvn test -Dtest=AccessDeniedCT008Test,AuthenticationRequiredCT009Test
```

### Testes CT017-CT020 (últimos 4):
```bash
mvn test -Dtest=RegistrarEstudoServiceImplCT017Test,PlanejamentoServiceImplCT018CT019Test,RegistrarEstudoServiceImplCT020Test
```

### Testes CT001-CT010 (primeiros 10):
```bash
mvn test -Dtest=CriarUsuarioCT001Test,CriarPlanejamentoCT003Test,RegistrarEstudoCT004Test,PlanejamentoServiceImplCT006Test
```

---

## 🔵 OPÇÕES ÚTEIS

### Com cobertura de testes (JaCoCo):
```bash
mvn clean test jacoco:report
```

### Sem compilar (apenas testes):
```bash
mvn test -DskipCompile
```

### Parar no primeiro teste que falha (fail-fast):
```bash
mvn test -ff
```

### Continuar mesmo com falhas (fail-at-end):
```bash
mvn test -fae
```

### Executar teste específico em modo debug:
```bash
mvn -Dtest=PlanejamentoServiceImplCT006Test -Dmaven.surefire.debug test
```

### Gerar relatório de teste detalhado:
```bash
mvn surefire-report:report
# Arquivo gerado em: target/site/surefire-report.html
```

---

## 📊 VERIFICAR ESTATÍSTICAS DE TESTES

### Contar total de testes:
```bash
mvn clean test 2>&1 | grep "Tests run:" | tail -1
```

### Contar testes por arquivo:
```bash
mvn clean test 2>&1 | grep "Tests run:"
```

### Ver apenas testes que falharam:
```bash
mvn clean test 2>&1 | grep -E "FAIL|ERROR"
```

### Ver output dos println dos testes:
```bash
mvn clean test 2>&1 | grep ">>>"
```

### Ver resumo final:
```bash
mvn clean test 2>&1 | tail -20
```

---

## 🎯 EXEMPLOS PRÁTICO DE USO

### Executar todos os testes e ver estatísticas:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test
```

### Executar apenas testes de CT006:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn test -Dtest=PlanejamentoServiceImplCT006Test
```

### Executar CT017, CT018, CT019 e CT020:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn test -Dtest=RegistrarEstudoServiceImplCT017Test,PlanejamentoServiceImplCT018CT019Test,RegistrarEstudoServiceImplCT020Test
```

### Gerar relatório HTML e abrir:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test surefire-report:report
open target/site/surefire-report.html
```

### Executar todos os testes de serviço com cobertura:
```bash
cd /Users/programador-rocha/Documents/PlanoramaWeb/demo
mvn clean test jacoco:report -Dtest=*ServiceImpl*Test
open target/site/jacoco/index.html
```

---

## ✅ STATUS ATUAL

| Métrica | Valor |
|---------|-------|
| **Total de Testes** | 164 |
| **Taxa de Sucesso** | 100% |
| **Framework** | JUnit 5 + Mockito 5.17.0 |
| **Java Version** | 21.0.9 |
| **Spring Boot** | 3.5.4 |
| **Status Build** | SUCCESS ✅ |

---

## 📝 DICAS

1. **Atalho rápido**: Sempre use `mvn clean` antes de `mvn test` para garantir compilação limpa
2. **Performance**: Use `-q` para saída menos verbosa e mais rápida
3. **Debug**: Use `-X` para modo debug completo (muito verbose)
4. **Parallel**: Use `-T 1C` para executar testes em paralelo (1 thread por core)
5. **Skip**: Use `-DskipTests` para compilar sem executar testes

---

**Última atualização**: 20 de janeiro de 2026
