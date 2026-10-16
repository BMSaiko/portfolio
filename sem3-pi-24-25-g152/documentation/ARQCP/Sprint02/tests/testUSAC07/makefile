prog: test07.0 USAC01.o USAC02.o USAC03.o USAC04.o USAC05.o USAC06.o USAC07.o USAC08.o USAC09.o USAC10.o
	gcc -g -Wall -o prog test07.0 USAC01.o USAC02.o USAC03.o USAC04.o USAC05.o USAC06.o USAC07.o USAC08.o USAC09.o USAC10.o

test07.o: test07.c ..src/us.h
	gcc -g -Wall -c ..src/test07.c -o test07.o

USAC01.o: ..src/USAC01
	gcc -g -Wall -Wextra -c ..src/USAC01 -o USAC01.o

USAC02.o: ..src/USAC02
	gcc -g -Wall -Wextra -c ..src/USAC02 -o USAC02.o

USAC03.o: ..src/USAC03
	gcc -g -Wall -Wextra -c ..src/USAC03 -o USAC03.o

USAC04.o: ..src/USAC04
	gcc -g -Wall -Wextra -c ..src/USAC04 -o USAC04.o

USAC05.o: ..src/USAC05
	gcc -g -Wall -Wextra -c ..src/USAC05 -o USAC05.o

USAC06.o: ..src/USAC06
	gcc -g -Wall -Wextra -c ..src/USAC06 -o USAC06.o

USAC07.o: ..src/USAC07
	gcc -g -Wall -Wextra -c ..src/USAC07 -o USAC07.o

USAC08.o: ..src/USAC08
	gcc -g -Wall -Wextra -c ..src/USAC08 -o USAC08.o

USAC09.o: ..src/USAC09
	gcc -g -Wall -Wextra -c ..src/USAC09 -o USAC09.o

USAC10.o: ..src/USAC10
	gcc -g -Wall -Wextra -c ..src/USAC10 -o USAC10.o


run: prog
	./prog

clear:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog