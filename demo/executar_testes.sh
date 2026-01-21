#!/bin/bash

# Script para executar testes e gerar relatório HTML
# Localização: /Users/programador-rocha/Documents/PlanoramaWeb/demo

cd /Users/programador-rocha/Documents/PlanoramaWeb/demo

echo "================================================"
echo "Executando testes do PlanoramaWeb..."
echo "================================================"

# Executar todos os testes
mvn clean test

# Verificar se teve sucesso
if [ $? -eq 0 ]; then
    echo ""
    echo "================================================"
    echo "Testes executados com sucesso!"
    echo "================================================"
    echo ""
    echo "Gerando relatório HTML..."
    
    # Gerar relatório
    mvn surefire-report:report
    
    echo ""
    echo "================================================"
    echo "Relatório HTML gerado!"
    echo "Arquivo: target/site/surefire-report.html"
    echo "================================================"
else
    echo ""
    echo "❌ Erro ao executar testes!"
    exit 1
fi
