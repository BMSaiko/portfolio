
---

# `USAC07` Documentation

This document details the RISC-V assembly implementation of the `move_n_to_array` function, developed for User Story **USAC07**. This function is a core component for managing a circular buffer data structure.

## 1. Function's Purpose

The `move_n_to_array` function is designed to dequeue (remove) a specified number of elements from a circular buffer. It removes the `n` oldest elements from the buffer's "tail" (read position) and copies them sequentially into a destination array. This operation is fundamental for consuming data from a buffer in batches.

The function correctly handles the wrap-around logic of a circular buffer, where the read index resets to the beginning after reaching the end of the allocated memory. It also updates the buffer's state, including the number of elements and the read pointer.

## 2. Function Signature (C)

The assembly implementation corresponds to the following C function:

```c
int move_n_to_array(int* buffer, int length, int* nelem, int* tail, int* head, int n, int* array);
```

## 3. Parameters and Return Value

### Parameters

*   `int* buffer` **(Input)**: A pointer to the start of the circular buffer's memory array.
*   `int length` **(Input)**: The total capacity (maximum number of elements) of the circular buffer.
*   `int* nelem` **(Input/Output)**: A pointer to an integer that holds the current number of elements in the buffer. This value is read and then updated by the function.
*   `int* tail` **(Input/Output)**: A pointer to an integer representing the read index (the position of the oldest element). This value is read and then updated. The name `tail` is used interchangeably with `read`.
*   `int* head` **(Input)**: A pointer to an integer representing the write index. This parameter is **ignored** by this function as it is not needed for a dequeue operation.
*   `int n` **(Input)**: The number of elements to remove from the buffer and move to the destination array.
*   `int* array` **(Output)**: A pointer to the destination array where the removed elements will be stored.

### Return Value

*   **`1` (Success)**: If the `n` elements were successfully moved from the buffer to the array.
*   **`0` (Failure)**: If the operation cannot be completed, which occurs if `n` is less than or equal to zero, or if there are fewer than `n` elements currently in the buffer.

## 4. Calling Convention (RISC-V ABI)

The function adheres to the standard RISC-V calling convention.

| Register  | Purpose                               |
|:----------|:--------------------------------------|
| `a0`      | `buffer` pointer / Return value       |
| `a1`      | `length` integer                      |
| `a2`      | `nelem` pointer                       |
| `a3`      | `tail` pointer                        |
| `a4`      | `head` pointer (unused)               |
| `a5`      | `n` integer                           |
| `a6`      | `array` pointer (output)              |
| `s0`-`s7` | Saved registers for internal use      |
| `sp`      | Stack Pointer for stack management    |
| `ra`      | Return Address                        |

## 5. Implementation Logic and Code Analysis

The algorithm first validates the request, then enters a loop to move the data, and finally updates the buffer's state.


*   **Function Entry & Context Save**: Standard function entry. It saves all necessary callee-saved registers (`ra`, `s0`-`s7`) onto the stack to preserve the caller's context.
*   **Function Exit & Context Restore**: Standard function exit. It restores the saved registers from the stack and deallocates the stack space before returning.

### 5.1. Initial Validation

Before performing any operations, the function validates the input parameters. The values of `*nelem` and `*tail` are loaded from memory into saved registers (`s6` and `s7`) for efficient access.

*   The function fails if `n` (the number of elements to move) is zero or negative.
*   It also fails if the buffer contains fewer than `n` elements.

```assembly
# Load values from pointers into registers
lw   s6, 0(s2)        # s6 = *nelem
lw   s7, 0(s3)        # s7 = *tail

# Fail if n <= 0
blez s4, failure
# Fail if n > *nelem
bgt  s6, s4, failure
```

### 5.2. Data Transfer Loop

If the validation passes, the function enters a loop that iterates `n` times to move each element. A loop counter (`t0`) is initialized to zero.

```assembly
    li   t0, 0            # t0 = i = 0 (loop counter)
move_loop:
    bge  t0, s4, loop_done  # if (i >= n), exit loop
```

Inside the loop, the following steps are performed for each element:

1.  **Calculate Source Address**: The memory address of the element at the current `tail` index is calculated: `address = buffer + (tail * 4)`.
2.  **Read Element**: The integer value is loaded from the calculated source address.
3.  **Calculate Destination Address**: The memory address in the output `array` at index `i` is calculated: `address = array + (i * 4)`.
4.  **Write Element**: The value read from the buffer is stored at the calculated destination address.
5.  **Update `tail` Index**: The `tail` index is incremented. If `tail` reaches `length`, it returns back to 0.

```assembly
    # a. Calculate source address: &buffer[tail]
    slli t1, s7, 2           # t1 = tail * 4
    add  t2, s0, t1         # t2 = buffer + offset
    lw   t3, 0(t2)          # Read buffer[tail]

    # c. Calculate destination address: &array[i]
    slli t4, t0, 2           # t4 = i * 4
    add  t5, s5, t4         # t5 = array + offset
    sw   t3, 0(t5)          # Write to array[i]

    # e. Update tail index with wrap-around
    addi s7, s7, 1           # tail++
    bne  s7, s1, tail_no_wrap # if (tail != length), skip
    li   s7, 0              # else, tail = 0
tail_no_wrap:

    addi t0, t0, 1           # i++
    j    move_loop
```

### 5.3. Final State Update and Return

After the loop completes, the function updates the values of `*nelem` and `*tail` in memory with the final values calculated in the registers.

*   The number of elements is decreased by `n`.
*   The `tail` pointer is updated to its new position.

Finally, the function sets the return value to `1` for success or `0` for failure before jumping to the restore_return to exit.

```assembly
loop_done:
    # Update *nelem = *nelem - n
    sub  s6, s6, s4
    sw   s6, 0(s2)

    # Update *tail with its final value
    sw   s7, 0(s3)

success:
    li   a0, 1
    j    restore_return

failure:
    li   a0, 0
    # Fall-through to restore_return
```