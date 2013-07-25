/* problem2.c : My solution to the "Drop the 9's" problem from Crash and Compile qualifying round	
	Some things to note:
		This is a further updated version which uses functions to reduce duplication of code and make it a little
		cleaner. The latest update now removes unnecessary variables, eliminates the need for the 'mark' variable
		which was used to keep track of the start position for the rhs. Instead, since the lhs and rhs result strings
		are built separately, you can just parse through the input line in one pass using the two for loops before you
		you reset the iterator variable back to zero to go through the result strings to get the results for each side.
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

int SumSingleOrDoubleDigit(int sum, char* digitString);
void AppendToResult(char* digitString, char* result, int num, char* str);

int main(int argc, char** argv) {
   char line[41] = "";
   char result[41] = "";
   char rt_result[41] = "";
   char digitString[3] = "";
   int i = 0;
   int lhs = 0;
   int sum = 0;
   int rhs = 0;

   /* read line from the input */
   scanf("%s", line);
   while ( line[0] != '#' && line[1] != '\0' ) {

   /* parse and format the left hand side terms */
   for (i=0; line[i] != '='; i++) {
      if (line[i] == '+') {
	      AppendToResult(digitString, result, sum, "+");
          sum = 0;
      }
      if (isdigit(line[i]) && line[i] != '9') {
          digitString[0] = line[i];
          sum = SumSingleOrDoubleDigit(sum, digitString);
      }
   }

   AppendToResult(digitString, result, sum, "=");
   sum = 0;

   /* parse and format the right hand side terms */ 
   for ( ; line[i] != '\0'; i++) {
      if (isdigit(line[i]) && line[i] != '9') {
         digitString[0] = line[i];
         sum = SumSingleOrDoubleDigit(sum, digitString);
         AppendToResult(digitString, rt_result, sum, "");
         sum = 0;
         if ( line[i+1] != '\0') {
            strcat(rt_result, "+");
		 }
	  }
   }

   strcat(rt_result,"=");

      
   /* sum the lhs result string terms to get the lhs result */
   for (i=0; result[i] != '='; i++) {
      if (isdigit(result[i]) && result[i] != '9') {
         digitString[0] = result[i];
         lhs = SumSingleOrDoubleDigit(lhs, digitString);
      }
   }
   AppendToResult(digitString, result, lhs, "");   
   
   /* sum the rhs terms from the rt_result string to get the rhs result */
   for (i=0; rt_result[i] != '\0'; i++) {
      if (isdigit(rt_result[i])) {
         digitString[0] = rt_result[i];
         rhs = SumSingleOrDoubleDigit(rhs, digitString);
      }
   }

   AppendToResult(digitString, rt_result, rhs, "");

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

int SumSingleOrDoubleDigit(int sum, char* digitString)
{
    sum += atoi(digitString);
    if ( sum > 9 ) {
        sprintf(digitString, "%d", sum);
		sum = 0;
		sum = (digitString[0] - '0') + (digitString[1] - '0');
        digitString[1] = '\0';
    }
	return sum;
}

void AppendToResult(char* digitString, char* result, int num, char* str)
{
    sprintf(digitString, "%d", num);
    strcat(result, digitString);
    strcat(result, str);
    digitString[1] = '\0';
}
