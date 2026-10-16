# -----------------------------------------------------------------------------
# Function: get_number
#
# Purpose:
#    This function takes a string representing a decimal integer and converts 
#    it into a numerical value. The string is processed one character at a time,
#    skipping any whitespace between digits, and the conversion stops when an 
#    invalid character or the end of the string is encountered. The resulting 
#    number (if valid) is stored at the memory location pointed to by the second
#    argument. If the string is invalid or empty, the function returns an error.
#
# Parameters:
#    %rdi (input)  - A pointer to the null-terminated string representing a 
#                    decimal integer. The string may contain spaces between 
#                    digits and should only contain digits (0-9) and spaces.
#    %rsi (input/output) - A pointer to an integer variable where the result 
#                           (converted number) will be stored.
#
# Return Value:
#    %rax - The return value indicates success or failure:
#          - 1: If the string is successfully converted to a valid number (0-31) 
#               and stored in the integer pointed to by %rsi.
#          - 0: If the input string is empty, contains invalid characters, or 
#               represents a number outside the valid range (0-31).
#
# Side Effects:
#    - Modifies the contents of the integer pointed to by %rsi, storing the 
#      converted value (if successful).
#    - If the string is invalid or empty, sets the integer at %rsi to -1 to 
#      indicate an error.
#
# Example 1: Assembly
#
# movq $string, %rdi            # Pointer to the string to convert
# leaq result, %rsi             # Pointer to the result variable
# call get_number               # Call the function
#
# Example 2: C
#
# char* str = "15";                   # Input string to convert
# int result;                               # Result variable
# int res = get_number(str, &result);       # Call the function
#
# -----------------------------------------------------------------------------

.section .text
    .global get_number          #funtion requested in the usac3, that receives a string and a pointer to an int, and converts the string into a number and stores it in the address of the pointer

get_number:
    pushq %rbx
    movq $0, %rdx               #initialize the value to be stored
    movq $0, %rax               #initialize the counter of valid characters
    movq $1, %rbx               #initialize flag
loop:
    movq $0, %rcx               #initialize the holder of the character
    movb (%rdi), %cl            #load the current character into %cl
    cmpb $0, %cl                #check for the end of the string
    je check_empty_or_invalid   #if end of string, jump to check_empty_or_invalid handler

    cmpb $' ', %cl              #if not, check for whitespace
    je skip_space_if_no_number  #if yes, jump to skip_space

    subb $'0', %cl              #convert from ASCII to integer ('0' -> 0, '1' -> 1, etc.)
    cmpb $9, %cl                #check if it's a valid digit (0-9)
    ja error                    #if not, jump to error handlre

    cmpb $0, %bl                #check flag for blank spaces between valid numbers
    je error

    imull $10, %edx             #if yes, multiply the current result by 10 (rdx = rdx * 10)
    addl %ecx, %edx               #add the current digit to the result (rdx = rdx + digit)
    incq %rdi                   #move to the next character in the string
    incq %rax                   #increment the counter
    jmp loop                    #repeat loop

skip_space_if_no_number:
    cmpq $0, %rax               #see if any number has already been processed
    je skip_space

    movb $0, %bl               #set flag to 0 to indicate that a blank space has been read after a valid number

skip_space:
    incq %rdi                   #skip space
    jmp loop                    #repeat the loop
  
error:
    movq $-1, %rdx              #store -1
    movq $0, %rax               #return 0 to indicate failure
    jmp end    

check_empty_or_invalid:
    cmpq $0, %rax
    je error                    # if we are at the end of the string and have not processed any number, it's invalid                 

success:
    movq $1, %rax               #return 1 to indicate success
    jmp end                     #jump to end

end:
    movl %edx, (%rsi)            #store the content of rdx in the address of the pointer
    popq %rbx
    ret                         #return to the caller
