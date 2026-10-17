.section .text
.global encrypt_data
.global decrypt_data
.global validate_uppercase_string

# int encrypt_data(char* in, int key, char *out)
encrypt_data:
    addi sp, sp, -16
    sw ra, 12(sp)
    sw s0, 8(sp)
    sw s1, 4(sp)
    sw s2, 0(sp)
    
    mv s0, a0        # s0 = input string pointer
    mv s1, a1        # s1 = key
    mv s2, a2        # s2 = output string pointer
    
    # Validate key range [1, 26]
    li t0, 1
    blt a1, t0, encrypt_fail
    li t0, 26
    bgt a1, t0, encrypt_fail
    
    # Validate input string
    mv a0, s0
    jal ra, validate_uppercase_string
    beqz a0, encrypt_fail
    
encrypt_loop:
    lb t0, 0(s0)          # Load current character
    beqz t0, encrypt_done
    
    # Encrypt: out_char = ((in_char - 'A' + key) % 26) + 'A'
    addi t1, t0, -65      # t1 = in_char - 'A'
    add t1, t1, s1        # t1 = (in_char - 'A' + key)
    
    # Modulo 26 operation
    li t2, 26
modulo_loop:
    blt t1, t2, modulo_done
    addi t1, t1, -26
    j modulo_loop
    
modulo_done:
    addi t1, t1, 65       # t1 = encrypted_char
    sb t1, 0(s2)          # Store encrypted character
    
    addi s0, s0, 1
    addi s2, s2, 1
    j encrypt_loop

encrypt_done:
    sb zero, 0(s2)        # Null terminate
    li a0, 1              # Return success
    j encrypt_exit

encrypt_fail:
    sb zero, 0(s2)        # Empty string
    li a0, 0              # Return failure

encrypt_exit:
    lw ra, 12(sp)
    lw s0, 8(sp)
    lw s1, 4(sp)
    lw s2, 0(sp)
    addi sp, sp, 16
    ret

# int decrypt_data(char* in, int key, char *out)
decrypt_data:
    addi sp, sp, -16
    sw ra, 12(sp)
    sw s0, 8(sp)
    sw s1, 4(sp)
    sw s2, 0(sp)
    
    mv s0, a0        # s0 = input string pointer
    mv s1, a1        # s1 = key
    mv s2, a2        # s2 = output string pointer
    
    # Validate key range [1, 26]
    li t0, 1
    blt a1, t0, decrypt_fail
    li t0, 26
    bgt a1, t0, decrypt_fail
    
    # Validate input string
    mv a0, s0
    jal ra, validate_uppercase_string
    beqz a0, decrypt_fail
    
decrypt_loop:
    lb t0, 0(s0)          # Load current character
    beqz t0, decrypt_done
    
    # Decrypt: out_char = ((in_char - 'A' - key + 26) % 26) + 'A'
    addi t1, t0, -65      # t1 = in_char - 'A'
    sub t1, t1, s1        # t1 = (in_char - 'A' - key)
    
    # Ensure positive value
    bge t1, zero, decrypt_modulo
    addi t1, t1, 26
    
decrypt_modulo:
    li t2, 26
decrypt_modulo_loop:
    blt t1, t2, decrypt_modulo_done
    addi t1, t1, -26
    j decrypt_modulo_loop
    
decrypt_modulo_done:
    addi t1, t1, 65       # t1 = decrypted_char
    sb t1, 0(s2)          # Store decrypted character
    
    addi s0, s0, 1
    addi s2, s2, 1
    j decrypt_loop

decrypt_done:
    sb zero, 0(s2)        # Null terminate
    li a0, 1              # Return success
    j decrypt_exit

decrypt_fail:
    sb zero, 0(s2)        # Empty string
    li a0, 0              # Return failure

decrypt_exit:
    lw ra, 12(sp)
    lw s0, 8(sp)
    lw s1, 4(sp)
    lw s2, 0(sp)
    addi sp, sp, 16
    ret

# int validate_uppercase_string(char* str)
validate_uppercase_string:
    mv t0, a0            # t0 = string pointer

validate_loop:
    lb t1, 0(t0)         # Load current character
    beqz t1, validate_success
    
    # Check if character is between 'A' and 'Z'
    li t2, 65            # 'A'
    blt t1, t2, validate_fail
    li t2, 90            # 'Z'
    bgt t1, t2, validate_fail
    
    addi t0, t0, 1       # Move to next character
    j validate_loop

validate_success:
    li a0, 1
    ret

validate_fail:
    li a0, 0
    ret