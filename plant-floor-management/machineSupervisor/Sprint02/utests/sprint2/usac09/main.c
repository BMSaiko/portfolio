#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


int call_func ( int (*f)(int* ptr, int num, char order),int* ptr, int num, char order);  

void setUp(void) {
    // set stuff up here
}

void tearDown(void) {
    // clean stuff up here
}



void run_test(int * vec, int in_num, char order, int exp_res, int * exp_vec)
{
    int vec1[100];
    int res; 


    // setup 
        memset(vec1, 0x55, sizeof vec1);
     
	memcpy(vec1+1,vec,in_num*sizeof(int));  //   
	res=call_func(sort_array,vec1+1,in_num,order);
    
    TEST_ASSERT_EQUAL_INT(exp_res,res);    // check result 
    TEST_ASSERT_EQUAL_INT(0x55555555, vec1[in_num+1]);    // check sentinel 
    TEST_ASSERT_EQUAL_INT(0x55555555, vec1[0]);    // check sentinel  
    if ( in_num != 0 ) 
    TEST_ASSERT_EQUAL_INT_ARRAY(exp_vec, vec1+1, in_num);    // check vec 
    
}


void test_NullVector()
{ 
    run_test((int[]){0},0,1,0,(int[]){0}); 
}
void test_One()
{ 
    run_test((int[]){1000},1,1,1,(int[]){1000}); 
}
void test_Zero()
{ 
    run_test((int[]){10,0,1},3,1,1,(int[]){0,1,10}); 
}
void test_Three()
{ 
    run_test((int[]){-1,-3,-2},3,1,1,(int[]){-3,-2,-1}); 
}
void test_Five()
{ 
    run_test((int[]){1,1,1,1,2},5,1,1,(int[]){1,1,1,1,2}); 
}

void test_NullVectorD()
{ 
    run_test((int[]){0},0,0,0,(int[]){0}); 
}
void test_OneD()
{ 
    run_test((int[]){1000},1,0,1,(int[]){1000}); 
}
void test_ZeroD()
{ 
    run_test((int[]){10,0,1},3,0,1,(int[]){10,1,0}); 
}
void test_ThreeD()
{ 
    run_test((int[]){-1,-3,-2},3,0,1,(int[]){-1,-2,-3}); 
}
void test_FiveD()
{ 
    run_test((int[]){1,1,1,1,2},5,0,1,(int[]){2,1,1,1,1}); 
}


int main()
  { 

    UNITY_BEGIN();
    RUN_TEST(test_NullVector);
    RUN_TEST(test_One);
    RUN_TEST(test_Zero);
    RUN_TEST(test_Three);
    RUN_TEST(test_Five);
    RUN_TEST(test_NullVectorD);
    RUN_TEST(test_OneD);
    RUN_TEST(test_ZeroD);
    RUN_TEST(test_ThreeD);
    RUN_TEST(test_FiveD);
    return UNITY_END();  

  } 






