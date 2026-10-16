prog: main.o machmanager.o ui.o utils.o machineEmulator.o usac01.o usac02.o usac03.o usac04.o usac05.o usac06.o usac07.o usac08.o usac09.o usac10.o
	gcc -g -Wall -o prog main.o machmanager.o ui.o utils.o machineEmulator.o usac01.o usac02.o usac03.o usac04.o usac05.o usac06.o usac07.o usac08.o usac09.o usac10.o

main.o: src/main.c include/supervisor.h
	gcc -g -Wall -c src/main.c -o main.o

machmanager.o: src/machmanager.c include/machmanager.h
	gcc -g -Wall -c src/machmanager.c -o machmanager.o

ui.o: src/ui.c include/ui.h
	gcc -g -Wall -c src/ui.c -o ui.o


utils.o: src/utils.c include/utils.h
	gcc -g -Wall -c src/utils.c -o utils.o

machineEmulator.o: src/machineEmulator.c include/machine.h
	gcc -g -Wall -c src/machineEmulator.c -o machineEmulator.o

# Assembly files
usac01.o: Sprint2src/usac01.s
	gcc -g -Wall -Wextra -c Sprint2src/usac01.s -o usac01.o

usac02.o: Sprint2src/usac02.s
	gcc -g -Wall -Wextra -c Sprint2src/usac02.s -o usac02.o

usac03.o: Sprint2src/usac03.s
	gcc -g -Wall -Wextra -c Sprint2src/usac03.s -o usac03.o

usac04.o: Sprint2src/usac04.s
	gcc -g -Wall -Wextra -c Sprint2src/usac04.s -o usac04.o

usac05.o: Sprint2src/usac05.s
	gcc -g -Wall -Wextra -c Sprint2src/usac05.s -o usac05.o

usac06.o: Sprint2src/usac06.s
	gcc -g -Wall -Wextra -c Sprint2src/usac06.s -o usac06.o

usac07.o: Sprint2src/usac07.s
	gcc -g -Wall -Wextra -c Sprint2src/usac07.s -o usac07.o

usac08.o: Sprint2src/usac08.s
	gcc -g -Wall -Wextra -c Sprint2src/usac08.s -o usac08.o

usac09.o: Sprint2src/usac09.s
	gcc -g -Wall -Wextra -c Sprint2src/usac09.s -o usac09.o

usac10.o: Sprint2src/usac10.s
	gcc -g -Wall -Wextra -c Sprint2src/usac10.s -o usac10.o

run: prog
	./prog

clean:
	rm -f prog *.o

debug: prog
	gdb -tui ./prog