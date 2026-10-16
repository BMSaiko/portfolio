
CC=riscv32-linux-gcc
CFLAGS= -Wall -Wextra -fanalyzer -g
ARCHFLAGS= -march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot


all: usac1314_test.elf

USAC1314_OBJS = usac1314_test.o manager.o asc01.o ac03_adaptada.o ac05.o ac09.o ac04.o


usac1314_test.elf: $(USAC1314_OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(USAC1314_OBJS) -o usac1314_test.elf

usac1314_test.o: usac1314_test.c manager.h asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c usac1314_test.c


manager.o: manager.c manager.h asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager.c


asc01.o: asc01.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asc01.s


ac03_adaptada.o: ac03_adaptada.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ac03_adaptada.s

ac04.o: ac04.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ac04.s

ac05.o: ac05.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ac05.s


ac09.o: ac09.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ac09.s

run: usac1314_test.elf
	@echo "Testing USAC13 e 14."
	sudo qemu-riscv32 -L $(LIBRV32) ./usac1314_test.elf

clean:
	rm -f *.o *.elf