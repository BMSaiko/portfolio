.section .text
.global enqueue_value
.global dequeue_value

# int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value)
# a0 = buffer, a1 = length, a2 = nelem, a3 = tail, a4 = head, a5 = value
enqueue_value:
    # Prologue
    addi sp, sp, -24
    sw ra, 20(sp)
    sw s0, 16(sp)
    sw s1, 12(sp)
    sw s2, 8(sp)
    sw s3, 4(sp)
    sw s4, 0(sp)
    
    # Save arguments
    mv s0, a0        # s0 = buffer
    mv s1, a1        # s1 = length
    mv s2, a2        # s2 = nelem
    mv s3, a3        # s3 = tail
    mv s4, a4        # s4 = head
    
    # Load current values
    lw t0, 0(s2)     # t0 = *nelem
    lw t1, 0(s3)     # t1 = *tail
    lw t2, 0(s4)     # t2 = *head
    
    # Check if buffer is full
    bne t0, s1, not_full
    
    # Buffer is full - remove oldest element (dequeue)
    # Calculate address: buffer + tail
    slli t3, t1, 2    # t3 = tail * 4 (sizeof(int))
    add t3, s0, t3    # t3 = &buffer[tail]
    
    # Update tail = (tail + 1) % length
    addi t1, t1, 1
    blt t1, s1, tail_ok_full
    mv t1, zero       # if tail >= length, tail = 0
tail_ok_full:
    sw t1, 0(s3)     # *tail = new tail
    
    # nelem remains the same (we remove one but add one)
    j insert_element

not_full:
    # Buffer not full - increment nelem
    addi t0, t0, 1
    sw t0, 0(s2)     # *nelem = *nelem + 1

insert_element:
    # Insert new element at head position
    # Calculate address: buffer + head
    slli t3, t2, 2    # t3 = head * 4 (sizeof(int))
    add t3, s0, t3    # t3 = &buffer[head]
    sw a5, 0(t3)      # buffer[head] = value
    
    # Update head = (head + 1) % length
    addi t2, t2, 1
    blt t2, s1, head_ok
    mv t2, zero       # if head >= length, head = 0
head_ok:
    sw t2, 0(s4)     # *head = new head
    
    # Check if buffer is full after insertion
    lw t0, 0(s2)     # t0 = *nelem
    beq t0, s1, return_full
    
    # Buffer not full
    li a0, 0
    j enqueue_exit

return_full:
    li a0, 1

enqueue_exit:
    # Epilogue
    lw ra, 20(sp)
    lw s0, 16(sp)
    lw s1, 12(sp)
    lw s2, 8(sp)
    lw s3, 4(sp)
    lw s4, 0(sp)
    addi sp, sp, 24
    ret

# int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value)
# a0 = buffer, a1 = length, a2 = nelem, a3 = tail, a4 = head, a5 = value
dequeue_value:
    # Prologue
    addi sp, sp, -20
    sw ra, 16(sp)
    sw s0, 12(sp)
    sw s1, 8(sp)
    sw s2, 4(sp)
    sw s3, 0(sp)
    
    # Save arguments
    mv s0, a0        # s0 = buffer
    mv s1, a1        # s1 = length
    mv s2, a2        # s2 = nelem
    mv s3, a3        # s3 = tail
    
    # Load current values
    lw t0, 0(s2)     # t0 = *nelem
    lw t1, 0(s3)     # t1 = *tail
    
    # Check if buffer is empty
    beqz t0, dequeue_fail
    
    # Get value from tail position
    slli t2, t1, 2    # t2 = tail * 4 (sizeof(int))
    add t2, s0, t2    # t2 = &buffer[tail]
    lw t3, 0(t2)      # t3 = buffer[tail]
    
    # Store value in output parameter
    sw t3, 0(a5)      # *value = buffer[tail]
    
    # Update tail = (tail + 1) % length
    addi t1, t1, 1
    blt t1, s1, tail_ok_dequeue
    mv t1, zero       # if tail >= length, tail = 0
tail_ok_dequeue:
    sw t1, 0(s3)     # *tail = new tail
    
    # Decrement nelem
    lw t0, 0(s2)     # t0 = *nelem
    addi t0, t0, -1
    sw t0, 0(s2)     # *nelem = *nelem - 1
    
    li a0, 1         # Return success
    j dequeue_exit

dequeue_fail:
    li a0, 0         # Return failure

dequeue_exit:
    # Epilogue
    lw ra, 16(sp)
    lw s0, 12(sp)
    lw s1, 8(sp)
    lw s2, 4(sp)
    lw s3, 0(sp)
    addi sp, sp, 20
    ret

# ... (código das USAC01 e USAC02 permanece aqui) ...