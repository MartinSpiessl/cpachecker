/* Generated by CIL v. 1.3.7 */
/* print_CIL_Input is true */

#line 69 "/usr/include/assert.h"
extern  __attribute__((__nothrow__, __noreturn__)) void __assert_fail(char const   *__assertion ,
                                                                      char const   *__file ,
                                                                      unsigned int __line ,
                                                                      char const   *__function ) ;
#line 12 "test_malloc-2.c"
int VERDICT_SAFE  ;
#line 13 "test_malloc-2.c"
int CURRENTLY_UNSAFE  ;
#line 16 "test_malloc-2.c"
int main(void) 
{ int a ;
  int b ;
  int *p1 ;
  int *p2 ;

  {
#line 24
  p1 = & a;
#line 25
  p2 = & b;
#line 27
  if ((unsigned long )p1 != (unsigned long )((int *)0)) {
#line 27
    if ((unsigned long )p2 != (unsigned long )((int *)0)) {
#line 28
      if ((unsigned long )p1 != (unsigned long )p2) {

      } else {
        {
#line 28
        __assert_fail("p1!=p2", "test_malloc-2.c", 28U, "main");
        }
      }
    } else {

    }
  } else {

  }
#line 30
  return (0);
}
}
