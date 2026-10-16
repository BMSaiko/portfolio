# -----------------------------------------------------------------------------
# Function: get_number_binary
#
# Purpose: 
#    This function converts a given integer value (between 0 and 31, inclusive)
#    into its binary representation and stores it in a provided array.
#    The binary value is stored in the array from the least significant bit 
#    to the most significant bit (little-endian format), starting at the address 
#    pointed to by the second argument. The function handles values from 0 to 31 
#    and stores them as 5 bits in total.
#
# Parameters:
#    %rdi (input)  - The integer value (between 0 and 31, inclusive) to convert 
#                    to binary.
#    %rsi (input/output) - A pointer to the memory location (array) where the 
#                           binary representation will be stored. The function
#                           stores one bit per element in the array (5 elements in total).
#
# Return Value:
#    %rax - The return value indicates success or failure:
#          - 1: If the function successfully converts the value to binary and
#               stores it in the array.
#          - 0: If the input value is not within the valid range (0-31).
#
# Side Effects:
#    - Modifies the contents of the array pointed to by %rsi, storing the binary
#      representation of the input value.
#
# Example 1: Assembly
#
# movq $15, %rdi                Value to convert (15)
# leaq binary_array, %rsi       Pointer to the destination array
# call get_number_binary        Call the function
#
# Example 2: C
#
# int value = 15;                               Value to convert (15)
# char bits[5];                                 Destination array
# int res = get_number_binary(value, bits);     Call the function
# -----------------------------------------------------------------------------

.section .text
    .global get_number_binary  #function requested in the usac2, that receives a value between 0 and 31, and a array, and stores the value in binary in the array

get_number_binary:
    
    cmpb $31, %dil              #see if is inside the range (0 < value < 31) 
    ja error                    #if not, jump to error handler

    movb $0, %cl                #initialize counter (number of bits) to 0

loop:
    cmpb $5, %cl                #compare if the counter has reached 5
    je success                  #if yes, jump to success handler
  
    testb $1, %dil              #test if the least significant bit of the value is 1       
    setne (%rsi)                #if the bit is 1, store the value 1 (0 if not) in the correct position in the array  (using %rsi)      
    incb %cl                    #increment the counter
    incq %rsi                   #move to the next byte in the array  

    shrb $1, %dil               #shift the value right by 1 to check the next bit

    jmp loop                    #repeat loop section

error:
    movq $0, %rax               #set return value to 0 (indicating error)
    jmp end                     

success:
    movq $1, %rax               #set return value to 1 (indicating success)
    jmp end               

end:
    ret                         #return to the caller
