#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


int call_func ( int (*f)(char* str,char* tok, char* unit, int* value),char* str,char *tok, char* unit, int* value);  

void setUp(void) {
    // set stuff up here
}
void tearDown(void) {
    // clean stuff up here
}



void run_test(char * str, char* tok, int exp_res, char * exp_unit, int exp_value)
{
	int vec[3]={0x55555555,0x55555555,0x55555555}; 
	char unit[100]; 
	int  res;
	memset(unit,'@',sizeof(unit));
	res=call_func(extract_data,str,tok,&unit[1],&vec[1]);
	TEST_ASSERT_EQUAL_INT(exp_res,res); 
    	TEST_ASSERT_EQUAL_INT(0x55555555, vec[2]);    // check sentinel 
	TEST_ASSERT_EQUAL_INT(0x55555555, vec[0]);    // check sentinel  
	TEST_ASSERT_EQUAL_INT(exp_value, vec[1]);    // check value   
   	
	TEST_ASSERT_EQUAL_INT8('@',unit[0]);       // check sentinel 
	TEST_ASSERT_EQUAL_STRING(exp_unit,&unit[1]); 
        TEST_ASSERT_EQUAL_INT8('@',unit[strlen(exp_unit)+2]); // check sentinel -> sentinel at [0] + '\0' + strlen !!!  		
}


void test_Null()
{ 
    run_test("","",0,"",0); 
}
void test_One()
{ 
    run_test("TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80","TEMP",1,"celsius",20); 
}
void test_Zero()
{ 
    run_test("TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80","HUM",1,"percentage",80); 
}
void test_Three()
{ 
    run_test("TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80","LEN",0,"",0); 
}
void test_Four()
{ 
    run_test("TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80","EMP",0,"",0); 
}
void test_Five()
{ 
    run_test("TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80","UM",0,"",0); 
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






