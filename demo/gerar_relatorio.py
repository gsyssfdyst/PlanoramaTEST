#!/usr/bin/env python3
"""
Script para gerar relatório HTML dos testes do PlanoramaWeb
Lê os arquivos XML dos testes e gera um HTML visualmente agradável
"""

import xml.etree.ElementTree as ET
import os
from pathlib import Path
from datetime import datetime

def generate_table_html(test_results, total_tests):
    """Gera a tabela HTML com os resultados dos testes"""
    if total_tests == 0:
        return '<div class="no-tests">Nenhum teste foi executado ainda. Execute "mvn clean test" primeiro.</div>'
    
    rows = []
    for result in test_results:
        row = f"""<tr>
                        <td>{result['class']}</td>
                        <td>{result['name']}</td>
                        <td><span class="status-badge {result['status'].lower()}">{result['status']}</span></td>
                        <td class="time">{result['time']:.3f}s</td>
                    </tr>"""
        rows.append(row)
    
    table = f"""<table>
                <thead>
                    <tr>
                        <th>Classe de Teste</th>
                        <th>Nome do Teste</th>
                        <th>Status</th>
                        <th>Tempo (s)</th>
                    </tr>
                </thead>
                <tbody>
                    {''.join(rows)}
                </tbody>
            </table>"""
    return table

