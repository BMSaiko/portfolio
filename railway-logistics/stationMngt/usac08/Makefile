CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -fanalyzer -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o ac08.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o ac08.o -o program.elf
	
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c
	
ac08.o: ac08.s ac08.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ac08.s

clean:
	rm -f *.o *.elf

run: program.elf 
	qemu-riscv32 -L $(LIBRV32) ./program.elf

debug: program.elf
	@echo "On other terminal window do:"
	@echo "riscv32-linux-gdb -tui ./program.elf"
	qemu_riscv32 -L $(LIBRV32) -g 1234 program.elf -S
