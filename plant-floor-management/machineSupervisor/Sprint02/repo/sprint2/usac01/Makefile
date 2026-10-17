prog: main.o usac01.o
	gcc -g -Wall -o prog main.o usac01.o

main.o: main.c asm.h
	gcc -g -Wall -c main.c -o main.o

usac01.o: usac01.s
	gcc -g -Wall -Wextra -c usac01.s -o usac01.o


run: prog
	./prog

clean:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog