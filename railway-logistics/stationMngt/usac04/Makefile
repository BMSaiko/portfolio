CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -fanalyzer -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o ac04.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o ac04.o -o program.elf
	
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c
	
ac04.o: ac04.s ac04.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ac04.s

clean:
	rm -f *.o *.elf

run: program.elf 
	qemu-riscv32 -L $(LIBRV32) ./program.elf

debug: program.elf
	@echo "On other terminal window do:"
	@echo "riscv32-linux-gdb -tui ./program.elf"
	qemu_riscv32 -L $(LIBRV32) -g 1234 program.elf -S
