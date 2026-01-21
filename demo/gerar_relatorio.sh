#!/bin/bash

# Script para gerar relatório HTML dos testes PlanoramaWeb
# Alternativa ao Maven quando há problemas

cd /Users/programador-rocha/Documents/PlanoramaWeb/demo

echo "================================================"
echo "Gerando relatório de testes..."
echo "================================================"

# Executar script Python
python3 gerar_relatorio.py

echo ""
echo "================================================"
echo "Abrindo relatório no navegador..."
echo "================================================"

# Abrir relatório
open target/site/surefire-report.html

echo "✅ Relatório aberto com sucesso!"
