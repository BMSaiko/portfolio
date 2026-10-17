# **Função `deactivate_customer`**

## **Descrição**
A função `deactivate_customer` desativa um cliente, alterando seu status para inativo. Ela realiza as seguintes verificações antes de desativar o cliente:

1. Se o cliente existe no banco de dados.
2. Se o cliente já está inativo.
3. Se o cliente possui pedidos pendentes (com data de entrega futura ou ainda não definida).

### **Informações Adicionais**
- Cada cliente pode ter múltiplos pedidos registrados na tabela `Order`.
- A data de entrega de um pedido (`DateDelivery`) é utilizada para determinar se o cliente possui pedidos pendentes.

---

## **Regras de Negócio**
- Um cliente **não pode ser desativado** se tiver pedidos pendentes.
- Caso um cliente já esteja desativado, uma mensagem informativa será retornada.

---

## **Parâmetros**
- **`p_nif` (VARCHAR2)**: O NIF do cliente que se deseja desativar.

---

## **Retorno**
A função retorna uma mensagem indicando o resultado da operação:
- **`Erro: O cliente já está desativado.`**
- **`Erro: Cliente com pedidos pendentes não pode ser desativado.`**
- **`Erro: Cliente com o NIF fornecido não encontrado.`**
- **`Sucesso: Cliente desativado com sucesso.`**

---

## **Passos para Implementação**
1. Execute o script de criação de tabelas para configurar o banco de dados.
2. Insira os dados necessários para testes, como clientes e pedidos.
3. Crie a função `deactivate_customer` no banco.
4. Teste a função em diferentes cenários, como:
   - Cliente sem pedidos pendentes.
   - Cliente com pedidos pendentes.
   - Cliente já desativado.
   - Cliente inexistente.