package simulator.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class RoadMap {
	//Se usan cuando necesitas recorrer todos los cruces en orden de inserción, útil cuando quieres iterar sobre ellos para generar informes o aplicar cambios globales.
    private List<Junction> junctions;
    private List<Road> roads;
    private List<Vehicle> vehicles;
    //Se usa para acceder rápidamente a un cruce específico sin tener que recorrer toda la lista, mejorando la eficiencia en operaciones que requieren búsquedas por ID.
    private Map<String, Junction> junctionMap; //mapa donde la clave es el ID del cruce, y el valor es el propio cruce
    private Map<String, Road> roadMap;
    private Map<String, Vehicle> vehicleMap;

    public RoadMap() {
        this.junctions = new ArrayList<>(); //Inicializamos un array de cruces
        this.roads = new ArrayList<>();//Inicializamos un array de roads
        this.vehicles = new ArrayList<>();//Inicializamos un array de vehiculos
        this.junctionMap = new HashMap<>(); //
        this.roadMap = new HashMap<>();
        this.vehicleMap = new HashMap<>();
    }
//
    public void addJunction(Junction j) {
        if (junctionMap.containsKey(j.getId())) //el constainkey busca si hay algun id co el mismonombre exacto y lanza la excepcion
            throw new IllegalArgumentException("Un cruce con el mismo identificador ya existe.");
        junctions.add(j);
        junctionMap.put(j.getId(), j);
    }

    public void addRoad(Road r) {
        if (roadMap.containsKey(r.getId()))
            throw new IllegalArgumentException("Una carretera con el mismo identificador ya existe.");
      //Hay que asegurarse de que ambos cruces ya existen en junctionMap, es decir, que han sido añadidos previamente al RoadMap.
        if (!junctionMap.containsKey(r.getSrc().getId()) || !junctionMap.containsKey(r.getDest().getId())) 
            throw new IllegalArgumentException("Los cruces que conecta la carretera deben existir en el mapa.");
        roads.add(r);
        roadMap.put(r.getId(), r);
    }

    public void addVehicle(Vehicle v) 
    {
    	
        if (vehicleMap.containsKey(v.getId()))
            throw new IllegalArgumentException("Un vehículo con el mismo identificador ya existe.");
        
        if (!isValidItinerary(v.getItinerary()))
            throw new IllegalArgumentException("El itinerario del vehículo no es válido.");
        
        vehicles.add(v);
        vehicleMap.put(v.getId(), v);
    }

    private boolean isValidItinerary(List<Junction> itinerary) // para añadir un vehiculo, el itinerario debe ser valido
    { 
        for (int i = 0; i < itinerary.size() - 1; i++) {
            Junction src = itinerary.get(i);
            Junction dest = itinerary.get(i + 1);
            if (src.roadTo(dest) == null)
                return false;
        }
        return true;
    }

    public Junction getJunction(String id) {
        return junctionMap.get(id);
    }

    public Road getRoad(String id) {
        return roadMap.get(id);
    }

    public Vehicle getVehicle(String id) {
        return vehicleMap.get(id);
    }

    public List<Junction> getJunctions() {
        return Collections.unmodifiableList(junctions); //devuelve una vista no modificable de la lista de crucesn (para que no se pueda modificar desde fuera)
    }

    public List<Road> getRoads() {
        return Collections.unmodifiableList(roads);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void reset() {
        junctions.clear();
        roads.clear();
        vehicles.clear();
        junctionMap.clear();
        roadMap.clear();
        vehicleMap.clear();
    }

    public JSONObject report() {
        JSONObject map = new JSONObject();
        map.put("junctions", new JSONArray(getJunctions().stream().map(Junction::report).collect(Collectors.toList())));
        map.put("roads", new JSONArray(getRoads().stream().map(Road::report).collect(Collectors.toList())));
        map.put("vehicles", new JSONArray(getVehicles().stream().map(Vehicle::report).collect(Collectors.toList())));
        return map;
    }
}
