/*Name: BANDOLA, JOSEPHINE JOY L.
  Date: 09/10/25
  Class code: BIT213L OOP
*/

import java.util.Scanner;
import java.util.Random;

public class BandolaTVBattleActivity {

    enum GameResults {
        char1Win, char2Win, TIE
    }

    private static final Random random = new Random();
    private static int totalRounds = 0;

    public static int getRandomStat(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static int attack(int strength) {
        return strength * 10+ getRandomStat(1, 10);
    }

    public static int attack(double strength) {
        return (int) (strength * 10 + getRandomStat(1, 10));
    }

    public static int specialMove(int power) {
        return power + random.nextInt(5) + 1;
    }

    public static int specialMove(double power) {
        return (int) (power + random.nextInt(5) + 1);
    }

    public static void battleWithHP(String char1, String char2, int strength1, int strength2) {
        totalRounds++;
        System.out.println("\n=== Starting Battle " + totalRounds + " ===");

        int hp1 = 100;
        int hp2 = 100;
        int round = 1;

        while (hp1 > 0 && hp2 > 0) {
            System.out.println("\n--- Round " + round + " ---");
            System.out.println(char1 + " HP: " + hp1 + " | " + char2 + " HP: " + hp2);

            int power1 = attack(strength1);
            int power2 = attack(strength2);

            System.out.println(char1 + " attacks with power: " + power1);
            System.out.println(char2 + " attacks with power: " + power2);

            if (power1 > power2) {
                int damage = power1 - power2;
                hp2 -= damage;
                System.out.println(char1 + " deals " + damage + " damage to " + char2 + "!");
            } else if (power2 > power1) {
                int damage = power2 - power1;
                hp1 -= damage;
                System.out.println(char2 + " deals " + damage + " damage to " + char1 + "!");
            } else {
                System.out.println("It's a tie this round! Both use special moves!");
                hp1 -= specialMove(power2);
                hp2 -= specialMove(power1);
            }

            hp1 = Math.max(hp1, 0);
            hp2 = Math.max(hp2, 0);

            if (hp1 == 0 || hp2 == 0) {
                break;
            }

            round++;
        }

        System.out.println("\n=== Battle Result ===");
        if (hp1 > 0) {
            System.out.println("The Winner is " + char1 + "!");
        } else if (hp2 > 0) {
            System.out.println("The Winner is " + char2 + "!");
        } else {
            System.out.println("Both fighters collapsed! It's a draw!");
        }

        System.out.println("Total Battles so far: " + totalRounds);
    }


    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        String choice;

        do {
            System.out.print("\nEnter your First TV Character: ");
            String name1 = input.nextLine();
            System.out.print("Enter your Second TV Character: ");
            String name2 = input.nextLine();

            // Generate random strength (1–10 as per instructions)
            int strength1 = getRandomStat(1, 10);
            int strength2 = getRandomStat(1, 10);

            // Run the simple required version
            battleWithHP(name1, name2, strength1, strength2);

            System.out.print("\nDo you want to battle again? (yes/no): ");
            choice = input.nextLine().trim().toLowerCase();

        } while (choice.equals("yes"));

        System.out.println("\nThank you for playing!");
        input.close();
    }
}
