#include<stdio.h>    

int  main (void){    
int x, y;   
 
for (x = 1; x < 16; ++x)    
printf ("\n");  
  
for (x = 1; x < 16; ++x){  
  
for (y = 1; y < 16; ++y)   
 
printf ("%#3d   ", x * y);  
  
printf ("\n");    
}    

return 0;    
}    