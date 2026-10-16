
---

# `USAC09` Documentation

This document details the RISC-V assembly implementation of the `median` function, developed for User Story **USAC09**.

## 1. Function's Purpose

The `median` function calculates the median value of an integer array. To achieve this, it first sorts the array in-place in **ascending order** and then determines the middle value. The function correctly handles both odd and even length arrays.

## 2. Function Signature (C Prototype)

The assembly implementation corresponds to the following C function prototype:

```c
int median(int* vec, int length, int *me);
```

## 3. Parameters and Return Value

### Parameters

*   `int* vec` **(Input/Output)**: A pointer to the integer array. The function will sort this array in-place, so its contents will be modified.
*   `int length` **(Input)**: The number of elements in the array.
*   `int* me` **(Output)**: A pointer to an integer where the calculated median value will be stored.

### Return Value

*   **`1` (Success)**: If the median was successfully calculated (i.e., if `length` > 0).
*   **`0` (Failure)**: If the operation cannot be performed, which occurs if `length` is less than or equal to zero.

## 4. Calling Convention (RISC-V ABI)

The function adheres to the standard RISC-V calling convention.

| Register  | Purpose                               |
|:----------|:--------------------------------------|
| `a0`      | `vec` pointer / Return value          |
| `a1`      | `length` integer                      |
| `a2`      | `me` pointer (output)                 |
| `s0`-`s6` | Saved registers for internal use      |
| `sp`      | Stack Pointer for stack management    |
| `ra`      | Return Address                        |

## 5. Implementation Logic and Code Analysis

The function's logic is divided into three main stages: validation, in-place sorting, and median calculation.

*   **Function Entry & Context Save**: The function begins by saving all necessary callee-saved registers (`ra`, `s0`-`s6`) to the stack, allocating 32 bytes for this purpose.
*   **Function Exit & Context Restore**: The `restore_return` routine restores the saved registers and deallocates the stack space before the function returns.

### 5.1. Initial Validation

The first operational step is to check the array's `length`. If it is zero or less, sorting is not possible. The function immediately branches to the `failure` routine.

```assembly
# if length <= 0 jumps to failure
blez s1, failure
```

### 5.2. In-place Sorting (Ascending)

The function integrates a complete Bubble Sort algorithm. As noted in the source code, this logic is re-used and adapted from the implementation in `USAC08`. The sorting is hardcoded to be **ascending**, which is a prerequisite for correctly identifying the median.

```assembly
# ... (outer and inner loop setup) ...

    lw   t1, 0(s5)          # t1 = vec[j]
    lw   t2, 0(s6)          # t2 = vec[j+1]

    # The comparison is always ascending:
    # if vec[j] <= vec[j+1], the elements are in order, so skip the swap.
    ble  t1, t2, continue_inner_loop

    # Swap only if vec[j] > vec[j+1]
    sw   t2, 0(s5)
    sw   t1, 0(s6)
# ... (loop increments and branches) ...
```

### 5.3. Median Calculation

Once the array is sorted (`sort_done`), the function proceeds to calculate the median. It first determines if the array's length is odd or even.

```assembly
# Verifies if the length is even or odd
andi t0, s1, 1              # t0 = length & 1
beqz t0, handle_even_length # if t0 is 0, length is even
```

#### 5.3.1. Odd Length

If the length is odd, the median is the single element in the middle of the array, located at index `(length - 1) / 2`.

```assembly
handle_odd_length:
    # ...
    sub  t0, s1, t0       # t0 = length - 1
    srai t0, t0, 1        # t0 = (length - 1) / 2 (division by 2)
    slli t1, t0, 2        # t1 = index * 4 (byte offset)
    add  t2, s0, t1       # t2 = address of the median element
    lw   t3, 0(t2)          # t3 = median value
    j    store_median
```

#### 5.3.2. Even Length

If the length is even, the median is the integer average of the two middle elements. These are located at indices `(length / 2) - 1` and `length / 2`.

```assembly
handle_even_length:
    srai t0, s1, 1        # t0 = length / 2
    # ... (calculate index1 = t0 - 1) ...

    # Load the two middle values
    # ... (lw instructions for vec[index1] and vec[index2]) ...
    
    # Calculate the integer average
    add  t3, t3, t4       # t3 = value1 + value2
    srai t3, t3, 1        # t3 = (value1 + value2) / 2
```

### 5.4. Storing the Result and Exiting

The calculated median (`t3`) is stored at the memory location pointed to by `me`. The function then sets the return value and jumps to the restore_return.

```assembly
store_median:
    sw   t3, 0(s2)        # *me = median

success:
    li   a0, 1
    j    restore_return

failure:
    li   a0, 0
    # Fall-through to restore_return
```