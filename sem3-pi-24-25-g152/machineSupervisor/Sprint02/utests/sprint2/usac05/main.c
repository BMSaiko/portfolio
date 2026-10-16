#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


int call_func ( int (*f)(int* array, int len, int* rd, int* wr, int val),
                           int* array, int len, int* rd, int *wr, int val);  

void setUp(void) {
    // set stuff up here
}

void tearDown(void) {
    // clean stuff up here
}



void run_test(int * array, int len, int rd, int wr, int val, 
			  int * exp_arr, int exp_rd, int exp_wr, int exp_res)
{
    int vec[100];
	// 0 - sentinel  
	// 1 - read 
	// 2 - sentinel  
	// 3 - write 
    // 4 - sentinel 
	// 5 - buffer 
	// 5+length - sentinel 	

    int res; // result 	
    // setup 
    memset(vec, 0x55, sizeof vec);
	memcpy(vec+5,array,len*sizeof(int));  // buffer   
	vec[1]=rd; 
	vec[3]=wr; 

     res=call_func(enqueue_value,vec+5,len,vec+1,vec+3, val);
   
    TEST_ASSERT_EQUAL_INT(exp_res,res);      

    TEST_ASSERT_EQUAL_INT(0x55555555, vec[0]);    // check sentinel 
    TEST_ASSERT_EQUAL_INT(0x55555555, vec[2]);    // check sentinel  
    TEST_ASSERT_EQUAL_INT(0x55555555, vec[4]);    // check sentinel  
    TEST_ASSERT_EQUAL_INT(0x55555555, vec[len+5]);    // check sentinel  
    TEST_ASSERT_EQUAL_INT_ARRAY(exp_arr, vec+5, len);    // check buffer 
    TEST_ASSERT_EQUAL_INT(exp_rd, vec[1]);    // check read   
    TEST_ASSERT_EQUAL_INT(exp_wr, vec[3]);    // check write  
    
}

//  void run_test(int * array, int len, int rd, int wr, int val, 
// 			  int * exp_arr, int exp_rd, int exp_wr, int exp_res)


void test_One()
{ 
    run_test((int[]){0,0,0},3,0,0,5, (int[]){5,0,0},0,1,0); 
}
void test_Zero()
{ 
    run_test((int[]){0,0,0},3,0,2,5, (int[]){0,0,5},1,0,1); 
}
void test_Three()                                             
{ 
    run_test((int[]){0,0,0,0},4,3,3,5, (int[]){0,0,0,5},3,0,0); 
}
void test_Five()
{ 
    run_test((int[]){0,0,0,0},4,0,3,5, (int[]){0,0,0,5},1,0,1); 
}

int main()
  { 

    UNITY_BEGIN();
    RUN_TEST(test_One);
    RUN_TEST(test_Zero);
    RUN_TEST(test_Three);
    RUN_TEST(test_Five);
    return UNITY_END();  

  } 






