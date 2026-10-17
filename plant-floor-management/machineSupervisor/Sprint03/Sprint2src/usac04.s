.section .data
ONstr:
    .asciz "ON"
OFFstr:
    .asciz "OFF"
OPstr:
    .asciz "OP"
CMDstr:
    .asciz "CMD"

.section .text
.global format_command
format_command:
    # Entrada:
    #   %rdi - Ponteiro para string original (entrada)
    #   %rsi - Número (0-31) para ser convertido para binário
    #   %rdx - Ponteiro para buffer de saída (cmd)

    pushq %rbp
    movq %rsp, %rbp

    # Remove espaços iniciais e finais, armazena no buffer de saída
    pushq %rsi
    movq %rdi, %rsi             # Copia ponteiro de entrada para %rsi (origem)
    movq %rdx, %rdi             # Ponteiro de saída para %rdi (destino)
    call TrimAndCapitalize      # Remove espaços e capitaliza (nova função)
    popq %rsi



    pushq %rsi
    # Valida o comando
    movq %rdx, %rdi             # %rdx contém o comando formatado
    call ValidateCommand        # Valida se é ON, OFF ou OP
    testq %rax, %rax            # Verifica retorno
    je endFail                  # Se inválido, falha
    popq %rsi


    movq %rdx, %r8              # %rdx é o ponteiro do buffer de saída
AddComma:
    movb (%r8), %al             # Lê o próximo caractere no buffer
    cmpb $0, %al                # Procura o final da string
    je CommaAdded               # Se for \0, adiciona a vírgula
    incq %r8                    # Avança o ponteiro
    jmp AddComma

CommaAdded:
    movb $',', (%r8)            # Escreve a vírgula no final do comando
    incq %r8


    # Converte número para binário e adiciona ao buffer
    subq $5, %rsp               # Reserva espaço para 5 bits na pilha
    movq %rsp, %r9              # Ponteiro para o array de bits

    movq %rsi, %rdi             # Número a ser convertido
    movq %r9, %rsi              # Ponteiro para o array de bits
    call get_number_binary      # Converte para binário
    testq %rax, %rax            # Verifica sucesso
    je endFail                  # Falha se número fora do intervalo


    # Escreve os bits binários no buffer de saída
    movq %r9, %rsi              # Ponteiro para os bits binários
    movq %r8, %rdi              # Ponteiro para o buffer de saída
    call WriteBinaryString      # Converte bits para string
    addq $5, %rsp               # Libera espaço para bits na pilha


    movq $1, %rax               # Sucesso
    movq %rbp, %rsp
    popq %rbp
    ret

endFail:
    movq $0, %rax               # Retorna 0 para indicar falha
    movq %rbp, %rsp
    popq %rbp
    ret

# Função: TrimAndCapitalize
# Remove espaços iniciais/finais e converte para maiúsculas
.global TrimAndCapitalize
TrimAndCapitalize:
    # %rsi: Ponteiro para a entrada
    # %rdi: Ponteiro para o buffer de saída
    pushq %rbp
    movq %rsp, %rbp

    # Remove espaços iniciais
TrimStart:
    movb (%rsi), %al            # Lê caractere da entrada
    cmpb $0, %al                # Verifica fim da string
    je TrimEnd                  # Se fim, pula para TrimEnd
    cmpb $' ', %al              # Compara com espaço
    jne CopyChar                # Se não for espaço, pula para copiar
    incq %rsi                   # Avança ponteiro de entrada
    jmp TrimStart               # Repete para próximo caractere

CopyChar:
    cmpb $'a', %al              # Verifica se caractere é 'a'-'z'
    jl WriteChar                # Pula se menor que 'a'
    cmpb $'z', %al              # Verifica se maior que 'z'
    jg WriteChar                # Pula se maior que 'z'
    subb $32, %al               # Converte para maiúscula

WriteChar:
    movb %al, (%rdi)            # Escreve caractere no buffer
    incq %rdi                   # Avança ponteiro de saída
    incq %rsi                   # Avança ponteiro de entrada
    jmp TrimStart               # Continua processando caracteres

TrimEnd:
    movb $0, (%rdi)             # Adiciona terminador nulo
    popq %rbp
    ret


