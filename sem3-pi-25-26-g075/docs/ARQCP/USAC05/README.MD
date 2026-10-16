# USAC05 Documentation

This document describes a RISC‑V assembly implementation of the `enqueue_value` function for User Story **USAC05** (ARQCP). The function implements insertion into a fixed‑size circular (ring) buffer with automatic overwrite of the oldest element when full.

## 1. Function purpose
Insert a new integer into a circular buffer. If the buffer is full, overwrite the oldest element by advancing the tail. Returns 1 if the buffer is full after insertion, otherwise 0.

## 2. Function signature (C)
```c
int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);
```

## 3. Parameters and return value
- `int* buffer` (in/out): pointer to integer array (the circular buffer).  
- `int length` (in): buffer capacity.  
- `int* nelem` (in/out): pointer to number of elements currently in the buffer.  
- `int* tail` (in/out): pointer to index of the oldest element.  
- `int* head` (in/out): pointer to index where the next element will be inserted.  
- `int value` (in): value to insert.

Return:
- `1` if the buffer is full after insertion.  
- `0` if the buffer is not full after insertion.

## 4. Calling convention (RISC‑V ABI)
Arguments / return via registers:
- `a0` — buffer pointer  
- `a1` — length  
- `a2` — nelem pointer  
- `a3` — tail pointer  
- `a4` — head pointer  
- `a5` — value to insert  
- `a0` — return value

Saved registers used: `s0`–`s4`. Stack pointer `sp`, return address `ra`.

## 5. Implementation logic and assembly excerpts
High level: check if buffer full, if full advance tail (overwrite), otherwise increment `nelem`, then store `value` at `head` and advance `head` with wrap‑around. Finally, return whether buffer is full.

### 5.1 Prologue / epilogue
```asm
enqueue_value:
    addi sp, sp, -24
    sw   ra, 20(sp)
    sw   s0, 16(sp)
    sw   s1, 12(sp)
    sw   s2, 8(sp)
    sw   s3, 4(sp)
    sw   s4, 0(sp)
    ...
enqueue_exit:
    lw   ra, 20(sp)
    lw   s0, 16(sp)
    lw   s1, 12(sp)
    lw   s2, 8(sp)
    lw   s3, 4(sp)
    lw   s4, 0(sp)
    addi sp, sp, 24
    ret
```

### 5.2 Save parameters to preserved registers
```asm
mv s0, a0    # buffer pointer
mv s1, a1    # length
mv s2, a2    # nelem pointer
mv s3, a3    # tail pointer
mv s4, a4    # head pointer
```

### 5.3 Load state and check full
```asm
lw t0, 0(s2)   # t0 = *nelem
lw t1, 0(s3)   # t1 = *tail
lw t2, 0(s4)   # t2 = *head
bne t0, s1, not_full
```

### 5.4 Full buffer handling (advance tail)
```asm
# buffer full: advance tail (overwrite oldest)
slli t3, t1, 2
add  t3, s0, t3
addi t1, t1, 1
blt  t1, s1, tail_ok_full
mv   t1, zero
tail_ok_full:
sw   t1, 0(s3)
j    insert_element
```

### 5.5 Not full (increment nelem)
```asm
not_full:
    addi t0, t0, 1
    sw   t0, 0(s2)
```

### 5.6 Insert element and advance head
```asm
insert_element:
    slli t3, t2, 2
    add  t3, s0, t3
    sw   a5, 0(t3)     # buffer[head] = value
    addi t2, t2, 1
    blt  t2, s1, head_ok
    mv   t2, zero
head_ok:
    sw   t2, 0(s4)
```

### 5.7 Return value determination
```asm
lw t0, 0(s2)
beq t0, s1, return_full
li a0, 0
j  enqueue_exit
return_full:
li a0, 1
j  enqueue_exit
```

## 6. Circular buffer mechanics
Example buffer layout (length = 5):
```text
Buffer: [ _, _, _, _, _ ]
Indices: 0 1 2 3 4
```
- Tail: index of oldest element.  
- Head: index for next insertion.  
- Wrap-around: indices reset to 0 when reaching `length`.

State examples:
```text
Empty: nelem=0, tail=0, head=0
After 3 inserts: nelem=3, tail=0, head=3
Full (5): nelem=5, tail=0, head=0
Insert when full: nelem=5, tail=1, head=1  # oldest overwritten
```

## 7. Characteristics
- Time complexity: O(1)  
- Space: O(1) additional  
- Behavior: fixed-size FIFO with overwrite on full  
- Thread safety: not thread-safe (external synchronization required)

## 8. Example usage (C)
```c
#define BUFFER_SIZE 5
int buffer[BUFFER_SIZE];
int nelem = 0;
int tail = 0;
int head = 0;

for (int i = 1; i <= 6; ++i) {
    int result = enqueue_value(buffer, BUFFER_SIZE, &nelem, &tail, &head, i * 10);
    printf("Enqueued %d, buffer full: %s\n", i * 10, result ? "YES" : "NO");
}
```
Expected:
- Enqueued 10, buffer full: NO
- Enqueued 20, buffer full: NO
- Enqueued 30, buffer full: NO
- Enqueued 40, buffer full: NO
- Enqueued 50, buffer full: YES
- Enqueued 60, buffer full: YES (overwrites 10)

## 9. Error handling and edge cases
- Zero length: function compiles but has no useful behavior.  
- Null pointers: will cause faults — caller must validate.  
- Full buffer: handled by overwriting oldest element.  
- Empty buffer: normal insertion behavior.

This provides a concise, correct description and assembly outline for the circular buffer insertion function.
