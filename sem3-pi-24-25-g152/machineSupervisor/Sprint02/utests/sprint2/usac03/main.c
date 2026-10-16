#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


// int get_number(char * str, int * n ); 

int call_func ( int (*f)(char* str, int * p),  char *str , int *p);  

void setUp(void) {
    // set stuff up here
}
void tearDown(void) {
    // clean stuff up here
}



void run_test( char * input,  int exp_res, int exp_num )
{
	int  res; 
	int vec_out[3]={-1,-1,-1};  
	res=call_func(get_number,input,&vec_out[1]); 
	TEST_ASSERT_EQUAL_INT(exp_res,res); 
	if (exp_res==1) 
		TEST_ASSERT_EQUAL_INT(exp_num,vec_out[1]); 
	else 
		TEST_ASSERT_EQUAL_INT(-1,vec_out[1]); 
	TEST_ASSERT_EQUAL_INT(-1,vec_out[0]);      //sentinels 
	TEST_ASSERT_EQUAL_INT(-1,vec_out[2]); 


} 

void test_Null()
{ 
    run_test("",0,0); 
}
void test_One()
{ 
    run_test("1",1,1);  

}
void test_Zero()
{ 
    run_test(" 0   ",1,0);  
}
void test_Three()
{ 
    run_test(" 333    ",1,333);  
}
void test_Four()
{ 
    run_test(" 4444",1,4444);  
}
void test_Five()
{ 
    run_test(" 55555          ",1,55555);  
}
void test_MinusOne()
{ 
    run_test(" -1",0,0);  
}
void test_SixtyFour()
{ 
    run_test(" shdhsdh %444 sdjshd 64" ,0,0);  
}
void test_Forty()
{ 
    run_test("40  adsads",0,0);  
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
    RUN_TEST(test_MinusOne);
    RUN_TEST(test_SixtyFour);
    RUN_TEST(test_Forty);
    return UNITY_END();  

  } 






