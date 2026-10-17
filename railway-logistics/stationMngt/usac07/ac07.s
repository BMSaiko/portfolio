#   ac07.s: 
#   Register Allocation:
#   s0: buffer (pointer)
#   s1: length (int)
#   s2: nelem (pointer)
#   s3: tail (pointer)
#   s4: n (int, number of elements to move)
#   s5: array (pointer, destination)
#   s6: current number of elements (value of *nelem)
#   s7: current tail index (value of *tail)

.section .text
.global move_n_to_array

move_n_to_array:
    
    # Allocate 40 bytes: 36 bytes for 9 saved registers (ra, s0-s7).
    addi sp, sp, -40
    sw   ra, 36(sp)
    sw   s0, 32(sp)
    sw   s1, 28(sp)
    sw   s2, 24(sp)
    sw   s3, 20(sp)
    sw   s4, 16(sp)
    sw   s5, 12(sp)
    sw   s6, 8(sp)
    sw   s7, 4(sp)

    # a4 (head/write pointer) is ignored as it is not needed for this operation.
    mv   s0, a0           # s0 = buffer
    mv   s1, a1           # s1 = length
    mv   s2, a2           # s2 = nelem (pointer)
    mv   s3, a3           # s3 = tail (pointer)
    mv   s4, a5           # s4 = n
    mv   s5, a6           # s5 = array (destination)

    
    # load *nelem and *tail into registers
    lw   s6, 0(s2)        # s6 = *nelem
    lw   s7, 0(s3)        # s7 = *tail

    # Fail if n <= 0
    blez s4, failure
    # Fail if n > *nelem
    bgt  s4, s6, failure

    # Loop to move 'n' elements
    li   t0, 0            # t0 (loop counter)
move_loop:
    # if (i >= n), exit loop
    bge  t0, s4, loop_done

    # Calculate source address,  &buffer[tail]
    slli t1, s7, 2           # t1 = tail * 4 (offset in bytes)
    add  t2, s0, t1         # t2 = buffer + offset

    # Read element from buffer
    lw   t3, 0(t2)          # t3 = buffer[tail]

    # Calculate destination address: &array[i]
    slli t4, t0, 2           # t4 = i * 4 (offset in bytes)
    add  t5, s5, t4         # t5 = array + offset

    # Write element to destination array
    sw   t3, 0(t5)

    # Update tail index
    # tail = (tail + 1) % length
    addi s7, s7, 1           # tail++
    bne  s7, s1, tail_no_wrap # if (tail != length) skip reset
    li   s7, 0              # else, tail = 0
tail_no_wrap:

    # Increment loop counter and repeat
    addi t0, t0, 1           # i++
    j    move_loop

loop_done:
    # Update values in memory 
    # Update *nelem = *nelem - n
    sub  s6, s6, s4
    sw   s6, 0(s2)

    # Update *tail with the final calculated value
    sw   s7, 0(s3)

    # Return success
    li   a0, 1
    j    restore_return

failure:
    li   a0, 0
    # Fall-through to exit routine

restore_return:

    lw   ra, 36(sp)
    lw   s0, 32(sp)
    lw   s1, 28(sp)
    lw   s2, 24(sp)
    lw   s3, 20(sp)
    lw   s4, 16(sp)
    lw   s5, 12(sp)
    lw   s6, 8(sp)
    lw   s7, 4(sp)
    addi sp, sp, 40
    ret
    
    