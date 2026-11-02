package simulator.factories;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.Event;
import simulator.model.NewVehicleEvent;

public class NewVehicleEventBuilder extends Builder<Event> {

    public NewVehicleEventBuilder() {
        super("new_vehicle", "Crea un nuevo evento de vehículo en la simulación.");
    }

    @Override
    protected void fill_in_data(JSONObject o) { //No sabemos si hace falta
        o.put("time", "Tick en el que se añadirá el vehículo");
        o.put("id", "ID del vehículo");
        o.put("maxspeed", "Velocidad máxima del vehículo");
        o.put("class", "Clase de contaminación (0-10)");
        o.put("itinerary", "Lista de identificadores de cruces que forman el itinerario");
    }

    @Override
    protected Event create_instance(JSONObject data) {
        int time = data.getInt("time");
        String id = data.getString("id");
        int maxSpeed = data.getInt("maxspeed");
        int contaminationClass = data.getInt("class");

        List<String> itinerary = new ArrayList<>();
        JSONArray itineraryArray = data.getJSONArray("itinerary");
        for (int i = 0; i < itineraryArray.length(); i++) {
            itinerary.add(itineraryArray.getString(i)); // Guardamos los IDs de los cruces
        }

        return new NewVehicleEvent(time, id, maxSpeed, contaminationClass, itinerary);
    }
}
