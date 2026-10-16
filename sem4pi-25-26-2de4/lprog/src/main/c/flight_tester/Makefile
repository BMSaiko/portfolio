# ─────────────────────────────────────────────────────────────────────────────
# Makefile — flight_tester (US085 physical test component)
#
# Targets
# ─────────────────────────────────────────────────────────────────────────────
#   make          → compile release build → bin/flight_tester
#   make test     → compile test build   → bin/flight_tester_test, then run it
#   make clean    → remove bin/
# ─────────────────────────────────────────────────────────────────────────────

CC      = gcc
CFLAGS  = -Wall -Wextra -pedantic -std=c11
LDFLAGS = -lm

SRC     = flight_tester.c json_parser.c physics.c
BIN_DIR = bin
TARGET  = $(BIN_DIR)/flight_tester
TEST_TARGET = $(BIN_DIR)/flight_tester_test

.PHONY: all test clean

all: $(BIN_DIR) $(TARGET)

$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(TARGET): $(SRC) json_parser.h physics.h
	$(CC) $(CFLAGS) -o $@ $(SRC) $(LDFLAGS)

# ── Test build: compile with ENABLE_TESTS and run --test ─────────────────────
test: $(BIN_DIR) $(TEST_TARGET)
	$(TEST_TARGET) --test

$(TEST_TARGET): $(SRC) json_parser.h physics.h
	$(CC) $(CFLAGS) -DENABLE_TESTS -o $@ $(SRC) $(LDFLAGS)

clean:
	rm -rf $(BIN_DIR)