/* Generated by CIL v. 1.3.7 */
/* print_CIL_Input is true */

#line 12 "test_union-2.c"
union A {
   int list ;
   int l2 ;
   char *str ;
};
#line 69 "/usr/include/assert.h"
extern  __attribute__((__nothrow__, __noreturn__)) void __assert_fail(char const   *__assertion ,
                                                                      char const   *__file ,
                                                                      unsigned int __line ,
                                                                      char const   *__function ) ;
#line 7 "test_union-2.c"
int VERDICT_SAFE  ;
#line 8 "test_union-2.c"
int CURRENTLY_SAFE  ;
#line 18 "test_union-2.c"
int main(void) 
{ union A x ;

  {
#line 20
  x.list = 0;
#line 24
  if (x.list == 0) {

  } else {
    {
#line 24
    __assert_fail("x.list==0", "test_union-2.c", 24U, "main");
    }
  }
#line 26
  return (0);
}
}
