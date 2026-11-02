package simulator.factories;

import org.json.JSONObject;
import simulator.model.Event;
import simulator.model.NewCityRoadEvent;
import simulator.model.NewInterCityRoadEvent;
import simulator.model.Weather;

public abstract class NewRoadEventBuilder extends Builder<Event> {

    public NewRoadEventBuilder(String type, String desc) {
        super(type, desc);
    }

    protected Event create_road_event(JSONObject data, boolean isCityRoad) {
        int time = data.getInt("time");
        String id = data.getString("id");
        String src = data.getString("src");
        String dest = data.getString("dest");
        int length = data.getInt("length");
        int co2Limit = data.getInt("co2limit");
        int maxSpeed = data.getInt("maxspeed");
        Weather weather = Weather.valueOf(data.getString("weather").toUpperCase());

        return isCityRoad //preguntar en labortorio si lo podemos hacer asi
            ? new NewCityRoadEvent(time, id, src, dest, length, co2Limit, maxSpeed, weather) //si el booleano de isCityRoad es true, se crea un NewCity
            : new NewInterCityRoadEvent(time, id, src, dest, length, co2Limit, maxSpeed, weather); 
    }
}
