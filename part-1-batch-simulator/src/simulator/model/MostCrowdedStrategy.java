package simulator.model;

import java.util.List;

public class MostCrowdedStrategy implements LightSwitchingStrategy {
	private int timeSlot;

	public MostCrowdedStrategy(int timeSlot) {
		this.timeSlot = timeSlot;
	}

	@Override
	public int chooseNextGreen(List<Road> roads, List<List<Vehicle>> queues, int currentGreen, int lastSwitchingTime,
			int currentTime) {
		if (roads.isEmpty()) // si la lita de carreteras esta vacia pues le pones un -1
			return -1;

		else if (currentGreen == -1) { // -1 es que estan rojos
			int maxQueueSize = 0;
			int selectedIndex = 0;

			for (int i = 0; i < queues.size(); i++) { // buscas la cola mas larga para ponerla en verde
				if (maxQueueSize < queues.get(i).size()) {
					maxQueueSize = queues.get(i).size();
					selectedIndex = i;
				}
			}

			return selectedIndex;
		}

		else if (currentTime - lastSwitchingTime < timeSlot) // lo dejas tal cual
			return currentGreen;

		else {
			int maxQueueSize = 0;
			int selectedIndex = 0;
			int searchIndex = (currentGreen + 1) % queues.size();

			for (int i = 0; i < queues.size(); i++) { //busqueda circular de qs empezando por la pos currgreen +1
				if (maxQueueSize < queues.get(searchIndex).size()) {
					maxQueueSize = queues.get(searchIndex).size();
					selectedIndex = searchIndex;
				}
				searchIndex++;
				if (searchIndex == queues.size()) {
					searchIndex = 0;
				}
			}
			return selectedIndex;
		}
	}

}
