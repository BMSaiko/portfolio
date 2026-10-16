
CC = gcc
GENARCH = -m64
CFLAGS = -Wall -Wextra -fanalyzer -g


OBJS = main.o callfunc.o unity.o get_n_element.o
EXEC = prog


all: $(EXEC)

$(EXEC): $(OBJS)
	$(CC) $(GENARCH) $(CFLAGS) $(OBJS) -z noexecstack -o $@

main.o: main.c
	$(CC) $(GENARCH) $(CFLAGS) -c $< -o $@

callfunc.o: callfunc.s
	$(CC) $(GENARCH) -c $< -o $@

unity.o: unity.c
	$(CC) $(GENARCH) -c $< -o $@

get_n_element.o: get_n_element.s
	$(CC) $(GENARCH) -c $< -o $@


run: $(EXEC)
	./$(EXEC)


clean:
	rm -f $(OBJS) $(EXEC)

