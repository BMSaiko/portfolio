# `USAC01` Documentation

This document details the RISC-V assembly implementation of the `encrypt_data` function, developed for User Story **USAC01** of the Computer Architecture (ARQCP) project.

## 1. Function Purpose

The `encrypt_data` function implements the Caesar Cipher encryption algorithm. It takes an input string containing only uppercase English letters (A–Z) and a key value, and produces an encrypted output string where each character is shifted by the specified key positions in the alphabet.

## 2. Function Signature (C)

```c
int encrypt_data(char* in, int key, char* out);
```

## 3. Parameters and Return Value

- Parameters
    - `char* in` (Input): Pointer to the input string containing only uppercase English letters (A–Z).
    - `int key` (Input): Integer in the range [1, 26] representing the shift amount.
    - `char* out` (Output): Pointer to a buffer where the encrypted string will be stored.

- Return value
    - `1` — Encryption successful (valid key and input).
    - `0` — Encryption failed (invalid key or input contains non-uppercase letters). On failure, `out` is set to an empty string (`""`).

## 4. Calling Convention (RISC-V ABI)

The function adheres to the standard RISC-V calling convention:

Register | Purpose
---|---
`a0` | `in` pointer / return value
`a1` | `key`
`a2` | `out` pointer
`s0-s2` | Saved registers for internal use
`sp` | Stack pointer
`ra` | Return address

## 5. Implementation Logic and Code Analysis

The algorithm performs a character-by-character encryption using modular arithmetic. Each major step below includes the assembly excerpts.

### 5.1 Stack Management

Function prologue/epilogue save and restore callee-saved registers and manage stack space.

```assembly
# Function Entry & Context Save
encrypt_data:
        addi sp, sp, -16      # Allocate 16 bytes on the stack
        sw   ra, 12(sp)       # Save the return address
        sw   s0, 8(sp)        # Save s0
        sw   s1, 4(sp)        # Save s1
        sw   s2, 0(sp)        # Save s2

# ... (function body) ...

# Function Exit & Context Restore
encrypt_exit:
        lw   ra, 12(sp)       # Restore saved registers
        lw   s0, 8(sp)
        lw   s1, 4(sp)
        lw   s2, 0(sp)
        addi sp, sp, 16       # Deallocate stack space
        ret                   # Return
```

### 5.2 Input Validation

Validate the key range and ensure the input string contains only uppercase letters.

Key validation (must be in [1, 26]):

```assembly
# Validate key range [1, 26]
li t0, 1
blt a1, t0, encrypt_fail    # Key < 1 → fail
li t0, 26
bgt a1, t0, encrypt_fail    # Key > 26 → fail
```

String validation uses a helper function:

```assembly
# Validate input string (check if all uppercase letters)
mv a0, s0
jal ra, validate_uppercase_string
beqz a0, encrypt_fail      # Invalid characters → fail
```

### 5.3 Encryption Loop

Process each character until the null terminator is reached.

```assembly
encrypt_loop:
        lb   t0, 0(s0)          # Load current character from input
        beqz t0, encrypt_done   # If null terminator, encryption complete

        # Caesar Cipher transformation:
        # encrypted_char = ((char - 'A' + key) % 26) + 'A'
```

### 5.4 Character Transformation

Steps to convert, shift, wrap, and store the encrypted character.

Step 1: Convert to 0-based index

```assembly
addi t1, t0, -65      # t1 = char - 'A' (convert to 0-25 range)
```

Step 2: Apply shift and modulo 26

```assembly
add  t1, t1, s1        # t1 = (char - 'A' + key)

# Modulo 26 operation to handle wraparound
li   t2, 26
modulo_loop:
        blt  t1, t2, modulo_done
        addi t1, t1, -26
        j    modulo_loop
modulo_done:
```

Step 3: Convert back to ASCII and store

```assembly
addi t1, t1, 65       # t1 = encrypted_char = result + 'A'
sb   t1, 0(s2)        # Store encrypted character in output
```

### 5.5 Pointer Advancement and Loop Control

Advance pointers and repeat.

```assembly
# Move to next characters
addi s0, s0, 1        # Advance input pointer
addi s2, s2, 1        # Advance output pointer
j    encrypt_loop     # Continue with next character
```

### 5.6 Successful Completion

Null-terminate output and return success.

```assembly
encrypt_done:
        sb   zero, 0(s2)   # Null terminate output string
        li   a0, 1         # Return success (1)
        j    encrypt_exit
```

### 5.7 Failure Handling

On validation failure, set output to empty string and return failure.

```assembly
encrypt_fail:
        sb   zero, 0(s2)   # Set output to empty string
        li   a0, 0         # Return failure (0)
        j    encrypt_exit
```

## 6. Helper Function: validate_uppercase_string

Validates that a string contains only uppercase English letters.

```assembly
# int validate_uppercase_string(char* str)
validate_uppercase_string:
        mv   t0, a0            # t0 = string pointer

validate_loop:
        lb   t1, 0(t0)         # Load current character
        beqz t1, validate_success  # Null terminator → valid string

        # Check if character is between 'A' (65) and 'Z' (90)
        li   t2, 65
        blt  t1, t2, validate_fail
        li   t2, 90
        bgt  t1, t2, validate_fail

        addi t0, t0, 1         # Move to next character
        j    validate_loop

validate_success:
        li   a0, 1             # Return valid (1)
        ret

validate_fail:
        li   a0, 0             # Return invalid (0)
        ret
```

## 7. Algorithm Characteristics

- Time complexity: O(n), where n is the input string length.
- Space complexity: O(1) additional space (excluding input/output buffers).
- Character set: Uppercase English letters (A–Z) only.
- Key range: [1, 26].
- Wraparound: Handled via modulo 26 arithmetic.

## 8. Example Usage

C example:

```c
char input[] = "HELLO";
char encrypted[10];
int key = 3;

int result = encrypt_data(input, key, encrypted);
/* result == 1 (success)
     encrypted == "KHOOR" */
```

This document provides a complete, markdown-formatted description of the RISC-V Caesar Cipher implementation for `encrypt_data`, including validation, transformation, and helper routines.