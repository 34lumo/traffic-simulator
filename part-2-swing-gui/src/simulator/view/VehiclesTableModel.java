package simulator.view;

import simulator.model.*;
import simulator.control.Controller;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class VehiclesTableModel extends AbstractTableModel implements TrafficSimObserver {

	private List<Vehicle> vehiculos;
	private final String[] nombresColumnas = { "ID", "Estado", "Itinerario", "Clase CO2", "Vel. Máxima", "Vel. Actual", "CO2 Total", "Distancia" };
	private Controller controller;

	public VehiclesTableModel(Controller ctrl) {
		controller = ctrl;
		vehiculos = new ArrayList<>();
		controller.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return vehiculos.size();
	}

	@Override
	public int getColumnCount() {
		return nombresColumnas.length;
	}

	@Override
	public String getColumnName(int col) {
		return nombresColumnas[col];
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		Vehicle v = vehiculos.get(fila);
		switch (columna) {
			case 0:
				return v.getId();
			case 1:
				VehicleStatus estado = v.getStatus();
				switch (estado) {
					case PENDING:
						return "Pending";
					case TRAVELING:
						return v.getRoad().getId() + ":" + v.getLocation();
					case WAITING:
						return "Waiting:" + v.getRoad().getDest().getId();
					case ARRIVED:
						return "Arrived";
					default:
						return "";
				}
			case 2:
				return v.getItinerary();
			case 3:
				return v.getContClass();
			case 4:
				return v.getMaxSpeed();
			case 5:
				return v.getSpeed();
			case 6:
				return v.getTotalCO2();
			case 7:
				return v.getTotalDistance();
			default:
				return null;
		}
	}

	//Lo usamos como metodo update //Reemplaza la lista anterior por la nueva (listaVehiculos = vehiculos)
	private void setVehicleList(List<Vehicle> nuevaLista) {
		vehiculos = nuevaLista;
		fireTableStructureChanged();
	}

	@Override
	public void onAdvance(RoadMap map, Collection<Event> events, int time) {
		setVehicleList(map.getVehicles());
	}

	@Override
	public void onEventAdded(RoadMap map, Collection<Event> events, Event e, int time) {
		setVehicleList(map.getVehicles());
	}

	@Override
	public void onReset(RoadMap map, Collection<Event> events, int time) {
		setVehicleList(map.getVehicles());
	}

	@Override
	public void onRegister(RoadMap map, Collection<Event> events, int time) {
		setVehicleList(map.getVehicles());
	}
}
