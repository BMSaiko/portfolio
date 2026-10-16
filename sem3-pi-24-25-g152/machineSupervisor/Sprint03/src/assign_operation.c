#include "../include/supervisor.h"
#include <fcntl.h>
#include <termios.h>

#define port "/dev/ttyS0"

int read_csv(const char *csv_filename, Machine *machine);
int configure_serial(const char *portname);
int send_data(int serial_fd, char *str);
int read_data(int serial_fd, char *str, size_t buffer_size, Machine *machine); // Pass as reference

int assign_op(char *file_path, Machine *machine)
{
    char str[100];
    int temp = machine->op_count;

    if (!read_csv(file_path, machine) ||
        temp != machine->op_count ||
        machine->identifier != machine->operations[machine->op_count - 1].machine_id)
        return 0;

    int serial_fd = configure_serial(port);

    if (serial_fd == -1)
    {
        close(serial_fd);
        return 0;
    }

    char bin[5];
    get_number_binary(machine->identifier, bin);

    sprintf(str, "OP,%s", bin);
    if (!send_data(serial_fd, str))
        return 0;

    strcpy(machine->status, "OP");

    get_current_time(machine->operations[machine->op_count - 1].start_time,
                     sizeof(machine->operations[machine->op_count - 1].start_time));
    /*
        if (!read_data(serial_fd, str, sizeof(str), machine))
            return 0;
    */
    close(serial_fd);
    return 0;
}

int read_csv(const char *csv_filename, Machine *machine)
{
    FILE *csv_file = fopen(csv_filename, "r");
    if (!csv_file)
    {
        perror("Error opening CSV file");
        return 0;
    }

    char line[1024];

    if (machine->operations == NULL)
    {
        machine->operations = malloc(sizeof(Operation));
        if (!machine->operations)
        {
            perror("Memory allocation failed");
            fclose(csv_file);
            return 0;
        }
    }

    machine->op_count = 0;

    while (fgets(line, sizeof(line), csv_file))
    {
        if (strlen(line) <= 1)
        {
            continue;
        }

        machine->op_count++;
        machine->operations = realloc(machine->operations, machine->op_count * sizeof(Operation));

        Operation op;
        char *token = strtok(line, ",");
        if (!token)
            continue;

        op.operation_id = atoi(token);

        token = strtok(NULL, ",");
        if (!token)
            continue;
        op.machine_id = atoi(token);

        token = strtok(NULL, ",");
        if (!token)
            continue;
        strncpy(op.operation_name, token, sizeof(op.operation_name) - 1);
        op.operation_name[sizeof(op.operation_name) - 1] = '\0';

        machine->operations[machine->op_count - 1] = op;
    }

    fclose(csv_file);
    return machine->op_count;
}

int configure_serial(const char *portname)
{
    int fd = open(portname, O_RDWR | O_NOCTTY | O_SYNC);
    if (fd == -1)
    {
        perror("Error opening serial port");
        return -1;
    }

    struct termios tty;
    if (tcgetattr(fd, &tty) != 0)
    {
        perror("Error getting terminal attributes");
        close(fd);
        return -1;
    }

    cfsetospeed(&tty, B9600); // Set baud rate to 9600
    cfsetispeed(&tty, B9600);

    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8; // 8 data bits
    tty.c_iflag &= ~IGNBRK;                     // Disable break processing
    tty.c_lflag = 0;                            // No canonical mode
    tty.c_oflag = 0;                            // No remapping, no delays
    tty.c_cc[VMIN] = 1;                         // Read blocks until 1 char is received
    tty.c_cc[VTIME] = 1;                        // 0.1 second timeout

    tty.c_iflag &= ~(IXON | IXOFF | IXANY); // Disable XON/XOFF flow control
    tty.c_cflag |= (CLOCAL | CREAD);        // Enable receiver, local mode
    tty.c_cflag &= ~(PARENB | PARODD);      // Disable parity
    tty.c_cflag &= ~CSTOPB;                 // 1 stop bit
    tty.c_cflag &= ~CRTSCTS;                // No hardware flow control

    if (tcsetattr(fd, TCSANOW, &tty) != 0)
    {
        perror("Error setting terminal attributes");
        close(fd);
        return -1;
    }

    return fd;
}

int send_data(int serial_fd, char *str)
{
    size_t len = strlen(str);

    ssize_t bytes_written = write(serial_fd, str, len);

    if (bytes_written == -1)
    {
        perror("Error writing to serial port");
        return 0;
    }

    if ((size_t)bytes_written != len)
    {
        fprintf(stderr, "Partial write to serial port. Only %zd bytes written.\n", bytes_written);
        return 0;
    }

    return 1;
}

int read_data(int serial_fd, char *str, size_t buffer_size, Machine *machine) // Pass as reference
{
    char unit_t[20], unit_h[20];

    if (buffer_size <= 1)
    {
        fprintf(stderr, "Buffer size must be greater than 1.\n");
        return 0;
    }

    ssize_t bytes_read = read(serial_fd, str, buffer_size - 1);

    if (bytes_read == -1)
    {
        perror("Error reading from serial port");
        return 0;
    }

    str[bytes_read] = '\0';

    return 1;
}
