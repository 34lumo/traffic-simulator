package simulator.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.*;

public class JunctionsTableModel extends AbstractTableModel implements TrafficSimObserver {

	private List<Junction> listaCruces;
	private final String[] columnas = { "ID", "Semáforo", "Colas" };
	private Controller controller;

	public JunctionsTableModel(Controller ctrl) {
		controller = ctrl;
		listaCruces = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return listaCruces == null ? 0 : listaCruces.size();
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
		Junction junction = listaCruces.get(fila);
		switch (columna) {
			case 0:
				return junction.getId();
			case 1:
				int verde = junction.getGreenLightIndex();
				return verde == -1 ? "NONE" : junction.getIncomingRoads().get(verde).getId();
			case 2:
				StringBuilder colas = new StringBuilder();
				for (Road r : junction.getIncomingRoads()) {
					colas.append(r.getId()).append(" : ").append(r.getVehicles().toString()).append("  ");
				}
				return colas.toString();
			default:
				return null;
		}
	}

	private void setJunctionsList(List<Junction> cruces) {
		listaCruces = cruces;
		fireTableStructureChanged();
	}

	@Override
	public void onAdvance(RoadMap map, Collection<Event> events, int time) {
		setJunctionsList(map.getJunctions());
	}

	@Override
	public void onEventAdded(RoadMap map, Collection<Event> events, Event e, int time) {
		setJunctionsList(map.getJunctions());
	}

	@Override
	public void onReset(RoadMap map, Collection<Event> events, int time) {
		setJunctionsList(map.getJunctions());
	}

	@Override
	public void onRegister(RoadMap map, Collection<Event> events, int time) {
		setJunctionsList(map.getJunctions());
	}

}
