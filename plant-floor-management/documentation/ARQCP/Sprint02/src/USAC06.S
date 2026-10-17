.text
.global dequeue_value

dequeue_value:
    pushq   %rbp
    movq    %rsp, %rbp
    pushq   %rbx

    movl    (%rcx), %eax
    movl    (%rdx), %ebx

    cmpl    %ebx, %eax
    je      buffer_empty

buffer_not_empty:
    movl    (%rdi,%rax,4), %edx
    movl    %edx, (%r8)

    incl    %eax

    cmpl    %esi, %eax
    jl      store_new_head
    xorl    %eax, %eax

store_new_head:
    movl    %eax, (%rcx)

    movl    $1, %eax
    jmp     function_end

buffer_empty:
    movl    $0, %eax

function_end:
    popq    %rbx
    movq    %rbp, %rsp
    popq    %rbp
    ret

.section .note.GNU-stack,"",@progbits
