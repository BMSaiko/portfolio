<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Proposta Aprovada</title>
</head>
<body>
    <h1>Foi Aprovado A Sua Proposta</h1>
    <p>Detalhes da proposta:</p>
    <p>Título: {{ $proposta->Titulo }}</p>
    <p>Descrição: {{ $proposta->Descricao }}</p>
    <!-- Adicione mais detalhes conforme necessário -->

    <p>Obrigado,</p>
    <p>Sua assinatura</p>
</body>
</html>
