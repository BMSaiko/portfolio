.section .text
.global dequeue_value

dequeue_value:
    # Parameters:
    # rdi -> pointer to buffer (int array)
    # rsi -> buffer length (int)
    # rdx -> pointer to tail (volatile int)
    # rcx -> pointer to head (int)
    # r8  -> pointer to value (int)

    # Save callee-saved registers
    pushq %rbx

    # Check if pointers are null
    test %rdi, %rdi            # Check if buffer pointer is null
    jz error_exit              # Exit if null
    test %r8, %r8              # Check if value pointer is null
    jz error_exit              # Exit if null

    # Load tail and head indices
    movl (%rdx), %eax          # Load tail into %eax
    movl (%rcx), %ebx          # Load head into %ebx

    # Check if buffer is empty
    cmpl %ebx, %eax            # Compare tail and head
    je buffer_empty            # If tail == head, buffer is empty

    # Calculate buffer index and load value
    movsxd %eax, %rax          # Sign-extend tail index to 64 bits in %rax
    leaq (%rdi,%rax,4), %r9    # Compute address: buffer + tail * 4
    movl (%r9), %ecx           # Load buffer[tail] into %ecx
    movl %ecx, (%r8)           # Store value into *value

    # Increment tail index
    incl %eax                  # Increment tail

    # Check if tail has reached length
    cmpl %esi, %eax            # Compare tail with length
    jl store_updated_tail      # If tail < length, jump to store_updated_tail

    xorl %eax, %eax            # Reset tail to 0 if it reaches length

store_updated_tail:
    movl %eax, (%rdx)          # Store updated tail index

    # Restore registers and return 1 (success)
    popq %rbx
    movl $1, %eax
    ret

buffer_empty:
    # Restore registers and return 0 (buffer is empty)
    popq %rbx
    xorl %eax, %eax            # Return 0
    ret

error_exit:
    # Restore registers and return 0 (failure due to null pointer)
    popq %rbx
    xorl %eax, %eax            # Return 0
    ret

.section .note.GNU-stack,"",@progbits
