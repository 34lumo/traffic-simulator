package simulator.factories;

import org.json.JSONObject;
import simulator.model.CityRoad;
import simulator.model.Event;
import simulator.model.Road;

public class NewCityRoadEventBuilder extends NewRoadEventBuilder {
    public NewCityRoadEventBuilder() {
        super("new_city_road", "Crea una carretera urbana.");
    }

    @Override
    protected Event create_instance(JSONObject data) { //Llama a la clase de newRoadevent y asi no duplicamos tnato codigo
        return create_road_event(data, true);
    }
}
