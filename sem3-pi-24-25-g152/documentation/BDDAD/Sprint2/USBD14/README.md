# **Função `get_products_using_all_workstation_types`**

## **Descrição**
A função `get_products_using_all_workstation_types` identifica os produtos que utilizam todos os tipos de estações de trabalho definidas na tabela `WorkstationTypes`. A função retorna um cursor contendo os IDs dos produtos que atendem a este critério.

---

## **Funcionamento**
1. **Consulta Principal**: 
   - Retorna os produtos (`Product.PartID`) que não possuem nenhum tipo de estação de trabalho faltante.  
   - Utiliza a operação `MINUS` para identificar a diferença entre:
     - Todos os tipos de estações de trabalho existentes.
     - Os tipos de estações de trabalho utilizados pelo produto em questão.

2. **Exceção Tratada**:
   - Caso nenhum produto seja encontrado, uma mensagem será exibida no console informando que não existem produtos que utilizem todos os tipos de estações de trabalho.

---

## **Parâmetros**
- **Nenhum**: A função não requer parâmetros.

---

## **Retorno**
- **`SYS_REFCURSOR`**: Cursor contendo os IDs dos produtos que utilizam todos os tipos de estações de trabalho.
- Caso nenhum produto seja encontrado, a função retorna `NULL`.

---

## **Exemplo de Uso**
### **Chamada da Função e Exibição de Resultados**
```sql
DECLARE
    products_cursor SYS_REFCURSOR;
    product_id Product.PartID%TYPE;
    v_no_data_found BOOLEAN := TRUE;
BEGIN
    -- Obtém o cursor com os produtos
    products_cursor := get_products_using_all_workstation_types;

    -- Itera pelos resultados
    LOOP
        FETCH products_cursor INTO product_id;
        EXIT WHEN products_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Produto que utiliza todas as máquinas: ' || product_id);
        v_no_data_found := FALSE;
    END LOOP;

    -- Exibe mensagem se nenhum produto foi encontrado
    IF v_no_data_found THEN
        DBMS_OUTPUT.PUT_LINE('Nenhum produto encontrado que utilize todos os tipos de estações de trabalho.');
    END IF;

    -- Fecha o cursor
    CLOSE products_cursor;
END;