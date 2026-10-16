prog: main.o usac02.o
	gcc main.o usac02.o -z noexscstack -o prog

main.o: main.c asm.h
	gcc -g -Wall -Wextra -fanalyzer -c main.c -o main.o

usac02.o: usac02.s
	gcc -g -Wall -Wextra -fanalyzer -c usac02.s -o usac02.o

run:
	./prog

clean:
	rm *.o prog

debugtui:
	gdb -tui ./prog

debug:
	gdb ./prog