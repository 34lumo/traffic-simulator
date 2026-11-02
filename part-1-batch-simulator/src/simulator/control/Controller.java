package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.model.TrafficSimulator;
import simulator.model.Event;
import simulator.factories.Factory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public class Controller {
	private TrafficSimulator simulator;
	private Factory<Event> eventsFactory;

	public Controller(TrafficSimulator sim, Factory<Event> eventsFactory) { // recibe un simulador (nuevo), y una
																			// factoría de eventos (con una serie de
																			// eventos ya dentro de la lista).
		if (sim == null || eventsFactory == null) {
			throw new IllegalArgumentException("El simulador y la factoría de eventos no pueden ser null.");
		}
		this.simulator = sim;
		this.eventsFactory = eventsFactory;
	}

//Lee un archivo JSON que contiene eventos. ej; FileInputStream in = new FileInputStream("eventos.json") y se lo pasa -> controller.loadEvents(in);
	public void loadEvents(InputStream in) {
		if (in == null) {
		}
		JSONObject jo = new JSONObject(new JSONTokener(in)); // Lee el archivo JSON y lo convierte en un JSONObject
		if (!jo.has("events")) { // Verifica que el JSON contiene la clave "events"
			throw new IllegalArgumentException("El JSON de entrada debe contener una clave 'events'.");
		}
		JSONArray eventsArray = jo.getJSONArray("events"); // Crea un array de eventos (lo hace solo porque mira a ver
															// cuantos "events" existen dentro del json) - este array
															// esta en formato JSON ( no lo queremos asi ) lo que
															// queremos es los eventos
		for (int i = 0; i < eventsArray.length(); i++) { // itera sobre cada uno de los eventos
			JSONObject eventJSON = eventsArray.getJSONObject(i); // Por cada "event" que hay, sabe guardar cada evento
																	// en un JSONObject
			Event event = eventsFactory.create_instance(eventJSON); // ahora si que crea el evento, llamando al
																	// create_instance de concrete factory (que es la
																	// que sabe a que subclase de evento llamar)
			simulator.addEvent(event); // lo metemos al simulador vacio que teniamos.
		}
	}

	public void run(int n, OutputStream out) {
		if (n < 1)
			throw new IllegalArgumentException("El número de ticks debe ser mayor que 0.");

		if (out == null)
			throw new IllegalArgumentException("El OutputStream no puede ser null.");
		PrintWriter writer = new PrintWriter(out);
		writer.println("{");
		writer.println("  \"states\": [");


		for (int i = 0; i < n; i++) {
			simulator.advance();
			JSONObject state = simulator.report();

			writer.print(state.toString()); 
			if (i < n - 1)
				writer.println(",");
			else
				writer.println(); // Si es la ultima linea.
		}
		writer.println("]");
		writer.println("}");
		writer.flush(); // Asegura que toda la salida se escriba
	}

	// resetea el controllador
	public void reset() {
		simulator.reset();
	}
}