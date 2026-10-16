.section .data
.section .text
.global enqueue_value
enqueue_value:

	movl  (%rcx),%eax
	movl  %r8d,(%rdi,%rax,4)
	addl  $1,%eax 
	cmpl  %esi, %eax
	jnz   inc
	movl  $0,%eax
	
inc:
    movl  %eax,(%rcx)
	cmpl  %eax,(%rdx)
	jne   end


	addl  $1,%eax
    cmpl  %esi,%eax
	jnz   inc2
	movl  $0,%eax
	
inc2:
	movl  %eax,(%rdx) 
    movl  $1,%eax
	ret

end:
	movl  $0,%eax
	ret
