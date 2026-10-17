prog: main.o usac03.o
	gcc main.o usac03.o -z noexscstack -o prog

main.o: main.c asm.h
	gcc -g -Wall -Wextra -fanalyzer -c main.c -o main.o

usac03.o: usac03.s
	gcc -g -Wall -Wextra -fanalyzer -c usac03.s -o usac03.o

run:
	./prog

clean:
	rm *.o prog

debugtui:
	gdb -tui ./prog


debug:
	gdb ./prog