typedef int bool;
struct module;

void *__VERIFIER_nondet_pointer(void);

static inline bool try_module_get(struct module *module);
static inline void module_put(struct module *module)
{
}
static inline void __module_get(struct module *module)
{
}
extern void module_put_and_exit(struct module *mod, long code);
int module_refcount(struct module *mod);

void ldv_check_final_state(void);

const int N = 10;

void main(void)
{
	struct module *test_module_1 = __VERIFIER_nondet_pointer();
	int i;

	if (test_module_1 != ((void *) 0)) {
		for (i = 0; i < N; i++) {
			__module_get(test_module_1);
		}
		if (module_refcount(test_module_1) != N) {
		    for (i = 0; i < N; i++) {
			    module_put(test_module_1);
		    }
		}
	}

	ldv_check_final_state();
}

