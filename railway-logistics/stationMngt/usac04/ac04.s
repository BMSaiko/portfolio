#   ac04.s:
#
#   Saved Registers:
#   s0: Pointer 'op' (input string)
#   s1: Integer 'n' (input number)
#   s2: Pointer 'cmd' (output buffer)
#   s3: Pointer to the temporary, cleaned command on the stack
#   s4: General purpose temporary pointer

.section .rodata
.global re_str
    re_str: .asciz "RE"

.global ye_str
    ye_str: .asciz "YE"

.global ge_str
    ge_str: .asciz "GE"

.global rb_str
    rb_str: .asciz "RB"

.global gth_str
    gth_str:.asciz "GTH"


.section .text
.global format_command

format_command:
    # Allocate 48 bytes: 32 bytes for saved regs + 16 bytes for a temporary buffer for the cleaned command string.
    addi sp, sp, -48
    sw   ra, 44(sp)
    sw   s0, 40(sp)
    sw   s1, 36(sp)
    sw   s2, 32(sp)
    sw   s3, 28(sp)
    sw   s4, 24(sp)

    mv   s0, a0           # s0 = op
    mv   s1, a1           # s1 = n
    mv   s2, a2           # s2 = cmd
    addi s3, sp, 0        # s3 points to the start of our temp buffer on the stack

    # Trim spaces and capitalize 'op' into the temp buffer 's3'
    mv   s4, s0           # s4 will iterate through 'op'

find_first_char:
    lb   t0, 0(s4)
    beq  t0, zero, invalid_command # Empty or all-space string fails
    li   t1, ' '
    beq  t0, t1, advance_op_ptr
    j    copy_and_capitalize_loop

advance_op_ptr:
    addi s4, s4, 1
    j    find_first_char

copy_and_capitalize_loop:
    lb   t0, 0(s4)
    beq  t0, zero, copy_done  # End of string
    li   t1, ' '
    beq  t0, t1, copy_done    # Space marks end of command

    # Capitalize if it's a lowercase letter
    li   t1, 'a'
    blt  t0, t1, store_char
    li   t1, 'z'
    bgt  t0, t1, store_char
    addi t0, t0, -32          # 'a' - 'A' = 32

store_char:
    sb   t0, 0(s3)            # Store capitalized char in temp buffer
    addi s3, s3, 1
    addi s4, s4, 1
    j    copy_and_capitalize_loop

copy_done:
    sb   zero, 0(s3)          # Null-terminate the cleaned command string
    addi s3, sp, 0            # Reset s3 to the start of the temp buffer for comparison

    # Validate the cleaned command
    # Try matching with each valid command string

    # Compare with "RE"
    la   s4, re_str
    jal  ra, string_equals
    beq  a0, zero, handle_re

    # Compare with "YE"
    la   s4, ye_str
    jal  ra, string_equals
    beq  a0, zero, handle_ye

    # Compare with "GE"
    la   s4, ge_str
    jal  ra, string_equals
    beq  a0, zero, handle_ge

    # Compare with "RB"
    la   s4, rb_str
    jal  ra, string_equals
    beq  a0, zero, handle_rb

    # Compare with "GTH"
    la   s4, gth_str
    jal  ra, string_equals
    beq  a0, zero, handle_gth

    # If no match was found, the command is invalid
    j    invalid_command

# Handlers for each valid command

handle_re:
    la s4, re_str
    j format_cmd_with_num

handle_ye:
    la s4, ye_str
    j format_cmd_with_num

handle_ge:
    la s4, ge_str
    j format_cmd_with_num

handle_rb:
    la s4, rb_str
    # Fall-through to the common formatting logic

format_cmd_with_num:
    # Validate number n (must be 0-99)
    blt  s1, zero, invalid_command
    li   t0, 100
    bge  s1, t0, invalid_command

    # Format output "CMD,XX"
    lb   t0, 0(s4)
    sb   t0, 0(s2)
    lb   t0, 1(s4)
    sb   t0, 1(s2)

    # Add comma to separate command and number
    li   t0, ','
    sb   t0, 2(s2)

    # Convert number n to two digits
    li   t0, 10
    divu t1, s1, t0       # t1 = n / 10 (tens digit)
    remu t2, s1, t0       # t2 = n % 10 (ones digit)

    li   t3, '0'
    add  t1, t1, t3       # Convert tens digit to asci
    add  t2, t2, t3       # Convert ones digit to asci

    sb   t1, 3(s2)
    sb   t2, 4(s2)

    # Null-terminate
    sb   zero, 5(s2)
    j    success

handle_gth:
    # Format output "GTH" (number is ignored)
    la   s4, gth_str
    lb   t0, 0(s4)
    sb   t0, 0(s2)
    lb   t0, 1(s4)
    sb   t0, 1(s2)
    lb   t0, 2(s4)
    sb   t0, 2(s2)
    sb   zero, 3(s2)
    j    success


success:
    li   a0, 1
    j    restore_return

invalid_command:
    sb   zero, 0(s2)      # Ensure output is an empty string on failure
    li   a0, 0

restore_return:
    lw   ra, 44(sp)
    lw   s0, 40(sp)
    lw   s1, 36(sp)
    lw   s2, 32(sp)
    lw   s3, 28(sp)
    lw   s4, 24(sp)
    addi sp, sp, 48
    ret

# String_equals
# Compares two null-terminated strings.
# s3 (str1), s4 (str2)
# Retruns: a0 = 0 if equal, a0 = 1 if not equal

string_equals:
    mv t0, s3
    mv t1, s4
    
compare_loop:
    lb t2, 0(t0)
    lb t3, 0(t1)
    bne t2, t3, strings_not_equal
    beq t2, zero, strings_are_equal
    addi t0, t0, 1
    addi t1, t1, 1
    j compare_loop

strings_are_equal:
    li a0, 0
    ret

strings_not_equal:
    li a0, 1
    ret
