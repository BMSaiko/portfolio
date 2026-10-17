.section .data
TEMPstr:
    .asciz "TEMP"
HUMstr:
    .asciz "HUM"


.section .text
.global extract_data
extract_data:
    pushq %rbp
    movq %rsp, %rbp
    movl $0, %ebx

    pushq %rdi
    pushq %rsi
    pushq %rdx
    pushq %rcx
    pushq %r8
    pushq %rbx
    movq %rsi, %rdi
    call verifyToken
    popq %rbx
    popq %r8
    popq %rcx
    popq %rdx
    popq %rsi
    popq %rdi

    cmpq $0, %rax
    je error

    pushq %rdi
    pushq %rsi
    pushq %rdx
    pushq %rcx
    pushq %r8
    call tokenCheck
    popq %r8
    popq %rcx
    popq %rdx
    popq %rsi
    popq %rdi
    #rax endereço do caracter a seguir ao token
    cmpq $0, %rax
    je error
    movq %rax, %r8


    pushq %rdi
    pushq %rsi
    pushq %rdx
    pushq %rcx
    pushq %r8
    movq %r8, %rdi
    movq %rcx, %rsi
    call extractValue
    popq %r8
    popq %rcx
    popq %rdx
    popq %rsi
    popq %rdi


    pushq %rdi
    pushq %rsi
    pushq %rdx
    pushq %rcx
    pushq %r8
    movq %r8, %rdi
    movq %rdx, %rsi
    call extractUnit
    popq %r8
    popq %rcx
    popq %rdx
    popq %rsi
    popq %rdi

    movl $1, %ebx
error:
    movl %ebx, %eax
    movq %rbp, %rsp
    popq %rbp
    ret


.global tokenCheck
tokenCheck:
    pushq %rbp
    movq %rsp, %rbp
    movq $0, %r8
    movq $0, %rax
    movq $0, %rdx



fullstrLoop:
    cmpb $0, (%rdi)
    je notFound


    movb (%rsi), %dl
    movb (%rdi), %r8b

    cmpb %dl, %r8b
    jne skipChar

    pushq %rdi
    pushq %rsi
TokenLoop:
    inc %rdi
    inc %rsi
    movb (%rsi), %dl
    movb (%rdi), %r8b

    cmpb $0, %dl
    je foundToken

    cmpb %r8b, %dl
    je TokenLoop

    popq %rsi
    popq %rdi
skipChar:
    inc %rdi

    jmp fullstrLoop

foundToken:
    movq %rdi, %rax

notFound:
    movq %rbp, %rsp
    popq %rbp
    ret

.global extractUnit  #rdi ponteiro para primeiro caracter depois do token #rdx->rsi para string unit
extractUnit:
    pushq %rbp
    movq %rsp, %rbp
    movq $0, %rdx

unitLoop:
    cmpb $0, (%rdi)
    je endCopyUnit

    movb (%rdi), %dl
    cmpb $'#', %dl
    je endCopyUnit

    cmpb $'u', %dl
    jne incStrUnit
    cmpb $'n', 1(%rdi)
    jne incStrUnit
    cmpb $'i', 2(%rdi)
    jne incStrUnit
    cmpb $'t', 3(%rdi)
    jne incStrUnit
    cmpb $':', 4(%rdi)
    jne incStrUnit

    addq $5, %rdi
    jmp copyUnit

incStrUnit:
    inc %rdi
    jmp unitLoop

copyUnit:

    movb (%rdi), %dl
    cmpb $'&', %dl
    je endCopyUnit
    cmpb $'#', %dl
    je endCopyUnit
    cmpb $0, %dl
    je endCopyUnit

    movb %dl, (%rsi)
    inc %rdi
    inc %rsi
    jmp copyUnit

endCopyUnit:
    movb $0, (%rsi)
    movq %rbp, %rsp
    pop %rbp
    ret





.global extractValue #rdi ponteiro para primeiro caracter depois do token #rcx->rsi ponteiro para valor
extractValue:
    pushq %rbp
    movq %rsp, %rbp

valueLoop:
    cmpb $0, (%rdi)
    je end_extractValue
    movb (%rdi), %al

    cmpb $'#', %al
    je end_extractValue

    cmpb $'v', %al
    jne incStrValue
    cmpb $'a', 1(%rdi)
    jne incStrValue
    cmpb $'l', 2(%rdi)
    jne incStrValue
    cmpb $'u', 3(%rdi)
    jne incStrValue
    cmpb $'e', 4(%rdi)
    jne incStrValue
    cmpb $':', 5(%rdi)
    jne incStrValue

    addq $6, %rdi
    jmp convert_value

incStrValue:
    inc %rdi
    jmp valueLoop


convert_value:
    movq $0, %rax

convertLoop:
    movq $0, %r10
    movb (%rdi), %r10b
    cmpb $'&', %r10b
    je end_convert_value
    cmpb $'#', %r10b
    je end_convert_value
    cmpb $0, %r10b
    je end_convert_value

    subb $'0', %r10b
    imul $10, %rax
    addq %r10, %rax

    inc %rdi
    jmp convertLoop

end_convert_value:
    movq %rax, (%rsi)
    movq %rbp, %rsp
    popq %rbp
    ret

end_extractValue:
    movq %rbp, %rsp
    popq %rbp
    ret



.global verifyToken
verifyToken:
    pushq %rbp
    movq %rsp, %rbp
    movq $0, %rax

    leaq TEMPstr(%rip), %rsi
    pushq %rdi

compare_temp:
    movb (%rdi), %cl
    movb (%rsi), %bl
    cmpb %cl, %bl
    jne check_hum

    cmpb $0, %cl
    je token_found

    inc %rdi
    inc %rsi
    jmp compare_temp

check_hum:
    popq %rdi
    leaq HUMstr(%rip), %rsi

compare_hum:
    movb (%rdi), %cl
    movb (%rsi), %bl
    cmpb %cl, %bl
    jne end_verify

    cmpb $0, %cl
    je token_found

    inc %rdi
    inc %rsi
    jmp compare_hum

token_found:
    movq $1, %rax

end_verify:
    movq %rbp, %rsp
    popq %rbp
    ret

