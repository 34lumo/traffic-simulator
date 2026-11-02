package simulator.model;

public abstract class Event implements Comparable<Event> {

	private static long _counter = 0; // Contador global para ordenar eventos con el mismo tiempo
	protected int _time; // Momento en el que debe ejecutarse el evento
	protected long _time_stamp; // Orden de creación del evento

	Event(int time) {
		if (time < 1) {
			throw new IllegalArgumentException("El tiempo del evento debe ser mayor o igual a 1.");
		}
		this._time = time;
		this._time_stamp = _counter++;
	}

	public int getTime() {
		return _time;
	}

	@Override
	public int compareTo(Event o) {
	    if (this._time != o._time) {
	        // Integer.compare devuelve:
	        // - Un valor negativo si this._time es menor que o._time.
	        // - Cero si son iguales.
	        // - Un valor positivo si this._time es mayor que o._time.
	        return Integer.compare(this._time, o._time);
	    }

	    return Long.compare(this._time_stamp, o._time_stamp);
	}

	abstract void execute(RoadMap map);

}
