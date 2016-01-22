//Average of number inputed
import java.util.Scanner;

public class average {

    public static void main(String[] args) {
   	 Scanner scan = new Scanner(System.in);
   	 {
   		 char answer;
   		 int input = 0;
   		 int total = 0;
   		 double average = 0;
   		 int count = 0;
   		 System.out.println("Do you wish to enter a number? y or n?");
   		 answer = scan.next().charAt(0);
   		 
   		 while (answer == 'y')
   		 {
   		 System.out.println("Enter a Numerical Value");
   		 input = scan.nextInt();
   		 total = total + input;
   		 count++;
   		 average = total/count;
   		 System.out.println("You entered " + input+ " and the sum is " + total);
   		 
   		 System.out.println("Do you wish to enter another number? y or n?");
   		 answer = scan.next().charAt(0);
   		 }
   		 
   		 if (answer == 'n');
   		 {
   			 System.out.println("The total numbers you inputed are " +count+ " numbers");
   			 System.out.println("The average of all your numbers are " +average);
   			 System.out.println("The total sum is " +total);
   		 }
   		 
   		 
   		 }
   	 }

    }

