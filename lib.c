int printf(const char *format, ...);
int scanf(const char *format, ...);

void puti(int n) { printf("%d", n); }
void puts(char* s) { printf("%s", s); }

int geti() {
    int n;
    scanf("%d", &n);
    return n;
}

int strlen(char* s) {
    int len = 0;
    while(s[len] != '\0') len++;
    return len;
}
