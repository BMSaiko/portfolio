
	.section .text 

#int dequeue_value(int* array, int length, int* read, int* write, int *value);
# 1 %rdi 2 %rsi 3 %rdx 4 %rcx 5 %r8 6 %r9 #free %r10 %r11 
#   array   len    *rd    *wr   *val 
	.global dequeue_value
dequeue_value: 

	 movl   (%rdx),%eax    # eax = read 
	 cmpl   %eax,(%rcx)    # read == write ?? 
	 je     empty

	 movl  (%rdi,%rax,4),%eax  # read value 
	 movl  %eax,(%r8)          #   write value on destination 
	 
	 incl  (%rdx)  # read ++
         cmpl  %esi,(%rdx)   # read == length ?? 
         jne   read_ok       # reset length  
         movl  $0,(%rdx) 
	
read_ok: movl  $1,%eax 
	 ret   


empty:  movl 	$0,%eax 
	ret  
	

		
#int enqueue_value(int* array, int length, int* read, int* write, int value){
# 1 %rdi 2 %rsi 3 %rdx 4 %rcx 5 %r8 6 %r9 #free %r10 %r11 
#   array   len    *rd    *wr   val 

	.global enqueue_value 
enqueue_value:  
	
	movl  (%rcx),%eax  	# %eax = write 
	movl  %r8d,(%rdi,%rax,4)   #  array[write]=val 
	addl  $1,%eax # write ++ 
	cmpl  %esi, %eax    # compare len 
	jnz   inc_ok
	movl  $0,%eax      # reset write 
inc_ok: 
        movl  %eax,(%rcx) # update write 
	cmpl  %eax,(%rdx) # write == read ?  
	jne   not_equal 
	
					# here write == read 
	addl  $1,%eax 
        cmpl  %esi,%eax # == len 
	jnz   inc_ok2 
	movl  $0,%eax 
inc_ok2: 
	movl  %eax,(%rdx)  # update read 
        movl  $1,%eax 
	ret 

not_equal:
	movl  $0,%eax  
	ret
