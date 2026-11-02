package simulator.model;

import java.util.PriorityQueue;
import java.util.Queue;
import org.json.JSONObject;

public class TrafficSimulator {
    private RoadMap map; // Mapa de carreteras con cruces, carreteras y vehículos
    private Queue<Event> eventQueue; // Cola de eventos ordenados por tiempo
    private int time; // Tiempo actual de la simulación

    public TrafficSimulator() {
        this.map = new RoadMap(); // Inicializa el mapa de carreteras
        this.eventQueue = new PriorityQueue<>(); // Cola de eventos con prioridad por tiempo
        this.time = 0; // Inicializa el tiempo de la simulación
    }

    public void addEvent(Event e) {
        eventQueue.add(e); // Añade el evento a la cola (se ordena automáticamente por tiempo, gracias a compareTo)
    }


    /**
     * Avanza la simulación un tick:
     * 1. Incrementa el tiempo en 1.
     * 2. Ejecuta los eventos programados para el tiempo actual.
     * 3. Llama a advance() en todos los cruces.
     * 4. Llama a advance() en todas las carreteras.
     */
    
    public void advance() {
        time++; // Avanza el tiempo de la simulación en 1 unidad
        // Ejecutar eventos programados para este tiempo
        while (!eventQueue.isEmpty() && eventQueue.peek().getTime() == time) {
            eventQueue.poll().execute(map); // Saca el evento y lo ejecuta en el RoadMap
        }
        // Llamar a advance() en cada cruce
        for (Junction j : map.getJunctions()) 
            j.advance(time);
        
        // Llamar a advance() en cada carretera
        for (Road r : map.getRoads()) 
            r.advance(time);
        
    }

    public void reset() {
        map.reset();    // Borra cruces, carreteras y vehículos
        eventQueue.clear(); // Borra todos los eventos pendientes
        time = 0;           // Reinicia el tiempo a 0
    }

    public JSONObject report() {
        JSONObject jo = new JSONObject();
        jo.put("time", time);          // Guarda el tiempo actual
        jo.put("state", map.report()); // Guarda el estado del mapa de carreteras
        return jo;
    }

}
