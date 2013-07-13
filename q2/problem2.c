/* problem2.c : My solution to the "Drop the 9's" problem from Crash and Compile qualifying round	
	Some things to note:
		There is a lot of violation of the DRY principle here. I was rushing and didn't stop to 
		refactor it. Variable names are pretty atrocious. The code style and formatting is inconsistent.
		There is absolutely no error checking (and since it was for a speed programming contest there 
		doesn't need to be frankly). Most importantly, the code is super unsafe :) and full of room for 
		buffer errors and exploits.

	Basically, after trying to understand the problem's vague instructions; it boiled down to this:
		Write a program that checks the left hand side of an addition equation against the right
		hand side by dropping any 9's found on either side, summing the individual digits in each term
		respectively, and if any sums contain a result greater than 9 (more than 2 digits), sum the
		individual digits in that result. When you're done, compare the left hand side total sum against
		the right hand side. If they are equal then the equation was valid. If not, then it wasn't. QED,
		so on so forth.
*/
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

int main(int argc, char** argv) {
   char line[40];
   char result[40];
   char rt_result[40];
   char tmpstr[2];
   char two_digits[2];
   int i = 0;
   int j = 0;
   int mark = 0;
   int lhs = 0;
   int sum = 0;
   int rhs = 0;

   /* read line from the input */
   scanf("%s", line);
   while ( line[0] != '#' && line[1] != '\0' ) {
   /* parse and format the left hand side */
      for (i=0; line[i] != '='; i++) {
         if (line[i] == '+') {
            sprintf(two_digits, "%d", sum);
            strcat(result,two_digits);
            strcat(result,"+");
            two_digits[1] = '\0';
            sum = 0;
         }
         if (isdigit(line[i]) && line[i] != '9') {
            two_digits[0] = line[i];
            sum += atoi(two_digits);
            if (sum > 9) {
               sprintf(two_digits, "%d", sum);
               sum = 0;
               for (j=0; j <2; j++) {
                  tmpstr[0] = two_digits[j];
                  sum += atoi(tmpstr);
               }
               two_digits[1] = '\0';
            }
         }
      }
      sprintf(two_digits, "%d", sum);
      strcat(result, two_digits);
      strcat(result, "=");
      two_digits[1] = '\0';
      sum = 0;
      mark = i;
      
      for (i=0; result[i] != '='; i++) {
         if (isdigit(result[i]) && result[i] != '9') {
            two_digits[0] = result[i];
            lhs += atoi(two_digits);
            if (lhs > 9) {
               sprintf(two_digits, "%d", lhs);
               lhs = 0;
               for (j=0; j<2; j++) {
                  tmpstr[0] = two_digits[j];
                  lhs += atoi(tmpstr);
               }
               two_digits[1] = '\0';
            }
         }
      }
      sprintf(two_digits, "%d", lhs);
      strcat(result, two_digits);

   /* parse and format the right hand side */ 
   for (i = mark; line[i] != '\0'; i++) {
      if (isdigit(line[i]) && line[i] != '9') {
         two_digits[0] = line[i];
         sum += atoi(two_digits);
        if (sum > 9) {
          sprintf(two_digits, "%d", sum);
          sum = 0;
          for (j=0; j<2; j++) {
             tmpstr[0] = two_digits[j];
             sum += atoi(tmpstr);
          }
          two_digits[1] = '\0';
        }
        sprintf(two_digits, "%d", sum);
        strcat(rt_result, two_digits);
        sum = 0;
        if ( line[i+1] != '\0') {
           strcat(rt_result, "+");
        }
      }
   }
   strcat(rt_result,"=");
   for (i=0; rt_result[i] != '\0'; i++) {
      if (isdigit(rt_result[i])) {
         two_digits[0] = rt_result[i];
         rhs += atoi(two_digits);
         if ( rhs > 9 ) {
            sprintf(two_digits, "%d", rhs);
            rhs = 0;
            for (j=0; j<2; j++) {
               tmpstr[0] = two_digits[j];
               rhs += atoi(tmpstr);
            }
            two_digits[1] = '\0';
         }
      }
   }
   sprintf(two_digits, "%d", rhs);
   strcat(rt_result, two_digits);

   if ( lhs == rhs ) {
      printf("TRUE: %s AND %s\n", result, rt_result);
   } else {
      printf("FALSE: %s BUT %s\n", result, rt_result);
   }

   rhs = 0;
   lhs = 0;
   result[0] = '\0';
   rt_result[0] = '\0';
   scanf("%s", line);
 }
   return 0;
}