def generate_html_report():
    # Caminhos
    surefire_reports_dir = Path("/Users/programador-rocha/Documents/PlanoramaWeb/demo/target/surefire-reports")
    output_dir = Path("/Users/programador-rocha/Documents/PlanoramaWeb/demo/target/site")
    output_file = output_dir / "surefire-report.html"
    
    # Criar diretório de saída
    output_dir.mkdir(parents=True, exist_ok=True)
    
    # Variáveis de agregação
    total_tests = 0
    total_failures = 0
    total_errors = 0
    total_skipped = 0
    total_time = 0.0
    test_results = []
    
    # Ler todos os arquivos XML de teste
    if surefire_reports_dir.exists():
        xml_files = sorted(surefire_reports_dir.glob("TEST-*.xml"))
        
        for xml_file in xml_files:
            try:
                tree = ET.parse(xml_file)
                root = tree.getroot()
                
                # Extrair informações do testcase
                tests = int(root.get('tests', 0))
                failures = int(root.get('failures', 0))
                errors = int(root.get('errors', 0))
                skipped = int(root.get('skipped', 0))
                time_val = float(root.get('time', 0))
                classname = root.get('name', 'Unknown')
                
                total_tests += tests
                total_failures += failures
                total_errors += errors
                total_skipped += skipped
                total_time += time_val
                
                # Processar testes individuais
                for testcase in root.findall('testcase'):
                    test_name = testcase.get('name', 'Unknown')
                    test_time = float(testcase.get('time', 0))
                    
                    # Verificar status
                    failure = testcase.find('failure')
                    error = testcase.find('error')
                    skipped_elem = testcase.find('skipped')
                    
                    if failure is not None:
                        status = 'FAILURE'
                        message = failure.get('message', 'No message')
                    elif error is not None:
                        status = 'ERROR'
                        message = error.get('message', 'No message')
                    elif skipped_elem is not None:
                        status = 'SKIPPED'
                        message = skipped_elem.get('message', 'No message')
                    else:
                        status = 'SUCCESS'
                        message = ''
                    
                    test_results.append({
                        'class': classname,
                        'name': test_name,
                        'time': test_time,
                        'status': status,
                        'message': message
                    })
            except Exception as e:
                print(f"Erro ao processar {xml_file}: {e}")
    
    # Gerar HTML
    success_rate = (total_tests - total_failures - total_errors) / total_tests * 100 if total_tests > 0 else 0
    
    html_content = f"""<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatório de Testes - PlanoramaWeb</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }}
        
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }}
        
        .header h1 {{
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        .header p {{
            font-size: 1.1em;
            opacity: 0.9;
        }}
        
        .summary {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            padding: 30px;
            background: #f8f9fa;
            border-bottom: 1px solid #e0e0e0;
        }}
        
        .summary-item {{
            background: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }}
        
        .summary-item h3 {{
            color: #666;
            font-size: 0.9em;
            margin-bottom: 10px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }}
        
        .summary-item .number {{
            font-size: 2.5em;
            font-weight: bold;
            margin-bottom: 5px;
        }}
        
        .total {{ color: #333; }}
        .success {{ color: #28a745; }}
        .failure {{ color: #dc3545; }}
        .error {{ color: #e74c3c; }}
        .skipped {{ color: #ffc107; }}
        .success-rate {{ color: #667eea; }}
        
        .content {{
            padding: 30px;
        }}
        
        .section-title {{
            font-size: 1.5em;
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #667eea;
        }}
        
        table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        
        th {{
            background: #f8f9fa;
            color: #333;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            border-bottom: 2px solid #e0e0e0;
        }}
        
        td {{
            padding: 12px 15px;
            border-bottom: 1px solid #e0e0e0;
        }}
        
        tr:hover {{
            background: #f8f9fa;
        }}
        
        .status-badge {{
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
            text-transform: uppercase;
        }}
        
        .status-badge.success {{
            background: #d4edda;
            color: #155724;
        }}
        
        .status-badge.failure {{
            background: #f8d7da;
            color: #721c24;
        }}
        
        .status-badge.error {{
            background: #f8d7da;
            color: #721c24;
        }}
        
        .status-badge.skipped {{
            background: #fff3cd;
            color: #856404;
        }}
        
        .time {{
            color: #999;
            font-size: 0.9em;
        }}
        
        .footer {{
            background: #f8f9fa;
            padding: 20px 30px;
            border-top: 1px solid #e0e0e0;
            color: #666;
            font-size: 0.9em;
            text-align: center;
        }}
        
        .no-tests {{
            text-align: center;
            padding: 40px;
            color: #999;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📊 Relatório de Testes</h1>
            <p>PlanoramaWeb - JUnit 5 + Mockito</p>
        </div>
        
        <div class="summary">
            <div class="summary-item">
                <h3>Total de Testes</h3>
                <div class="number total">{total_tests}</div>
            </div>
            <div class="summary-item">
                <h3>✅ Sucesso</h3>
                <div class="number success">{total_tests - total_failures - total_errors}</div>
            </div>
            <div class="summary-item">
                <h3>❌ Falhas</h3>
                <div class="number failure">{total_failures}</div>
            </div>
            <div class="summary-item">
                <h3>⚠️ Erros</h3>
                <div class="number error">{total_errors}</div>
            </div>
            <div class="summary-item">
                <h3>⏭️ Pulados</h3>
                <div class="number skipped">{total_skipped}</div>
            </div>
            <div class="summary-item">
                <h3>Taxa de Sucesso</h3>
                <div class="number success-rate">{success_rate:.1f}%</div>
            </div>
        </div>
        
        <div class="content">
            <h2 class="section-title">Detalhes dos Testes</h2>
            
            {generate_table_html(test_results, total_tests)}
        </div>
        
        <div class="footer">
            <p>Gerado em: {datetime.now().strftime('%d/%m/%Y às %H:%M:%S')}</p>
            <p>Framework: JUnit 5 + Mockito 5.17.0</p>
            <p>Tempo total de execução: {total_time:.2f}s</p>
        </div>
    </div>
</body>
</html>"""
    
    # Escrever arquivo HTML
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(html_content)
    
    print(f"✅ Relatório gerado com sucesso!")
    print(f"📄 Arquivo: {output_file}")
    print(f"📊 Total de testes: {total_tests}")
    print(f"✅ Sucesso: {total_tests - total_failures - total_errors}")
    print(f"❌ Falhas: {total_failures}")
    print(f"⚠️  Erros: {total_errors}")
    print(f"⏭️  Pulados: {total_skipped}")
    print(f"⏱️  Tempo total: {total_time:.2f}s")
    print(f"📈 Taxa de sucesso: {success_rate:.1f}%")
    
    return output_file

if __name__ == "__main__":
    generate_html_report()
