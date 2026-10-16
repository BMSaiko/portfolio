#   ac03.s:

#   Saved Registers:
#   s0: Pointer 'str' (current position during search/parse)
#   s1: Pointer 'token' (current position during compare)
#   s2: Pointer 'unit' (output buffer)
#   s3: Pointer to 'value' (output integer pointer)
#   s4: Pointer to the start of the current match attempt in 'str'
#   s5: Pointer to the start of the 'token' string (unmodified)
#   s6: Pointer tmp for parsing unit/value labels

.section .data
.global unit_label
unit_label:
    .asciz "&unit:"
.global value_label
value_label:
    .asciz "&value:"


.section .text
.global extract_data

extract_data:
    # Save registers to stack
    addi sp, sp, -32      # Allocate 32 bytes on stack
    sw   ra, 28(sp)       # Save return address
    sw   s0, 24(sp)       # Save s0..s6
    sw   s1, 20(sp)
    sw   s2, 16(sp)
    sw   s3, 12(sp)
    sw   s4, 8(sp)
    sw   s5, 4(sp)
    sw   s6, 0(sp)

    mv   s0, a0           # s0 = str (used as a temporary pointer)
    mv   s1, a1           # s1 = token (used as a temporary pointer)
    mv   s2, a2           # s2 = unit buffer
    mv   s3, a3           # s3 = value pointer
    mv   s4, a0           # s4 = pointer to the start of the next match attempt in str
    mv   s5, a1           # s5 = holds the original token pointer, for resets


main_loop:      # Main Loop: Iterate through the string str to find a potential token
    lb   t0, 0(s4)
    beq  t0, zero, not_found # Reached end of str, token not found

    mv   s0, s4           # Reset search pointer to the current start attempt
    mv   s1, s5           # Reset token pointer to the beginning of the token


compare_loop:               # Comparison Loop: Compare substring with the token
    lb   t1, 0(s1)
    beq  t1, zero, token_found # End of token string reached, potential match found

    lb   t0, 0(s0)
    # If we hit end of str during compare, it's not a match
    beq  t0, zero, no_match
    bne  t0, t1, no_match   # Characters do not match

    addi s0, s0, 1
    addi s1, s1, 1
    j    compare_loop

no_match:
    addi s4, s4, 1        # Advance start pointer in str by one
    j    main_loop        # and restart the search


token_found:            # A substring was found. Now, validate if it's a whole token.
    # A valid token must be:
    # 1. Followed by '&'.
    # 2. Preceded by the start of the string OR '#'.

    # Check condition 1: Is the character AFTER the match '&'?
    # s0 currently points to this character.
    lb   t0, 0(s0)
    li   t1, '&'
    bne  t0, t1, no_match      # If not followed by '&', it's not a valid token. Continue search.

    # Check condition 2: Was the match at the very beginning of the string?
    beq  s4, a0, valid_token # If s4 (start of match) is same as a0 (start of str), condition is met.

    # If not at the start, was it preceded by '#'?
    lb   t0, -1(s4)            # Load character before the start of the match
    li   t1, '#'
    bne  t0, t1, no_match      # If not preceded by '#', it's not a valid token. Continue search.

# If confirmed, valid token. Proceed to extract data.
valid_token:
    # s0 points to the '&' after the token. Now search for "&unit:"
    mv   s6, s0
find_unit_label:
    la   s1, unit_label
    mv   s0, s6

    lb   t0, 0(s0)
    beq  t0, zero, not_found
compare_unit_label_loop:
    lb   t1, 0(s1)
    beq  t1, zero, unit_label_found

    lb   t0, 0(s0)
    bne  t0, t1, unit_label_no_match

    addi s0, s0, 1
    addi s1, s1, 1
    j    compare_unit_label_loop

unit_label_no_match:
    addi s6, s6, 1
    j    find_unit_label

unit_label_found:
    # 's0' points to the char after "&unit:". Extract the unit string.
extract_unit_loop:
    lb   t0, 0(s0)
    li   t1, '&'
    beq  t0, t1, unit_done

    sb   t0, 0(s2)
    addi s0, s0, 1
    addi s2, s2, 1
    j    extract_unit_loop

unit_done:
    sb   zero, 0(s2)      # Null-terminate the unit string

    # Now search for "&value:"
    mv   s6, s0
find_value_label:
    la   s1, value_label
    mv   s0, s6

    lb   t0, 0(s0)
    beq  t0, zero, not_found

compare_value_label_loop:
    lb   t1, 0(s1)
    beq  t1, zero, value_label_found

    lb   t0, 0(s0)
    bne  t0, t1, value_label_no_match

    addi s0, s0, 1
    addi s1, s1, 1
    j    compare_value_label_loop

value_label_no_match:
    addi s6, s6, 1
    j    find_value_label

value_label_found:
    # 's0' points to the char after "&value:". Extract and convert the number.
    li   t2, 0
extract_value_loop:
    lb   t0, 0(s0)
    li   t1, '0'
    blt  t0, t1, value_done
    li   t1, '9'
    bgt  t0, t1, value_done

    addi t0, t0, -'0'
    li   t1, 10
    mul  t2, t2, t1
    add  t2, t2, t0
    addi s0, s0, 1
    j    extract_value_loop

value_done:
    sw   t2, 0(s3)
    li   a0, 1            # Success
    j    restore_return

not_found:
    # Ensure output is clean on failure
    sb   zero, 0(s2)
    sw   zero, 0(s3)
    li   a0, 0            # Failure

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