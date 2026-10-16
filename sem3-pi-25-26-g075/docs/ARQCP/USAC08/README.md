
---

# `USAC08` Documentation

This document details the RISC-V assembly implementation of the `sort_array` function, developed for User Story **USAC08**.

## 1. Function's Purpose

The `sort_array` function is designed to sort an array of integers either in ascending or descending order. It implements the classic **Bubble Sort** algorithm, which is straightforward and effective. The function modifies the input array in-place.

## 2. Function Signature (C)

The assembly implementation corresponds to the following C function:

```c
int sort_array(int* vec, int length, char order);
```

## 3. Parameters and Return Value

### Parameters

*   `int* vec` **(Input/Output)**: A pointer to the integer array that will be sorted. The sorting is done in-place, so the original array will be modified.
*   `int length` **(Input)**: The number of elements in the array.
*   `char order` **(Input)**: A flag that determines the sorting order.
    *   `1`: Sort in **ascending** order (smallest to largest).
    *   `0`: Sort in **descending** order (largest to smallest).

### Return Value

*   **`1` (Success)**: If the sorting process is initiated (i.e., if `length` > 0). The array will be sorted upon return.
*   **`0` (Failure)**: If the operation cannot be performed, which occurs if `length` is less than or equal to zero.

## 4. Calling Convention (RISC-V ABI)

The function adheres to the standard RISC-V calling convention.

| Register    | Purpose                            |
|:------------|:-----------------------------------|
| `a0`        | `vec` pointer / Return value       |
| `a1`        | `length` integer                   |
| `a2`        | `order` character                  |
| `s0`-`s6`   | Saved registers for internal use   |
| `sp`        | Stack Pointer for stack management |
| `ra`        | Return Address                     |

## 5. Implementation Logic and Code Analysis

The core of the function is an implementation of the Bubble Sort algorithm.

*   **Function Entry & Context Save**: At the start, the function saves all necessary callee-saved registers (`ra` and `s0`-`s6`) to the stack. It allocates 32 bytes for this purpose.
*   **Function Exit & Context Restore**: Before returning, the `restore_return` routine restores all saved registers from the stack and deallocates the stack space.

```assembly
# Prologue
addi sp, sp, -32        # Allocate 32 bytes: 32 bytes for 8 saved registers (ra, s0-s6).
sw   ra, 28(sp)
# ... (sw instructions for s0-s6)

# ... (function body) ...

restore_return:
    # Epilogue
    lw   ra, 28(sp)
    # ... (lw instructions for s0-s6)
    addi sp, sp, 32
    ret
```

### 5.2. Initial Validation

The first step is to validate the `length` of the array. If `length` is less than or equal to zero, the function immediately branches to the `failure` routine, sets the return value to 0, and exits.

```assembly
# Return failure if length <= 0
blez s1, failure
```

### 5.3. Bubble Sort Algorithm (Nested Loops)

The function uses two nested loops to perform the sort.

*   **Outer Loop (`i`)**: This loop runs from `i = 0` to `length - 2`. After each full iteration of this loop, the next largest (or smallest) element is guaranteed to be in its final sorted position at the end of the unsorted portion of the array.
*   **Inner Loop (`j`)**: This loop runs from `j = 0` to `length - i - 2`. It iterates through the unsorted portion, comparing adjacent elements.

```assembly
# Outer Loop: for (i = 0; i < length - 1; i++)
li   s3, 0              # s3 = i = 0
outer_loop_start:
    # ... (check if i < length - 1) ...

    # Inner Loop: for (j = 0; j < length - i - 1; j++)
    li   s4, 0            # s4 = j = 0
inner_loop_start:
    # ... (check if j < length - i - 1) ...
```

### 5.4. Comparison and Swap Logic

This is the core of the Bubble Sort. Inside the inner loop, for each `j`:
1.  The memory addresses of `vec[j]` and `vec[j+1]` are calculated.
2.  The values at these addresses are loaded into temporary registers.
3.  Based on the `order` parameter, the code decides whether to perform an ascending or descending comparison.
4.  If the elements are out of order, they are swapped in memory.

```assembly
    # Calculate addresses of vec[j] and vec[j+1]
    slli t0, s4, 2           # t0 = j * 4
    add  s5, s0, t0          # s5 = &vec[j]
    addi s6, s5, 4           # s6 = &vec[j+1]

    # Load values
    lw   t1, 0(s5)          # t1 = vec[j]
    lw   t2, 0(s6)          # t2 = vec[j+1]

    # Branch based on order
    beqz s2, compare_descending

compare_ascending:
    # if (vec[j] > vec[j+1]), swap
    ble  t1, t2, continue_inner_loop
    j    swap_elements

compare_descending:
    # if (vec[j] < vec[j+1]), swap
    bge  t1, t2, continue_inner_loop

swap_elements:
    sw   t2, 0(s5)          # vec[j] = value from vec[j+1]
    sw   t1, 0(s6)          # vec[j+1] = value from vec[j]
    
```

### 5.5. Success and Failure Handling

The function has two main exit paths that set the return value in register `a0` before jumping to the Function Exit & Context Restore.

```assembly
sort_done:
    li   a0, 1            # Set return value to 1 (success)
    j    restore_return

failure:
    li   a0, 0            # Set return value to 0 (failure)
    # Fall-through to restore_return
```