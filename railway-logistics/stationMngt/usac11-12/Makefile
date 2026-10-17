
CC=riscv32-linux-gcc
CFLAGS= -Wall -Wextra -fanalyzer -g
ARCHFLAGS= -march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

all: usac11_test.elf usac12_test.elf

# USAC11
usac11_test.elf: test_usac11.o manager.o asc01.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) test_usac11.o manager.o asc01.o -o usac11_test.elf

test_usac11.o: test_usac11.c manager.h asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c test_usac11.c

# USAC12
usac12_test.elf: test_usac12.o manager.o asc01.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) test_usac12.o manager.o asc01.o -o usac12_test.elf

test_usac12.o: test_usac12.c manager.h asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c test_usac12.c


manager.o: manager.c manager.h asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager.c

asc01.o: asc01.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asc01.s

clean:
	rm -f *.o *.elf

run_usac11: usac11_test.elf config.txt
	qemu-riscv32 -L $(LIBRV32) ./usac11_test.elf

run_usac12: usac12_test.elf config.txt
	qemu-riscv32 -L $(LIBRV32) ./usac12_test.elf

debug: usac12_test.elf
	@echo "Em outro terminal execute:"
	@echo "riscv32-linux-gdb-tui ./usac12_test.elf"
	qemu-riscv32 -L $(LIBRV32) -g 1234 usac12_test.elf -S
