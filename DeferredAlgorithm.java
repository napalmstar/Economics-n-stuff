
/*
 * written in java 1.8.x
 * James Barrett
 */
public class DeferredAlgorithm {
	/*
	 * Some constants used throughout the Algorithm in place of loading information from a file. A character value indicates preferences, they are ordered from left (most desireable) to right (least desireable)
	 * a value of 0 indicates a preference for remaining single over other choices. It will also make array manipulation easier in the future as all PREF_XX are the same size
	 */
		final private static char[] PROPOSER_ID = {'A','B','C','D','E'};
		final private static char[] PROPOSEE_ID  = {'W','X','Y','Z'};
		final private static int NUM_PROPOSERS = PROPOSER_ID.length;
		final private static int NUM_PROPOSEES = PROPOSEE_ID.length;
		final private static char[][] PREF_PROPOSERS = {{'W','X','Y','Z'},{'Z','Y','X','W'},{'Y','X',0,0},{'W','Z','X',0},{'Y','Z','X','W'}};
		final private static char[][] PREF_PROPOSEES = {{'A','B','C','D','E'},{'B','C','D','E',0},{'E','C','A',0,0},{'C','D','E','A','B'}};
		
		/*
		 * Nested inner static class, only included for purposes of making this sample self-contained
		 * In order to keep page count down I willfully skipped using most getters and setters. Member variables of class Person 
		 * set to private for encapsulation purposes but given the nature of nested classes the top level class has access regardless.
		 * 
		 * Set up a person class to allow for easy creation through a singly linked-list data structure.
		 * Member variables include a Person next, character ID, char array of preferences, a default (single) match character of 1, a matched boolean for exit strategy and a preference 
		 * position which has dual use through both the proposers as an end game counter and proposees for storing the preference value of current match
		 */
		public static class Person{
			private char id;
			private char[] preference;
			private Person next;
			private char match = 0;
			private int preferencePosition = 0;
			private boolean matched = false;
			
			public Person(char id, char[] preference){
				this.id = id;
				this.preference = new char[preference.length];
				for(int i = 0; i < preference.length; i++){
					this.preference[i] = preference[i];
				}
				next = null;
			}
			/*
			 * Create singly linked lists of people with a size input given by calling method with a given size
			 */
			public static Person createProposer(int size){
				if(size <= 0)return null;
				else{
					Person person = new Person(PROPOSER_ID[NUM_PROPOSERS - size], PREF_PROPOSERS[NUM_PROPOSERS - size]);
					person.next = createProposer(size - 1);
					return person;
				}
			}
			
			public static Person createProposee(int size){
				if(size <= 0)return null;
				else{
					Person person = new Person(PROPOSEE_ID[NUM_PROPOSEES - size], PREF_PROPOSEES[NUM_PROPOSEES - size]);
					person.next = createProposee(size - 1);
					return person;
				}
			}
		}
		/*
		 * end nested class
		 */
		
		/* 
		 * Creating people, singly linked
		 */
		private Person proposer = Person.createProposer(NUM_PROPOSERS);
		private Person proposee = Person.createProposee(NUM_PROPOSEES);
		/*
		 * proposer/proposee getters, again unnecessary given the nature of this program in that everything is included
		 */
		public Person getProposer(){
			return proposer;
		}
		
		public Person getProposee(){
			return proposee;
		}
				
		/*
		 *  Conversion of linked list of people into arrays for further manipulation
		 *  Java doesn't have pointers and that caused me a bit of a headache
		 */
		public static Person[] personArray (Person person, int size){
			int counter = 0;
			Person[] returnArray = new Person[size];
			while (person != null){
				System.out.println(person.id);
				returnArray[counter++] = person;
				person = person.next;
			}
			return returnArray;
		}
		/*
		 * Main algorithm
		 * The idea is that each proposer proposes to their first proposee. The proposee can choose to reject or accept each proposer and is allowed to reject a proposer accepted early
		 * on in favor of a new proposer which the proposee values higher.
		 * n x m matrix so at least one person will be single. 
		 */
		public static void playGame(Person input_proposer, Person input_proposee){
			boolean game = true;
			Person[] proposerArr = personArray(input_proposer, NUM_PROPOSERS);
			Person[] proposeeArr = personArray(input_proposee, NUM_PROPOSEES);

			while (game){
				for (int j = 0; j < proposerArr.length; j++){
					if (proposerArr[j].preferencePosition < NUM_PROPOSEES ){
						char proposal = proposerArr[j].preference[proposerArr[j].preferencePosition];
					
						for (int k = 0; k < proposeeArr.length; k++){
							if (proposal == proposeeArr[k].id){
								if (proposeeArr[k].matched){
									for (int s = 0; s < proposeeArr[k].preference.length; s++){
										if (proposerArr[j].id == proposeeArr[k].preference[s] && s < proposeeArr[k].preferencePosition){
											proposerArr[j].match = proposeeArr[k].id;
											proposerArr[j].matched = true;
										
											char temp = proposeeArr[k].match;
											proposeeArr[k].preferencePosition = s;
											proposeeArr[k].matched = true;
											proposeeArr[k].match = proposerArr[j].id;
										
											for (int x = 0; x < proposerArr.length; x++){
												if (proposerArr[x].id == temp){
													proposerArr[x].match = 0;
													proposerArr[x].matched = false;
													proposerArr[x].preferencePosition = 0;
												}
											}
										}
									}
								}else{
									for (int s = 0; s < proposeeArr[k].preference.length; s++){
										if (proposerArr[j].id == proposeeArr[k].preference[s]){
											proposerArr[j].matched = true;
											proposerArr[j].match = proposeeArr[k].id;
										
											proposeeArr[k].matched = true;
											proposeeArr[k].match = proposerArr[j].id;
											proposeeArr[k].preferencePosition = s;
										}
									}
								}
							}
						}
					}
				}
				for (int q = 0; q < proposerArr.length; q++){
					if (!proposerArr[q].matched) proposerArr[q].preferencePosition++;
				}
				/*
				 * Escape conditions for the while loop
				 * if all proposers are matched, exit
				 */
				int matchSum = 0;
				for (int t = 0; t < proposerArr.length; t++){
					if(proposerArr[t].matched) matchSum++;
				}
				if (matchSum >= NUM_PROPOSERS)game = false;
				
				/*
				 * if one or more proposers are continually unmatched, exit.
				 */
				int positionSum = 0;
				for(int g = 0; g < proposerArr.length; g++){
					positionSum += proposerArr[g].preferencePosition;
				}
				if (NUM_PROPOSERS * NUM_PROPOSEES < NUM_PROPOSERS * positionSum){
					game = false;
				}
			}
		}
		/*
		 * Print statement
		 */
		public static void print(Person person){
			while(person != null){
				if (person.matched)System.out.println("Person: " + person.id + " Matches with: " + person.match);
				else System.out.println("Person: " + person.id + " remains single");
				person  = person.next;
			}
		}

		public static void main(String[] args){
			DeferredAlgorithm daa = new DeferredAlgorithm();
			playGame(daa.getProposer(),daa.getProposee());
			System.out.println("Proposee Match");
			daa.print(daa.getProposee());
			System.out.println("Proposers Match");
			daa.print(daa.getProposer());
		}
}
