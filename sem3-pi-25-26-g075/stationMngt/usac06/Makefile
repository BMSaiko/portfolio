CC=riscv32-linux-gcc
CFLAGS= -Wall -Wextra -fanalyzer -g
ARCHFLAGS= -march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o asc06.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o asc06.o -o program.elf
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c
asc06.o: asc06.s asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asc06.s
clean:
	rm -f *.o *.elf
run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
debug: program.elf
	@echo "On other terminal window do:"
	@echo "riscv32-linux-gdb-tui ./program.elf"
	qemu-riscv32 -L $(LIBRV32) -g 1234 program.elf -S