#   ac09.s:
#
#   Register Allocation:
#   s0: vec (pointer)
#   s1: length (int)
#   s2: me (pointer to median result)
#   s3: i (outer loop counter for sort)
#   s4: j (inner loop counter for sort)
#   s5: pointer to vec[j]
#   s6: pointer to vec[j+1]

.section .text
.global median

median:
    addi sp, sp, -32    # Allocate 32 bytes for 8 saved registers (ra, s0-s6).
    sw   ra, 28(sp)
    sw   s0, 24(sp)
    sw   s1, 20(sp)
    sw   s2, 16(sp)
    sw   s3, 12(sp)
    sw   s4, 8(sp)
    sw   s5, 4(sp)
    sw   s6, 0(sp)


    mv   s0, a0           # s0 = vec
    mv   s1, a1           # s1 = length
    mv   s2, a2           # s2 = me (pointer)

    # code from usac08.s re-used and adapted for median calculation, sorting order is always ascending
    # if length <= 0 jumps to failure
    blez s1, failure

    li   s3, 0            # s3 = i = 0
outer_loop_start:
    li   t0, 1
    sub  t0, s1, t0       # t0 = length - 1
    bge  s3, t0, sort_done

    li   s4, 0            # s4 = j = 0
inner_loop_start:
    sub  t0, s1, s3
    li   t1, 1
    sub  t0, t0, t1       # t0 = length - i - 1
    bge  s4, t0, inner_loop_done

    slli t0, s4, 2
    add  s5, s0, t0         # s5 = &vec[j]
    addi s6, s5, 4          # s6 = &vec[j+1]

    lw   t1, 0(s5)          # t1 = vec[j]
    lw   t2, 0(s6)          # t2 = vec[j+1]


    ble  t1, t2, continue_inner_loop
    # Troca se vec[j] > vec[j+1]
    sw   t2, 0(s5)
    sw   t1, 0(s6)

continue_inner_loop:
    addi s4, s4, 1
    j    inner_loop_start

inner_loop_done:
    addi s3, s3, 1
    j    outer_loop_start

sort_done:

    # After sorting, calculate the median
    # Verifies if the lenght is pair or impar
    andi t0, s1, 1
    beqz t0, handle_even_length # if lenght is par go to handle_even_length
 
handle_odd_length:
    
    li   t0, 1            # median for odd length: middle element
    sub  t0, s1, t0       # t0 = length - 1
    srai t0, t0, 1        # t0 = (length - 1) / 2
    slli t1, t0, 2        # t1 = index * 4 (offset)
    add  t2, s0, t1       # t2 = &vec[index]
    lw   t3, 0(t2)          # t3 = vec[index] (esta é a mediana)
    j    store_median

handle_even_length:
    
    # median for even lenght: average of the two middle elements
    srai t0, s1, 1        # t0 = length / 2
    li   t1, 1
    sub  t1, t0, t1       # t1 = (length / 2) - 1 (índice 1)

    
    slli t2, t1, 2        # Offset to the first middle element
    add  t2, s0, t2
    lw   t3, 0(t2)          # t3 = vec[index 1]

    slli t2, t0, 2        # Offset to the second middle element
    add  t2, s0, t2
    lw   t4, 0(t2)          # t4 = vec[index 2]
    
    add  t3, t3, t4
    srai t3, t3, 1        # t3 = (val1 + val2) / 2

store_median:
    sw   t3, 0(s2)        # *me = median

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
