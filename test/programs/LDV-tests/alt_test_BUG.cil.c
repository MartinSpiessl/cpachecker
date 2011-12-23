/* Generated by CIL v. 1.3.7 */
/* print_CIL_Input is true */

#line 211 "/usr/lib64/gcc/x86_64-suse-linux/4.5/include/stddef.h"
typedef unsigned long size_t;
#line 471 "/usr/include/stdlib.h"
extern  __attribute__((__nothrow__)) void *malloc(size_t __size )  __attribute__((__malloc__)) ;
#line 488
extern  __attribute__((__nothrow__)) void free(void *__ptr ) ;
#line 69 "/usr/include/assert.h"
extern  __attribute__((__nothrow__, __noreturn__)) void __assert_fail(char const   *__assertion ,
                                                                      char const   *__file ,
                                                                      unsigned int __line ,
                                                                      char const   *__function ) ;
#line 5 "alt_test_BUG.c"
int VERDICT_UNSAFE  ;
#line 6 "alt_test_BUG.c"
int CURRENTLY_UNSAFE  ;
#line 8 "alt_test_BUG.c"
int globalState  =    0;
#line 9
void *l_malloc(int size ) ;
#line 10
void l_free(void *ptr ) ;
#line 12 "alt_test_BUG.c"
int main(int argc , char **argv ) 
{ int *a ;
  void *tmp ;

  {
  {
#line 14
  tmp = l_malloc((int )sizeof(int ));
#line 14
  a = (int *)tmp;
#line 15
  l_free((void *)a);
#line 16
  l_free((void *)a);
  }
#line 17
  return (0);
}
}
#line 20 "alt_test_BUG.c"
void *l_malloc(int size ) 
{ void *retVal ;
  void *tmp ;

  {
  {
#line 21
  tmp = malloc((unsigned long )size);
#line 21
  retVal = tmp;
  }
#line 22
  if ((unsigned long )retVal != (unsigned long )((void *)0)) {
#line 23
    globalState = 1;
  } else {

  }
#line 24
  return (retVal);
}
}
#line 27 "alt_test_BUG.c"
void l_free(void *ptr ) 
{ 

  {
#line 31
  if ((unsigned long )ptr != (unsigned long )((void *)0)) {
#line 31
    if (globalState == 1) {

    } else {
      {
#line 31
      __assert_fail("globalState == 1", "alt_test_BUG.c", 31U, "l_free");
      }
    }
  } else {

  }
  {
#line 32
  globalState = 0;
#line 33
  free(ptr);
  }
#line 34
  return;
}
}
