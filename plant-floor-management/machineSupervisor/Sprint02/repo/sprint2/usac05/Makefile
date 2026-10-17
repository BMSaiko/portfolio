prog: main.o usac05.o
	gcc -g -Wall -o prog main.o usac05.o

main.o: main.c asm.h
	gcc -g -Wall -c main.c -o main.o

usac05.o: usac05.s
	gcc -g -Wall -Wextra -c usac05.s -o usac05.o

run: prog
	./prog

clean:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog
