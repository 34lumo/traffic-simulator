package simulator.model;

import java.util.List;

public class RoundRobinStrategy implements LightSwitchingStrategy {
	private int timeSlot;

	public RoundRobinStrategy(int timeSlot) {
		this.timeSlot = timeSlot;
	}

	@Override
	public int chooseNextGreen(List<Road> roads, List<List<Vehicle>> qs, int currGreen, int lastSwitchingTime, int currTime) {
		if (roads.isEmpty())
			return -1;

		else if (currGreen == -1)
			return 0; // Si todos están en rojo, enciende el primero.

		else if (currTime - lastSwitchingTime < timeSlot) // 3
			return currGreen; // Mantén el semáforo actual si aún no ha pasado el timeSlot.

		else
			return (currGreen + 1) % roads.size(); // Cambia al siguiente semáforo en orden circular.
	}
}