.global CopyString
CopyString:
    # Copia string de %rsi para %rdi
    pushq %rbp
    movq %rsp, %rbp

CopyLoop:
    movb (%rsi), %al             # Caractere atual da origem
    movb %al, (%rdi)             # Copia para o destino
    testb %al, %al               # Verifica se é \0
    je CopyDone
    incq %rsi                    # Avança na origem
    incq %rdi                    # Avança no destino
    jmp CopyLoop

CopyDone:
    popq %rbp
    ret

.global ValidateCommand
ValidateCommand:

    lea ONstr(%rip), %r8        # Ponteiro para "ON"
    lea OFFstr(%rip), %r9       # Ponteiro para "OFF"
    lea OPstr(%rip), %r10       # Ponteiro para "OP"

    movq %rdi, %rsi             # String formatada como entrada
    movq %r8, %rdi              # Compara com "ON"
    pushq %rsi
    call strcmp
    popq %rsi
    cmpl $0, %eax
    je Valid


    movq %r9, %rdi              # Compara com "OFF"
    pushq %rsi
    call strcmp
    popq %rsi
    cmpl $0, %eax
    je Valid

    movq %r10, %rdi             # Compara com "OP"
    pushq %rsi
    call strcmp
    popq %rsi
    cmpl $0, %eax
    je Valid

    leaq CMDstr(%rip), %r8        # Ponteiro para "CMP"

    movq %r8, %rdi              # Compara com "CMP"
    pushq %rsi
    call strcmp
    popq %rsi
    cmpl $0, %eax
    je Valid

    movq $0, %rax               # Comando inválido
    ret

Valid:
    movq $1, %rax               # Comando válido
    ret

.global WriteBinaryString
WriteBinaryString:
    # Entrada:
    #   rsi - Ponteiro para os bits binários (5 elementos, ordem inversa)
    #   rdi - Ponteiro para a string de saída
    pushq %rbp
    movq %rsp, %rbp

    movq $5, %rcx                # Número de bits (sempre 5)
    leaq -1(%rsi, %rcx), %rsi    # Ajusta ponteiro para o último elemento do array

WriteLoop:
    movb (%rsi), %al             # Lê o bit do array (do último para o primeiro)
    addb $'0', %al               # Converte o bit (0 ou 1) para caractere ASCII
    movb %al, (%rdi)             # Escreve o caractere na saída
    incq %rdi                    # Avança na saída

    decq %rcx                    # Decrementa o contador de bits
    jz WriteBinaryDone           # Se todos os bits foram processados, termina

    movb $',', (%rdi)            # Adiciona vírgula após o bit
    incq %rdi                    # Avança na saída
    decq %rsi                    # Move para o bit anterior no array
    jmp WriteLoop                # Continua no loop

WriteBinaryDone:
    movb $0, (%rdi)              # Finaliza a string com '\0'
    popq %rbp
    ret

strcmp:
    # Entrada:
    #   %rdi - Ponteiro para a primeira string
    #   %rsi - Ponteiro para a segunda string
    # Saída:
    #   %rax - Retorna 0 se as strings forem iguais, ou a diferença do primeiro caractere desigual

    pushq %rbp
    movq %rsp, %rbp

strcmp_loop:
    xorq %r11, %r11
    movb (%rdi), %al            # Lê o caractere atual da primeira string
    movb (%rsi), %r11b            # Lê o caractere atual da segunda string
    cmpb %al, %r11b               # Compara os dois caracteres
    jne strcmp_diff             # Se diferentes, vai para strcmp_diff

    testb %al, %al              # Verifica se chegou ao final da string (\0)
    je strcmp_equal             # Se sim, strings são iguais

    incq %rdi                   # Avança para o próximo caractere da primeira string
    incq %rsi                   # Avança para o próximo caractere da segunda string
    jmp strcmp_loop             # Continua no loop

strcmp_diff:
    subb %r11b, %al               # Calcula a diferença entre os caracteres
    movsbl %al, %eax            # Converte o byte para inteiro
    jmp strcmp_end

strcmp_equal:
    movl $0, %eax               # Strings iguais, retorna 0

strcmp_end:
    movq %rbp, %rsp
    popq %rbp
    ret