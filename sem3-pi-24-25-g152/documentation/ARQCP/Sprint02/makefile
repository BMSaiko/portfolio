prog: main.o USAC01.o USAC02.o USAC03.o USAC04.o USAC05.o USAC06.o USAC07.o USAC08.o USAC09.o USAC10.o
	gcc -g -Wall -o prog main.o USAC01.o USAC02.o USAC03.o USAC04.o USAC05.o USAC06.o USAC07.o USAC08.o USAC09.o USAC10.o

main.o: src/main.c src/us.h
	gcc -g -Wall -c src/main.c -o main.o

USAC01.o: src/USAC01.s
	gcc -g -Wall -Wextra -c src/USAC01.s -o USAC01.o

USAC02.o: src/USAC02.s
	gcc -g -Wall -Wextra -c src/USAC02.s -o USAC02.o

USAC03.o: src/USAC03.s
	gcc -g -Wall -Wextra -c src/USAC03.s -o USAC03.o

USAC04.o: src/USAC04.s
	gcc -g -Wall -Wextra -c src/USAC04.s -o USAC04.o

USAC05.o: src/USAC05.s
	gcc -g -Wall -Wextra -c src/USAC05.s -o USAC05.o

USAC06.o: src/USAC06.s
	gcc -g -Wall -Wextra -c src/USAC06.s -o USAC06.o

USAC07.o: src/USAC07.s
	gcc -g -Wall -Wextra -c src/USAC07.s -o USAC07.o

USAC08.o: src/USAC08.s
	gcc -g -Wall -Wextra -c src/USAC08.s -o USAC08.o

USAC09.o: src/USAC09.s
	gcc -g -Wall -Wextra -c src/USAC09.s -o USAC09.o

USAC10.o: src/USAC10.s
	gcc -g -Wall -Wextra -c src/USAC10.s -o USAC10.o


run: prog
	./prog

clear:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog