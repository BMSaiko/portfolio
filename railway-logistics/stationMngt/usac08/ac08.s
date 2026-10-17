#   ac08.s:

#   Register Allocation:
#   s0: vec (pointer)
#   s1: length (int)
#   s2: order (char, 1 for ascending, 0 for descending)
#   s3: i (outer loop counter)
#   s4: j (inner loop counter)
#   s5: pointer to vec[j]
#   s6: pointer to vec[j+1]

.section .text
.global sort_array

sort_array:


    addi sp, sp, -32        # Allocate 32 bytes: 32 bytes for 8 saved registers (ra, s0-s6).
    sw   ra, 28(sp)
    sw   s0, 24(sp)
    sw   s1, 20(sp)
    sw   s2, 16(sp)
    sw   s3, 12(sp)
    sw   s4, 8(sp)
    sw   s5, 4(sp)
    sw   s6, 0(sp)


    mv   s0, a0              # s0 = vec
    mv   s1, a1              # s1 = length
    mv   s2, a2              # s2 = order (char is passed in a 32-bit register)


    # Return failure if length <= 0
    blez s1, failure

    # Buble Sort
    # 1st Loop: for (i = 0; i < length - 1; i++)
    li   s3, 0              # s3 = i = 0
outer_loop_start:
    li   t0, 1
    sub  t0, s1, t0         # t0 = length - 1
    bge  s3, t0, sort_done  # if (i >= length - 1), exit

    # 2nd Loop: for (j = 0; j < length - i - 1; j++)
    li   s4, 0                # s4 = j = 0
inner_loop_start:
    # Calculate stopping condition: length - i - 1
    sub  t0, s1, s3          # t0 = length - i
    li   t1, 1
    sub  t0, t0, t1          # t0 = length - i - 1
    bge  s4, t0, inner_loop_done     # if (j >= length - i - 1), exit inner loop

    # Compares vec[j] and vec[j+1] and swap if necessary
    # Calcula os endereços de vec[j] e vec[j+1]
    slli t0, s4, 2           # t0 = j * 4
    add  s5, s0, t0          # s5 = &vec[j]
    addi s6, s5, 4         # s6 = &vec[j+1]


    lw   t1, 0(s5)          # t1 = vec[j]
    lw   t2, 0(s6)          # t2 = vec[j+1]


    beqz s2, compare_descending # if (order == 0), jump to descending else ascending


compare_ascending:

    ble  t1, t2, continue_inner_loop    # if (vec[j] <= vec[j+1]), continue loop
    j    swap_elements                  # if (vec[j] > vec[j+1], swap

compare_descending:

    bge  t1, t2, continue_inner_loop   # if (vec[j] >= vec[j+1]), continue loop


swap_elements:
    # if (vec[j] < vec[j+1]), swap
    sw   t2, 0(s5)          # vec[j] = t2 (original value of vec[j+1])
    sw   t1, 0(s6)          # vec[j+1] = t1 (original value of vec[j])

continue_inner_loop:

    addi s4, s4, 1           # j++
    j    inner_loop_start

inner_loop_done:

    addi s3, s3, 1           # i++
    j    outer_loop_start

sort_done:
    li   a0, 1
    j    restore_return

failure:
    li   a0, 0

restore_return:
    lw   ra, 28(sp)
    lw   s0, 24(sp)
    lw   s1, 20(sp)
    lw   s2, 16(sp)
    lw   s3, 12(sp)
    lw   s4, 8(sp)
    lw   s5, 4(sp)
    lw   s6, 0(sp)
    addi sp, sp, 32
    ret
