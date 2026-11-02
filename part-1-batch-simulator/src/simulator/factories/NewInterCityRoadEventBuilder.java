package simulator.factories;

import org.json.JSONObject;

import simulator.model.Event;
import simulator.model.InterCityRoad;
import simulator.model.Road;

public class NewInterCityRoadEventBuilder extends NewRoadEventBuilder {
    public NewInterCityRoadEventBuilder() {
        super("new_inter_city_road", "Crea una carretera interurbana.");
    }
    
    @Override
    protected Event create_instance(JSONObject data) {
        return create_road_event(data, false);
        }
}

