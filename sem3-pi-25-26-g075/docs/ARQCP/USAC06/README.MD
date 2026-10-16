# USAC06 Documentation

This document describes the RISC-V assembly implementation of `dequeue_value` (User Story USAC06) — a FIFO removal operation for a fixed-size circular (ring) buffer.

## 1. Function purpose
Remove and return the oldest element from a circular buffer. Returns 1 on success, 0 if the buffer is empty. Intended for streaming/real-time contexts; not thread-safe (external synchronization required).

## 2. Function signature (C)
```c
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value);
```

## 3. Parameters and return value
- buffer (int* input): pointer to integer array acting as the circular buffer.  
- length (int input): capacity of the buffer (number of slots).  
- nelem (int* in/out): pointer to current element count.  
- tail (int* in/out): pointer to index of the oldest element.  
- head (int* in/out): pointer to index for next insertion.  
- value (int* out): pointer where the dequeued value will be stored.

Return:
- 1 — dequeue succeeded (element removed).
- 0 — dequeue failed (buffer empty).

## 4. Calling convention (RISC‑V ABI)
Arguments / return registers:
- a0: buffer pointer (also return value)
- a1: length
- a2: nelem pointer
- a3: tail pointer
- a4: head pointer
- a5: value pointer (output)
Saved registers used by the function: s0–s3. sp used for stack frame. ra for return address.

## 5. Implementation overview (RISC‑V assembly)
High-level steps:
1. Prologue — save context and allocate stack.
2. Save arguments in preserved registers.
3. Check if buffer is empty (*nelem == 0) — if so, return 0.
4. Compute address of buffer[tail], load value, store to *value.
5. Advance tail with wrap-around: tail = (tail + 1) % length.
6. Decrement *nelem.
7. Return 1 on success; epilogue restores context and returns.

Relevant assembly excerpts:

Prologue / epilogue:
```asm
dequeue_value:
    addi sp, sp, -20
    sw   ra, 16(sp)
    sw   s0, 12(sp)
    sw   s1, 8(sp)
    sw   s2, 4(sp)
    sw   s3, 0(sp)
    ...
dequeue_exit:
    lw   ra, 16(sp)
    lw   s0, 12(sp)
    lw   s1, 8(sp)
    lw   s2, 4(sp)
    lw   s3, 0(sp)
    addi sp, sp, 20
    ret
```

Save arguments:
```asm
    mv s0, a0    # buffer pointer
    mv s1, a1    # length
    mv s2, a2    # nelem pointer
    mv s3, a3    # tail pointer
```

Empty check:
```asm
    lw  t0, 0(s2)   # t0 = *nelem
    beqz t0, dequeue_fail
```

Value extraction:
```asm
    lw  t1, 0(s3)       # t1 = tail
    slli t2, t1, 2      # t2 = tail * 4
    add  t2, s0, t2     # t2 = &buffer[tail]
    lw   t3, 0(t2)      # t3 = buffer[tail]
    sw   t3, 0(a5)      # *value = t3
```

Tail update (with wrap-around):
```asm
    addi t1, t1, 1
    blt  t1, s1, tail_ok_dequeue
    mv   t1, zero
tail_ok_dequeue:
    sw   t1, 0(s3)      # *tail = new tail
```

Element count update:
```asm
    lw   t0, 0(s2)
    addi t0, t0, -1
    sw   t0, 0(s2)
```

Return success / failure:
```asm
    li   a0, 1
    j    dequeue_exit

dequeue_fail:
    li   a0, 0
    j    dequeue_exit
```

## 6. Circular buffer mechanics
- Empty: tail == head AND nelem == 0.  
- Full: tail == head AND nelem == length.  
- tail points to oldest element; head points to next insertion index.  
Time complexity: O(1). Additional space: O(1).

## 7. Example usage (C)
```c
#define BUFFER_SIZE 5
int buffer[BUFFER_SIZE] = {10, 20, 30, 0, 0};
int nelem = 3;
int tail = 0;
int head = 3;
int value;

while (1) {
    int result = dequeue_value(buffer, BUFFER_SIZE, &nelem, &tail, &head, &value);
    if (result == 0) { printf("Buffer is empty!\n"); break; }
    printf("Dequeued value: %d\n", value);
    printf("Remaining elements: %d\n", nelem);
}
```

Expected sequence:
Dequeued: 10 (nelem -> 2), 20 (nelem -> 1), 30 (nelem -> 0), then empty.

## 8. Integration with USAC05
This function pairs with a typical `enqueue_value` implementation (USAC05) that updates head and nelem; together they provide FIFO semantics for the circular buffer.

## 9. Edge cases and error conditions
- Empty buffer: returns 0, no modifications.  
- Single element: tail and head become equal after removal.  
- Wrap-around handled when tail reaches length.  
- Not thread-safe — concurrent access must be synchronized.

This cleaned Markdown presents the algorithm, calling convention, and assembly snippets required to implement `dequeue_value` in RISC‑V.
