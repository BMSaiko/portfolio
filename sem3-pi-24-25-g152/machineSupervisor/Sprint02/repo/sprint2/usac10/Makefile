prog: main.o usac10.o usac09.o
	gcc -g -Wall -o prog main.o usac10.o usac09.o

main.o: main.c asm.h
	gcc -g -Wall -c main.c -o main.o

usac09.o: usac09.s
	gcc -g -Wall -Wextra -c usac09.s -o usac09.o

usac10.o: usac10.s
	gcc -g -Wall -Wextra -c usac10.s -o usac10.o

run: prog
	./prog

clean:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog
