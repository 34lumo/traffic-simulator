package simulator.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class EventsTableModel extends AbstractTableModel implements TrafficSimObserver {

	private List<Event> listaEventos;
	private String[] columnas = { "Tiempo", "Descripción" };
	private Controller controlador;

	public EventsTableModel(Controller ctrl) {
		this.controlador = ctrl;
		this.listaEventos = new ArrayList<>();
		controlador.addObserver(this);
	}

	public void actualizarTabla() {
		fireTableStructureChanged(); //Llama a fireTableDataChanged() que le dice a la JTable que redibuje la tabla, porque han cambiado los datos.
	}

	public void setEventos(Collection<Event> eventos) {
		this.listaEventos = new ArrayList<>(eventos); //Convierte la colección de eventos en una lista (porque AbstractTableModel necesita poder acceder por índice).
		actualizarTabla(); //Luego actualiza la tabla para reflejar los cambios.
	}

	@Override
	public boolean isCellEditable(int fila, int columna) {
		return false;
	}
	
	//gets
	@Override
	public String getColumnName(int columna) {
		return columnas[columna];
	}

	@Override
	public int getColumnCount() {
		return columnas.length;
	}

	@Override
	public int getRowCount() {
		return listaEventos == null ? 0 : listaEventos.size();
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		Event e = listaEventos.get(fila);
		if (columna == 0) return e.getTime(); //columna 0 para time
		else return e.toString(); //columna 1 para descripcion
	}

	@Override
	public void onAdvance(RoadMap mapa, Collection<Event> eventos, int tiempo) {
		setEventos(eventos);
	}

	@Override
	public void onEventAdded(RoadMap mapa, Collection<Event> eventos, Event e, int tiempo) {
		setEventos(eventos);
	}

	@Override
	public void onReset(RoadMap mapa, Collection<Event> eventos, int tiempo) {
		setEventos(eventos);
	}

	@Override
	public void onRegister(RoadMap mapa, Collection<Event> eventos, int tiempo) {
		setEventos(eventos);
	}
}
