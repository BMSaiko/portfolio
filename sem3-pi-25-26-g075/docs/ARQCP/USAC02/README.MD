# `USAC02` Documentation

This document details the RISC-V assembly implementation of the `decrypt_data` function, developed for User Story **USAC02** of the Computer Architecture (ARQCP) project.

## 1. Function purpose
`decrypt_data` implements the Caesar Cipher decryption algorithm for input strings containing only uppercase English letters (A–Z). Given an encrypted input and a key, it produces the decrypted output by shifting each character back by the key value.

## 2. Function signature (C)
```c
int decrypt_data(char *in, int key, char *out);
```
Return values:
- `1` — success (valid input and key).
- `0` — failure (invalid key or input contains non-uppercase letters). On failure `out` is set to an empty string.

## 3. Parameters
- `char *in` (input): pointer to the encrypted null-terminated string (A–Z only).
- `int key` (input): integer in the range `[1, 26]` representing the shift amount.
- `char *out` (output): pointer to the buffer where the decrypted null-terminated string will be written.

## 4. Calling convention (RISC‑V ABI)
Arguments and return value use the standard RISC‑V calling convention:
- `a0` — `in` pointer / return value
- `a1` — `key`
- `a2` — `out` pointer
Saved registers used: `s0`–`s2`. Stack pointer: `sp`. Return address: `ra`.

## 5. Implementation overview
The function performs:
1. Prologue / epilogue: save/restore `ra` and `s0`–`s2`.
2. Validate `key` in `[1, 26]`.
3. Validate that `in` contains only uppercase letters (using a helper `validate_uppercase_string`).
4. Loop over each character and apply the reverse Caesar transform:
    decrypted = ((c - 'A' - key + 26) % 26) + 'A'
5. Null-terminate `out` and return success or set `out` to empty and return failure.

## 6. Stack management (prologue / epilogue)
```asm
decrypt_data:
     addi sp, sp, -16      # allocate stack
     sw   ra, 12(sp)       # save ra
     sw   s0, 8(sp)        # save s0
     sw   s1, 4(sp)        # save s1
     sw   s2, 0(sp)        # save s2
     ...
decrypt_exit:
     lw   ra, 12(sp)       # restore ra
     lw   s0, 8(sp)
     lw   s1, 4(sp)
     lw   s2, 0(sp)
     addi sp, sp, 16       # deallocate stack
     ret
```

## 7. Input validation
Key range check:
```asm
     li   t0, 1
     blt  a1, t0, decrypt_fail    # key < 1 -> fail
     li   t0, 26
     bgt  a1, t0, decrypt_fail    # key > 26 -> fail
```
String validation (calls helper):
```asm
     mv   a0, s0
     jal  ra, validate_uppercase_string
     beqz a0, decrypt_fail        # invalid chars -> fail
```

## 8. Decryption loop and character transformation
Main loop (high level):
```asm
decrypt_loop:
     lb   t0, 0(s0)          # load current char
     beqz t0, decrypt_done   # if null -> done

     # Convert to 0-based index: t1 = t0 - 'A'
     addi t1, t0, -65

     # Apply reverse shift: t1 = t1 - key (key held in s1)
     sub  t1, t1, s1

     # If negative, add 26 to make positive
     bge  t1, zero, decrypt_modulo
     addi t1, t1, 26

decrypt_modulo:
     # Ensure value in 0..25 (simple reduction)
     li   t2, 26
decrypt_modulo_loop:
     blt  t1, t2, decrypt_modulo_done
     addi t1, t1, -26
     j    decrypt_modulo_loop
decrypt_modulo_done:

     # Convert back to ASCII and store
     addi t1, t1, 65
     sb   t1, 0(s2)

     # Advance pointers
     addi s0, s0, 1
     addi s2, s2, 1
     j    decrypt_loop
```

## 9. Completion and failure handling
On success:
```asm
decrypt_done:
     sb   zero, 0(s2)    # null terminate output
     li   a0, 1          # return success
     j    decrypt_exit
```
On failure:
```asm
decrypt_fail:
     sb   zero, 0(s2)    # set output empty
     li   a0, 0          # return failure
     j    decrypt_exit
```

## 10. Mathematical foundation
Encryption: E(x) = (x + k) mod 26  
Decryption: D(x) = (x − k + 26) mod 26  
Adding 26 before modulo ensures non-negative values for correct wraparound.

## 11. Complexity and constraints
- Time complexity: O(n), n = length of input.
- Space complexity: O(1) additional (excluding input/output buffers).
- Supports only uppercase A–Z.
- Key restricted to [1, 26].

## 12. Examples
C usage:
```c
char encrypted[] = "KHOOR";
char decrypted[10];
int key = 3;

int result = decrypt_data(encrypted, key, decrypted);
// result == 1
// decrypted == "HELLO"
```

Integration with `encrypt_data`:
```c
char original[] = "HELLO";
char encrypted[10];
char decrypted[10];
int key = 3;

encrypt_data(original, key, encrypted); // encrypted == "KHOOR"
decrypt_data(encrypted, key, decrypted); // decrypted == "HELLO"
```

This document summarizes the RISC‑V implementation and logic for `decrypt_data`, including validation, loop logic, and register/stack usage.
