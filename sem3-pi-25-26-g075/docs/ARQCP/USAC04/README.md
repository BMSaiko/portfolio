
---

# `USAC04` Documentation

This document details the RISC-V assembly implementation of the `format_command` function, developed for User Story **USAC04**.

## 1. Function's Purpose

The `format_command` function is designed to validate and format a command string. Its purpose is to take a potentially messy input string (`op`), clean it up by trimming whitespace and converting it to uppercase, and validate it against a list of predefined commands (RE, YE, GE, RB, GTH). If the command is valid, it formats an output string (`cmd`) according to specific rules, incorporating a provided integer (`n`).

This functionality is crucial for creating standardized command messages from potentially varied user input.

## 2. Function Signature (C)

The assembly implementation corresponds to the following C function:

```c
int format_command(char* op, int n, char *cmd);
```

## 3. Parameters and Return Value

### Parameters

*   `char* op` **(Input)**: A pointer to the input command string (e.g., `" rB "`).
*   `int n` **(Input)**: An integer value to be included in the formatted output. This value must be in the range for most commands and is ignored for the "GTH" command.
*   `char* cmd` **(Output)**: A pointer to a character buffer where the formatted output string will be stored.

### Return Value

*   **`1` (Success)**: If the input `op` is a valid command and all conditions (like the range of `n`) are met.
*   **`0` (Failure)**: If the `op` string does not correspond to a valid command after cleaning, or if the value `n` is out of the valid range for commands that require it. In this case, the output `cmd` will be set to an empty string (`""`).

## 4. Calling Convention (RISC-V ABI)

The function adheres to the standard RISC-V calling convention, where arguments and the return value are passed through the following registers:

| Register  | Purpose                            |
|:----------|:-----------------------------------|
| `a0`      | `op` pointer / Return value        |
| `a1`      | `n` integer value                  |
| `a2`      | `cmd` pointer (output)             |
| `s0`-`s4` | Saved registers for internal use   |
| `sp`      | Stack Pointer for stack management |
| `ra`      | Return Address                     |

## 5. Implementation Logic and Code Analysis

The algorithm is a multi-step process involving cleaning, validation, and formatting. Each step is described below.

### 5.1. Stack Management

The function begins with a **prologue** to set up its execution environment and ends with an **exit routine** (`restore_return`) to clean it up.

*   **Function Entry & Context Save**: The prologue saves all necessary callee-saved registers (`ra`, `s0`-`s4`) onto the stack. It also reserves 16 bytes on the stack to be used as a temporary buffer for the cleaned command string.
*   **Function Exit & Context Restore**: The `restore_return` routine restores the saved registers from the stack and deallocates the stack space before returning control to the caller.

```assembly
# Function Entry & Context Save
addi sp, sp, -48      # Allocate 48 bytes on the stack
sw   ra, 44(sp)       # Save the return address
sw   s0, 40(sp)       # Save s0..s4
# ... (remaining sw instructions)
addi s3, sp, 0        # s3 points to the start of the temp buffer

# ... (function body) ...

restore_return:
    # Function Exit & Context Restore
    lw   ra, 44(sp)       # Restore saved registers
    # ... (remaining lw instructions)
    addi sp, sp, 48       # Deallocate stack space
    ret                   # Return```

### 5.2. Command Cleaning: Trimming and Capitalization

The first logical step is to process the raw input string `op`. The code trims leading whitespace, then copies the command's characters to the temporary buffer on the stack, converting any lowercase letters to uppercase. This process stops at the first trailing space or the end of the string.

```assembly
find_first_char:
    # ... (loop to skip leading spaces in 'op') ...

copy_and_capitalize_loop:
    lb   t0, 0(s4)        # Load char from 'op'
    # ... (check for space or null terminator to end copy) ...

    # Capitalize if 'a' <= char <= 'z'
    addi t0, t0, -32      # 'a' - 'A' = 32

store_char:
    sb   t0, 0(s3)        # Store cleaned char in temp buffer
    # ... (advance pointers and loop) ...

copy_done:
    sb   zero, 0(s3)      # Null-terminate the cleaned command string
```

### 5.3. Command Validation

With a clean, standardized command in the temporary buffer, the function validates it against the five allowed commands. A helper subroutine, `string_equals`, is used to perform the comparisons.

```assembly
# Compare with "RE"
la   s4, re_str
jal  ra, string_equals
beq  a0, zero, handle_re # If equal (a0=0), branch to its handler

# ... (repeat for "YE", "GE", "RB", "GTH") ...

j    invalid_command     # If no matches, fail.

# --- Helper subroutine: string_equals ---
# Compares two null-terminated strings.
# Args: s3 (str1), s4 (str2). Rets: a0 = 0 if equal, 1 otherwise.
string_equals:
    # ... (byte-by-byte comparison loop) ...
```

### 5.4. Output Formatting

If the command is valid, the code branches to a handler that formats the output string `cmd`. There are two distinct formatting paths.

#### 5.4.1. Formatting for "RE", "YE", "GE", "RB"

These commands require the format `CMD,XX`, where `XX` is the two-digit representation of the input integer `n`. The number `n` is also validated to be within the range.

```assembly
format_cmd_with_num:
    # Validate number n (0 <= n < 100)
    blt  s1, zero, invalid_command
    li   t0, 100
    bge  s1, t0, invalid_command

    # Copy CMD (e.g., "RB") and a comma to the output buffer
    # ... (sb instructions) ...

    # Convert number n to two digits
    li   t0, 10
    divu t1, s1, t0       # t1 = n / 10 (tens digit)
    remu t2, s1, t0       # t2 = n % 10 (ones digit)

    # Convert digits to ASCII and store them
    # ... (add '0', then sb instructions) ...
```

#### 5.4.2. Formatting for "GTH"

This command has a simpler format where the number `n` is ignored.

```assembly
handle_gth:
    # Copy "GTH" to the output buffer
    la   s4, gth_str
    # ... (sb instructions to copy 3 chars) ...
    sb   zero, 3(s2)      # Null-terminate the string
    j    success
```

### 5.5. Failure and Success Handling

All logical paths conclude by setting the return value in `a0` and then jumping to the `restore_return` routine to exit cleanly.

```assembly
success:
    li   a0, 1            # Set return value to 1 (success)
    j    restore_return

invalid_command:
    sb   zero, 0(s2)      # cmd = "" (empty string)
    li   a0, 0            # Set return value to 0 (failure)
    j    restore_return   # Jump to the cleanup and exit routine
```