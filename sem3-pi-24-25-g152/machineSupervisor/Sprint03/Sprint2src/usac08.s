.section .text
.global move_n_to_array
move_n_to_array: # rdi buffer, rsi len, rdx tail, rcx head, r8 n, r9 array
    pushq %rbp
    movq %rsp, %rbp
    pushq %r12
    pushq %r13

    movq $0, %r10             # Índice no array destino (array)

    # Verificar se n <= 0
    cmpq $0, %r8
    jle fail_return           # Falha se n <= 0

    # Calcular número de elementos disponíveis no buffer circular
    movl (%rcx), %r12d
    movl (%rdx), %r13d
    movl %r12d, %eax
    subl %r13d, %eax          # eax = head - tail
    cmpl $0, %eax
    jge buffer_size_valid     # Se head >= tail, número de elementos = head - tail
    addl %esi, %eax           # Caso contrário: head + (len - tail)

buffer_size_valid:
    movl %eax, %r12d

    # Verificar se há elementos suficientes no buffer
    cmpl %r12d, %r8d          # n > número de elementos
    jg fail_return

loop_start:
    # Verificar se todos os elementos foram copiados
    cmpq $0, %r8
    je success_return

    # Copiar elemento do buffer para o array
    movl (%rdx), %eax          # eax = *tail (índice atual no buffer)
    movl (%rdi, %rax, 4), %r11d
    movl %r11d, (%r9, %r10, 4)

    # Incrementar tail
    incq %r13
    cmpl %esi, %r13d
    jl no_reset
    movl $0, %r13d             # Reset tail para 0

no_reset:
    movl %r13d, (%rdx)         # Atualizar *tail com o novo valor


    incq %r10
    decq %r8
    jmp loop_start

# Retorno de sucesso
success_return:
    movq $1, %rax         
    popq %r13
    popq %r12
    movq %rbp, %rsp
    popq %rbp
    ret

# Retorno de falha
fail_return:
    movq $0, %rax
    popq %r13
    popq %r12
    movq %rbp, %rsp
    popq %rbp
    ret