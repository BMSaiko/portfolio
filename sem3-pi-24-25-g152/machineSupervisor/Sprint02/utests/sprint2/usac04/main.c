#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


// int format_command(char *op, int n, char * cmd); 

int call_func ( int (*f)(char* op, int n, char * cmd),  char *op, int n, char * cmd);  

void setUp(void) {
    // set stuff up here
}
void tearDown(void) {
    // clean stuff up here
}



void run_test( char * input, int n,  int exp_res, char * exp_cmd )
{
	int  res; 
	char cmd_out[100]; 
	memset(cmd_out,'@',sizeof(cmd_out)); 
	res=call_func(format_command,input,n,&cmd_out[1]); 
	TEST_ASSERT_EQUAL_INT(exp_res,res); 
	if (exp_res==1)
	       { 		
		TEST_ASSERT_EQUAL_STRING(exp_cmd,&cmd_out[1]); 
		TEST_ASSERT_EQUAL_INT8('@',cmd_out[strlen(exp_cmd)+2]); // end of string sentinel  
	       } 
	TEST_ASSERT_EQUAL_INT8('@',cmd_out[0]);      //sentinels 


} 

void test_Null()
{ 
    run_test("",0,0,""); 
}
void test_One()
{ 
    run_test(" cmd ",23,1,"CMD,1,0,1,1,1"); 

}
void test_Zero()
{ 
    run_test(" on ",8,1,"ON,0,1,0,0,0"); 
}
void test_Three()
{ 
    run_test(" ofF ",17,1,"OFF,1,0,0,0,1"); 
}
void test_Four()
{ 
    run_test("Off",7,1,"OFF,0,0,1,1,1"); 
}
void test_Five()
{ 
    run_test("oN      ",15,1,"ON,0,1,1,1,1"); 
}
void test_MinusOne()
{ 
    run_test(" cmD ",-1,0,""); 
}
void test_SixtyFour()
{ 
    run_test(" shdhsdh %444 sdjshd",64,0,"" );  
}
void test_Forty()
{ 
    run_test("40  adsads",40,0,"");  
}
void test_Offy()
{ 
    run_test("Offy   ",0,0,"");  
}
void test_Offy2()
{ 
    run_test("Off y  2   ",0,0,"");  
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
    RUN_TEST(test_Offy);
    RUN_TEST(test_Offy2);
    return UNITY_END();  

  } 






