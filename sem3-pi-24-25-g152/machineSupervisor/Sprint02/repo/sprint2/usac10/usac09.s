.section .data

.section .text
.global sort_array

sort_array:
    pushq %rbp
    movq %rsp, %rbp


    pushq %rbx
    pushq %r12
    pushq %r13
    pushq %r14

    cmpq $1, %rsi
    jl end_sort_otherwise

    cmpq $1, %rsi
    je sort_done

    movb %dl, %al
    cmpb $1, %al
    je ascending_sort
    cmpb $0, %al
    je descending_sort

    jmp end_sort_otherwise

ascending_sort:
    movq %rsi, %r12
    decq %r12

outer_loop_asc:
    movq $0, %r13
    cmpq $0, %r12
    je sort_done

inner_loop_asc:
    movq %rdi, %rbx
    movl (%rbx, %r13, 4), %eax
    cmpl 4(%rbx, %r13, 4), %eax
    jle no_swap_asc
    movl 4(%rbx, %r13, 4), %edx
    movl %edx, (%rbx, %r13, 4)
    movl %eax, 4(%rbx, %r13, 4)
no_swap_asc:
    incq %r13
    cmpq %r12, %r13
    jl inner_loop_asc
    decq %r12
    jmp outer_loop_asc

descending_sort:
    movq %rsi, %r12
    decq %r12
outer_loop_desc:
    movq $0, %r13
    cmpq $0, %r12
    je sort_done
inner_loop_desc:
    movq %rdi, %rbx
    movl (%rbx, %r13, 4), %eax
    cmpl 4(%rbx, %r13, 4), %eax
    jge no_swap_desc
    movl 4(%rbx, %r13, 4), %edx
    movl %edx, (%rbx, %r13, 4)
    movl %eax, 4(%rbx, %r13, 4)
no_swap_desc:
    incq %r13
    cmpq %r12, %r13
    jl inner_loop_desc
    decq %r12
    jmp outer_loop_desc

sort_done:
    movl $1, %eax

end_sort_succeed:
    popq %r14
    popq %r13
    popq %r12
    popq %rbx

    movq %rbp, %rsp
    popq %rbp
    ret

end_sort_otherwise:
    movl $0, %eax
    popq %r14
    popq %r13
    popq %r12
    popq %rbx

    movq %rbp, %rsp
    popq %rbp
    ret
