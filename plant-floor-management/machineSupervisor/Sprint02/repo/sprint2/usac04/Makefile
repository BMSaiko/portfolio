prog: main.o usac02.o usac04.o
	gcc -g -Wall -o prog main.o usac02.o usac04.o

main.o: main.c asm.h
	gcc -g -Wall -c main.c -o main.o

usac02.o: usac02.s
	gcc -g -Wall -Wextra -c usac02.s -o usac02.o

usac04.o: usac04.s
	gcc -g -Wall -Wextra -c usac04.s -o usac04.o

run: prog
	./prog

clean:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog
