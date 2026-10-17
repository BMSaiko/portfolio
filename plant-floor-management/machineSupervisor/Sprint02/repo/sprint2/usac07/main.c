#include <string.h>  
#include "unity.h"
#include "asm.h" 




int call_func ( int (*f)(int* array, int len, int* read, int *write), int* array, int len, int* read, int *write );  

void setUp(void) {

}
void tearDown(void) {

}



void run_test(int length , int read, int write , int  exp_res )
{
	int  res; 
	int vec[5]={-1,-1,-1,-1,-1};  
	vec[1]=read; 
	vec[3]=write; 
	res=call_func(get_n_element,NULL,length,&vec[1],&vec[3]); 
	TEST_ASSERT_EQUAL_INT(exp_res,res); 
	TEST_ASSERT_EQUAL_INT(read,vec[1]); 
	TEST_ASSERT_EQUAL_INT(write,vec[3]); 
	TEST_ASSERT_EQUAL_INT(-1,vec[0]); 
	TEST_ASSERT_EQUAL_INT(-1,vec[2]);
	TEST_ASSERT_EQUAL_INT(-1,vec[4]); 
} 

void test_Null()
{ 
    run_test(5,0,0,0); 
}
void test_One()
{ 
    run_test(5,0,1,1); 

}
void test_Zero()
{ 
    run_test(5,3,3,0); 

}
void test_Three()
{ 
    run_test(5,4,2,3); 
}
void test_Four()
{ 
    run_test(500,200,300,100); 
}
void test_Five()
{ 
    run_test(500,300,200,400); 
}

int main()
  { 

    UNITY_BEGIN();
    RUN_TEST(test_Null);
    RUN_TEST(test_One);
    RUN_TEST(test_Zero);
    RUN_TEST(test_Three);
    RUN_TEST(test_Four);
    RUN_TEST(test_Five);
    return UNITY_END();  

  } 






