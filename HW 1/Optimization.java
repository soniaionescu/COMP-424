import java.io.FileWriter;
import java.io.IOException;

public class Optimization {

	public static void main (String args[]){
		try {
			simulatedAnnealing();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void hillClimbing() throws IOException {

		FileWriter writer = new FileWriter("Hill Climbing.csv");

		double startPoint;
		double stepSize;

		double xCurrent;
		double yCurrent;
		double xLeft;
		double yLeft;
		double xRight;
		double yRight;


		for(startPoint = 0; startPoint < 11; startPoint++){

			for(stepSize = 0.01; stepSize < 0.11; stepSize = stepSize + 0.01){

				xCurrent = startPoint;
				stepSize = Round(stepSize);
				int numberOfSteps = 0;



				while (true){

					numberOfSteps++;

					yCurrent = Function(xCurrent);
					yCurrent = Round(yCurrent);

					xLeft = xCurrent - stepSize;
					xLeft = Round(xLeft);
					yLeft = Function(xLeft);
					yLeft = Round(yLeft);

					xRight = xCurrent + stepSize;
					xRight = Round(xRight);
					yRight = Function(xRight);
					yRight = Round(yRight);

					if(yLeft > yCurrent && xLeft > 0){
						xCurrent = xLeft;
					}
					else if(yRight > yCurrent && xRight < 10){
						xCurrent = xRight;
					}
					else{
						System.out.println(startPoint+"     "+stepSize+"         "+numberOfSteps+"        "+xCurrent+"            "+yCurrent);

						writer.append(startPoint+","+stepSize+","+numberOfSteps+","+xCurrent+","+yCurrent+"\n");

						break;
					}
				}
			}
		}
		writer.flush();
		writer.close();

	}
	public static void simulatedAnnealing() throws IOException {
		FileWriter writer = new FileWriter("Simulated Annealing.csv");

		double startPoint;
		double stepSize;

		double xCurrent;
		double yCurrent;
		double xLeft;
		double yLeft;
		double xRight;
		double yRight;
		
		double randomNeighbor;
		double yNeighbor;

		double t = 1000000;
		double a = .30;
		double eMax = 0;
		double xMax;

		for(startPoint = 0; startPoint < 11; startPoint++){

			for(stepSize = 0.01; stepSize < 0.11; stepSize = stepSize + 0.01){

				xCurrent = startPoint;
				stepSize = Round(stepSize);
				int numberOfSteps = 0;


				while (t>0){

					numberOfSteps++;

					yCurrent = Function(xCurrent);
					yCurrent = Round(yCurrent);

					xLeft = xCurrent - stepSize;
					xLeft = Round(xLeft);
					yLeft = Function(xLeft);
					yLeft = Round(yLeft);

					xRight = xCurrent + stepSize;
					xRight = Round(xRight);
					yRight = Function(xRight);
					yRight = Round(yRight);
					
					if(Math.random() > .5) { //choose a random neighbor of X
						randomNeighbor = xLeft;
						yNeighbor = yLeft;
					}
					else{ //choose a random neighbor of x
						randomNeighbor = xRight;
						yNeighbor = yRight;
					}

					if(yNeighbor > eMax) { //save new max
						eMax = yNeighbor;
						xMax = randomNeighbor;
					}
					if(yNeighbor > yCurrent) {
						yCurrent = yNeighbor;
						xCurrent = randomNeighbor;
						numberOfSteps++;

					}
					else if(yRight < yCurrent && yLeft < yCurrent) {
						if(Math.random() <= Math.exp(-1*(yCurrent-yNeighbor)/t)) {
							yCurrent = yNeighbor;
							xCurrent = randomNeighbor;
							numberOfSteps++;

							
						}
					}
					//cool down
					t *= a;

					//print
					System.out.println(startPoint+"     "+stepSize+"         "+numberOfSteps+"        "+xCurrent+"            "+yCurrent);

					writer.append(startPoint+","+stepSize+","+numberOfSteps+","+xCurrent+","+yCurrent+"\n");

					break;

				}
				
			}

		}
		writer.flush();
		writer.close();
	}

	public static double Function(double x){
		double y = Math.sin((Math.pow(x, 2))/2)/(Math.log(x+4)/Math.log(2));
		return y;

	}

	public static double Round(double number){
		number = Math.round(number * 1000000);
		number = number/1000000;
		return number;
	}


}
