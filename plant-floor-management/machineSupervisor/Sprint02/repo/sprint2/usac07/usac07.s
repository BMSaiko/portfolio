.section .text
.global get_n_element

get_n_element:

    pushq %rbp
    movq %rsp, %rbp
    pushq %rbx


    testq %rdx, %rdx
    je invalid_input
    testq %rcx, %rcx
    je invalid_input


    movl (%rdx), %eax
    movl (%rcx), %ebx


    cmpl $0, %eax
    jl invalid_input
    cmpl %esi, %eax
    jge invalid_input
    cmpl $0, %ebx
    jl invalid_input
    cmpl %esi, %ebx
    jge invalid_input


    cmpl %eax, %ebx
    jge normal_case


    movl %esi, %ecx
    subl %eax, %ecx
    addl %ebx, %ecx
    movl %ecx, %eax
    jmp done

normal_case:

    subl %eax, %ebx
    movl %ebx, %eax

done:

    popq %rbx
    leave
    ret

invalid_input:

    movl $0, %eax
    popq %rbx
    leave
    ret
