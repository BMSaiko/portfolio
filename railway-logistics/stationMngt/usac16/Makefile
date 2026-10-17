
CC=riscv32-linux-gcc
CFLAGS= -Wall -Wextra -fanalyzer -g
ARCHFLAGS= -march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot


all: main.elf

USAC16_OBJS = main.o manager.o asc01.o ac03_adaptada.o ac05.o ac09.o ac04.o


main.elf: $(USAC16_OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(USAC16_OBJS) -o main.elf
main.o: main.c manager.h asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c


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

run: main.elf
	@echo "A executar o teste da USAC13 e 14. Pode ser necessário introduzir a password 'guest'."
	@echo "Certifica-te que o Raspberry Pi Pico está ligado e a enviar dados."
	sudo qemu-riscv32 -L $(LIBRV32) ./main.elf

clean:
	rm -f *.o *.elf