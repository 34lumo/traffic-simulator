package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class RoadsTableModel extends AbstractTableModel implements TrafficSimObserver {

	private List<Road> listaCarreteras;
	private final String[] columnas = { "ID", "Longitud", "Clima", "Vel. Max", "Vel. Act", "CO2 Total", "Límite CO2" };
	private Controller controller;

	public RoadsTableModel(Controller ctrl) {
		controller = ctrl;
		listaCarreteras = new ArrayList<>();
		controller.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return listaCarreteras.size();
	}

	@Override
	public int getColumnCount() {
		return columnas.length;
	}

	@Override
	public String getColumnName(int columna) {
		return columnas[columna];
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		Road r = listaCarreteras.get(fila);
		switch (columna) {
			case 0: return r.getId();
			case 1: return r.getLength();
			case 2: return r.getWeather();
			case 3: return r.getMaxSpeed();
			case 4: return r.getSpeedLimit();
			case 5: return r.getTotalCO2();
			case 6: return r.getContLimit();
			default: return null;
		}
	}

	private void setRoadsList(List<Road> carreteras) {
		listaCarreteras = carreteras;
		fireTableStructureChanged();
	}

	@Override
	public void onAdvance(RoadMap map, Collection<Event> events, int time) {
		setRoadsList(map.getRoads());
	}

	@Override
	public void onEventAdded(RoadMap map, Collection<Event> events, Event e, int time) {
		setRoadsList(map.getRoads());
	}

	@Override
	public void onReset(RoadMap map, Collection<Event> events, int time) {
		setRoadsList(map.getRoads());
	}

	@Override
	public void onRegister(RoadMap map, Collection<Event> events, int time) {
		setRoadsList(map.getRoads());
	}
} 
