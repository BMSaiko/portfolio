#include <string.h>  
#include "../../unity.h"
#include "asm.h" 

// int get_number_binary(int n, char * p); 

int call_func ( int (*f)(int n ,char* p ), int n, char *p);  

void setUp(void) {
    // set stuff up here
}
void tearDown(void) {
    // clean stuff up here
}



void run_test(int num, int exp_res, char exp_out[5] )
{
	char out[7]; 
	int  res; 
	memset(out,'@',sizeof(out)); 	
	res=call_func(get_number_binary,num,&out[1]);
	TEST_ASSERT_EQUAL_INT(exp_res,res); 
   
	if (exp_res==1) 
	{ 	
		TEST_ASSERT_EQUAL_INT8('@',out[0]);       // check sentinel 
		TEST_ASSERT_EQUAL_INT8_ARRAY(exp_out,&out[1],5); 
		TEST_ASSERT_EQUAL_INT8('@',out[6]); // check sentinel  		
        }
	else 
		TEST_ASSERT_EACH_EQUAL_INT8('@',out,7); 	

} 

void test_Null()
{ 
    run_test(-1,0,(char[]){0,0,0,0,0}); 
}
void test_One()
{ 
    run_test(1,1,(char[]){1,0,0,0,0});  // "output bits"  in "reverse" order! ` 

}
void test_Zero()
{ 
    run_test(0,1,(char[]){0,0,0,0,0});  
}
void test_Three()
{ 
    run_test(3,1,(char[]){1,1,0,0,0});  
}
void test_Four()
{ 
    run_test(4,1,(char[]){0,0,1,0,0});  
}
void test_Five()
{ 
    run_test(5,1,(char[]){1,0,1,0,0});  
}
void test_MinusOne()
{ 
    run_test(-1,0,"     ");  
}
void test_SixtyFour()
{ 
    run_test(64,0,"     ");  
}
void test_Forty()
{ 
    run_test(40,0,"     ");  
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






