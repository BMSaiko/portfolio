# Compilador e flags
CC = gcc
GENARCH = -m64
CFLAGS = -Wall -Wextra -fanalyzer -g
ASMFLAGS = -Wall -g $(GENARCH)
LDFLAGS = -z noexecstack

# Arquivos objeto
OBJS = main.o callfunc.o dequeue_value.o unity.o

# Nome do executável
EXEC = prog

# Alvo padrão
all: $(EXEC)

# Regra para linkar o executável
$(EXEC): $(OBJS)
	$(CC) $(GENARCH) $(CFLAGS) $(OBJS) $(LDFLAGS) -o $@

# Regras para compilar arquivos .c em objetos
%.o: %.c
	$(CC) $(GENARCH) $(CFLAGS) -c $< -o $@

# Regras para compilar arquivos .s em objetos
%.o: %.s
	$(CC) $(GENARCH) -c $< -o $@

# Regra para executar o programa
run: $(EXEC)
	./$(EXEC)

# Regra para limpar arquivos objeto e executável
clean:
	rm -f $(OBJS) $(EXEC)
