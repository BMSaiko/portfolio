# Documentação da Função `RegisterProduct`

A função `RegisterProduct` foi criada para registrar um produto em um sistema,
garantindo que ele seja associado a uma descrição de peça e a uma família de produtos existente. 

---

## Objetivo

Registrar um novo produto no banco de dados, vinculando-o a uma peça (na tabela `Part`)
e verificando a associação a uma família de produtos (`ProductFamily`).
Em caso de erro, a função informa o motivo e reverte qualquer operação parcial.

---

## Definição da Função

```sql
CREATE OR REPLACE FUNCTION RegisterProduct(
    p_PartID Product.PartID%TYPE,
    p_Name Product.Name%TYPE,
    p_PFID Product.PFID%TYPE,
    p_description Part.Description%TYPE
) RETURN VARCHAR2

---

## **Exemplo de Uso**

### **Chamada da Função e Exibição de Resultados**

```sql
DECLARE
    v_result VARCHAR2(255);
BEGIN
    v_result := RegisterProduct('id', 'nome', 130, 'descriçao');
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/

SELECT Product.Name, Product.PartID, Product.PFID FROM Product
WHERE Product.PartID = 'id'
