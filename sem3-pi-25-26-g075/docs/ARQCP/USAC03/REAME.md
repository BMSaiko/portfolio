
---

# `USAC03` Documentation

This document details the RISC-V assembly implementation of the `extract_data` function, developed for User Story **USAC03**.

## 1. Function's Purpose

The `extract_data` function is designed to parse a formatted string containing multiple data records. Its purpose is to search for a specific `token` (a key, such as "TEMP" or "HUM") and, if it is found as a whole word and not as a substring, extract the associated `unit` and `value`.

This functionality is essential for interpreting data from sensors or configuration files where information is concatenated into a single line of text.

## 2. Function Signature (C)

The assembly implementation corresponds to the following C function:

```c
int extract_data(char* str, char* token, char* unit, int* value);
```

## 3. Parameters and Return Value

### Parameters

*   `char* str` **(Input)**: A pointer to the input string containing the data to be parsed. The expected format is `"TOKEN1&unit:unit1&value:value1#TOKEN2&unit:unit2&value:value2"`.
*   `char* token` **(Input)**: A pointer to the string that serves as the search key.
*   `char* unit` **(Output)**: A pointer to a character buffer where the extracted unit string will be stored.
*   `int* value` **(Output)**: A pointer to an integer variable where the extracted numerical value will be stored.

### Return Value

*   **`1`**: If the `token` is found and the data (`unit` and `value`) are successfully extracted.
*   **`0`**: If the `token` is not found in the input string or if the string is malformed. In this case, the `unit` will be set to an empty string (`""`) and the `value` will be set to `0`.

## 4. Calling Convention (RISC-V ABI)

The function adheres to the standard RISC-V calling convention, where arguments and the return value are passed through the following registers:

| Register  | Purpose                            |
|:----------|:-----------------------------------|
| `a0`      | `str` pointer / Return value       |
| `a1`      | `token` pointer                    |
| `a2`      | `unit` pointer                     |
| `a3`      | `value` pointer                    |
| `s0`-`s6` | Saved registers for internal use   |
| `sp`      | Stack Pointer for stack management |
| `ra`      | Return Address                     |

## 5. Implementation Logic and Code Analysis

The algorithm implements a sequential parsing process. Each step is described below with its corresponding assembly code excerpts.

### 5.1. Stack Management

The function begins with a **Function Entry & Context Save** to set up a safe execution environment and ends with a **Function Exit & Context Restore** to clean it up.

*   **Function Entry & Context Save**: Saves all registers that must be preserved (`ra` and `s0`-`s6`) onto the stack and allocates the necessary space.
*   **Function Exit & Context Restore**: Restores the saved registers from the stack and deallocates the space before returning.

```assembly
# Function Entry & Context Save
addi sp, sp, -32      # Allocate 32 bytes on the stack
sw   ra, 28(sp)       # Save the return address
sw   s0, 24(sp)       # Save s0..s6
# ... (remaining sw instructions)

# ... (function body) ...

# Function Exit & Context Restore
restore_return:
    lw   ra, 28(sp)       # Restore saved registers
    lw   s0, 24(sp)
    # ... (remaining lw instructions)
    addi sp, sp, 32       # Deallocate stack space
    ret                   # Return
```

### 5.2. Searching for the `token` Substring

This is the initial search phase. The code iterates through the input string (`str`) and, at each position, attempts to find a matching substring for the provided `token`.

```assembly
main_loop:
    # ... pointer initialization ...
compare_loop:
    lb   t1, 0(s1)        # Load char from token
    beq  t1, zero, token_found # End of token -> Match found!

    lb   t0, 0(s0)        # Load char from str
    bne  t0, t1, no_match # If characters differ, restart match

    # Characters are equal, advance pointers and continue comparing
    addi s0, s0, 1
    addi s1, s1, 1
    j    compare_loop

no_match:
    addi s4, s4, 1        # Advance to the next character in the main string
    j    main_loop
```

### 5.3. Token Validation: Distinguishing Substring from Whole Word

After finding a matching substring, it is crucial to validate if it's a standalone token and not part of a larger word. A valid token must satisfy two conditions:

1.  It must be immediately followed by the `&` delimiter.
2.  It must be preceded by the start of the string or by the `#` record delimiter.

This step is key to preventing false positives (e.g., finding "EMP" inside "TEMP").

```assembly
token_found:
    # Condition 1: Check if the character AFTER the match is '&'
    lb   t0, 0(s0)
    li   t1, '&'
    bne  t0, t1, no_match      # If not, it's not a valid token.

    # Condition 2: Check if the match was at the very beginning of the string...
    beq  s4, a0, valid_token # If so, it's valid.

    # ...or if it was preceded by '#'
    lb   t0, -1(s4)            # Load the character before the match
    li   t1, '#'
    bne  t0, t1, no_match      # If not, it's not a valid token.

valid_token:
    # Both conditions were met. Proceed with extraction.
```

### 5.4. Extracting the `unit`

Once a valid `token` is confirmed, the function searches for the `&unit:` label and copies the subsequent characters to the output buffer.

```assembly
# ... (logic to find the "&unit:" label) ...

unit_label_found:
    # s0 points to the char after "&unit:". Begin extraction.
extract_unit_loop:
    lb   t0, 0(s0)        # Load char
    li   t1, '&'
    beq  t0, t1, unit_done # '&' marks the end of the unit

    sb   t0, 0(s2)        # Store character in the 'unit' buffer
    addi s0, s0, 1
    addi s2, s2, 1        # Advance pointers
    j    extract_unit_loop

unit_done:
    sb   zero, 0(s2)      # Add null terminator ('\0') to the 'unit' string
```

### 5.5. Extracting and Converting the `value`

The final step is to find the `&value:` label and convert the following string of digits into an integer.

```assembly
# ... (logic to find the "&value:" label) ...

value_label_found:
    # s0 points to the char after "&value:".
    li   t2, 0            # t2 = accumulator for the integer value (starts at 0)

extract_value_loop:
    lb   t0, 0(s0)        # Load character

    # Check if it is a digit ('0' through '9')
    # ... (blt/bgt to check the range) ...

    # Convert from ASCII to integer (e.g., '5' (53) - '0' (48) = 5)
    addi t0, t0, -'0'

    # Update the result: result = (result * 10) + new_digit
    li   t1, 10
    mul  t2, t2, t1
    add  t2, t2, t0

    addi s0, s0, 1
    j    extract_value_loop

value_done:
    sw   t2, 0(s3)        # Store the final integer value in the 'value' pointer
    li   a0, 1            # Set return value to 1 (success)
    j    restore_return
```

### 5.6. Failure Handling

If at any point the `token` is not found or the string ends unexpectedly, control is transferred to the `not_found` label, which prepares a failure return.

```assembly
not_found:
    sb   zero, 0(s2)      # unit = "" (empty string)
    sw   zero, 0(s3)      # *value = 0
    li   a0, 0            # Set return value to 0 (failure)
    j    restore_return   # Jump to the cleanup and exit routine
```