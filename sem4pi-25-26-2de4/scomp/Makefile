CC     = gcc
CFLAGS = -Wall -Wextra -pedantic -std=c11 -D_POSIX_C_SOURCE=200809L -pthread
TARGET = air_sim

OBJS = simulation.o flight_process.o report.o report_thread.o

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS) -lm

simulation.o: simulation.c simulation.h shared_memory.h flight_process.h report.h report_thread.h
	$(CC) $(CFLAGS) -c simulation.c

flight_process.o: flight_process.c flight_process.h simulation.h
	$(CC) $(CFLAGS) -c flight_process.c

report.o: report.c report.h simulation.h
	$(CC) $(CFLAGS) -c report.c

report_thread.o: report_thread.c report_thread.h simulation.h
	$(CC) $(CFLAGS) -c report_thread.c

clean:
	rm -f $(OBJS) $(TARGET)