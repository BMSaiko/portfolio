.section .text
.global median

median:
    pushq %rbp
    movq %rsp, %rbp

    xorl %eax, %eax #predefinir retorno 0

    # Verifica se o comprimento do vetor é <= 0
    cmpq $0, %rsi       # Verifica se length <= 0
    jle return_fail        # Se zero ou negativo, retorna falha (0)


    pushq %rdi
    pushq %rsi
    pushq %rdx

    # Chama sort_array para ordenar o vetor
    movq $1, %rdx          # Define o modo ascendente (1)
    # Primeiro argumento: vec %rdi
    # Segundo argumento: length %rsi
    call sort_array        # sort_array(vec, length, 1)

    cmpq $0, %rax
    je return_fail

    popq %rdx
    popq %rsi
    popq %rdi

    movq %rsi, %r8         # r8 = length
    shrq $1, %r8           # r8 = length / 2 (índice do meio)

    testq $1, %rsi         # Verifica o bit menos significativo de length
    jnz odd_case           # Se ímpar, vai para odd_case

    # Caso Par: calcula a média dos dois valores centrais
    movl -4(%rdi, %r8, 4), %eax # eax = vec[length / 2 - 1]
    movl 0(%rdi, %r8, 4), %ecx  # ecx = vec[length / 2]
    addl %ecx, %eax             # eax = vec[length / 2 - 1] + vec[length / 2]
    sarl $1, %eax               # Divide por 2 para calcular a média
    jmp store_result

odd_case:
    # Caso Ímpar: pega o valor central
    movl 0(%rdi, %r8, 4), %eax  # eax = vec[length / 2]

store_result:
    # Armazena a mediana no endereço apontado por me
    movl %eax, (%rdx)           # *me = mediana (4 bytes)

    movl $1, %eax               # Sucesso = 1

return:
    movq %rbp, %rsp
    popq %rbp
    ret
