prog: main.o usac08.o
	gcc -g -Wall -o prog main.o usac08.o

main.o: main.c asm.h
	gcc -g -Wall -c main.c -o main.o

usac08.o: usac08.s
	gcc -g -Wall -Wextra -c usac08.s -o usac08.o


run: prog
	./prog

clean:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog