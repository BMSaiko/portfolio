#   ac03_adaptada.s:
#   Implements the generic get_value_by_key function.
#
#   Function Signature:
#   int get_value_by_key(char* str, char* key, char* value_buffer)
#
#   Register Allocation:
#   s0: str (input string)
#   s1: key (input key)
#   s2: value_buffer (output)
#   s3: pointer to the search key string built on the stack (e.g., "value:")
#   s4: temporary pointer to iterate through 'str'
#   s5: temporary pointer to iterate through the search key string

.section .text
.global get_value_by_key

get_value_by_key:

    addi sp, sp, -48
    sw   ra, 44(sp)
    sw   s0, 40(sp)
    sw   s1, 36(sp)
    sw   s2, 32(sp)
    sw   s3, 28(sp)
    sw   s4, 24(sp)
    sw   s5, 20(sp)


    mv   s0, a0           # s0 = str
    mv   s1, a1           # s1 = key
    mv   s2, a2           # s2 = value_buffer
    addi s3, sp, 0        # s3 points to the temporary buffer on the stack

    mv   t0, s1           # t0 = pointer to the input key
    mv   t1, s3           # t1 = pointer to the temporary buffer
build_search_key_loop:
    lb   t2, 0(t0)
    beq  t2, zero, build_search_key_done
    sb   t2, 0(t1)
    addi t0, t0, 1
    addi t1, t1, 1
    j    build_search_key_loop
build_search_key_done:
    li   t2, ':'          # Append the ':' character
    sb   t2, 0(t1)
    addi t1, t1, 1
    sb   zero, 0(t1)      # Append the null terminator '\0'


    mv   s4, s0           # s4 = search pointer for the main string

search_loop_start:
    lb   t0, 0(s4)
    beq  t0, zero, not_found

    mv   t0, s4           # t0 = pointer to the current position in 'str'
    mv   s5, s3           # s5 = pointer to the start of our search_key

compare_loop:
    lb   t1, 0(s5)
    beq  t1, zero, key_found

    lb   t2, 0(t0)
    bne  t1, t2, no_match

    addi t0, t0, 1
    addi s5, s5, 1
    j    compare_loop

no_match:
    addi s4, s4, 1        # Advance one character in the main string and try again
    j    search_loop_start

key_found:
    mv   s4, t0           # s4 = read pointer for the value
    mv   s5, s2           # s5 = write pointer for the value_buffer

extract_loop:
    lb   t1, 0(s4)
    beq  t1, zero, extract_done # End of the main string
    li   t2, '#'
    beq  t1, t2, extract_done # End of the value

    sb   t1, 0(s5)        # Copy the character
    addi s4, s4, 1
    addi s5, s5, 1
    j    extract_loop

extract_done:
    sb   zero, 0(s5)      # Null-terminate the value string
    li   a0, 1            # Return success
    j    restore_return

not_found:
    sb   zero, 0(s2)      # Ensure the output buffer is an empty string on failure
    li   a0, 0            # Return failure

restore_return:
    lw   ra, 44(sp)
    lw   s0, 40(sp)
    lw   s1, 36(sp)
    lw   s2, 32(sp)
    lw   s3, 28(sp)
    lw   s4, 24(sp)
    lw   s5, 20(sp)
    addi sp, sp, 48
    ret