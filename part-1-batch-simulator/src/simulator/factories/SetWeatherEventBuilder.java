package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.Event;
import simulator.model.SetWeatherEvent;
import simulator.misc.Pair;
import simulator.model.Weather;
import java.util.ArrayList;
import java.util.List;

public class SetWeatherEventBuilder extends Builder<Event> {

    public SetWeatherEventBuilder() {
        super("set_weather", "Cambia el clima en carreteras.");
    }

    @Override
    protected void fill_in_data(JSONObject o) {
        o.put("info", "[carretera, clima]");
    }

    @Override
    protected Event create_instance(JSONObject data) {
        int time = data.getInt("time");
        List<Pair<String, Weather>> weatherChanges = new ArrayList<>();

        JSONArray infoArray = data.getJSONArray("info");
        for (int i = 0; i < infoArray.length(); i++) {
            JSONObject pair = infoArray.getJSONObject(i);
            weatherChanges.add(new Pair<>(pair.getString("road"), Weather.valueOf(pair.getString("weather").toUpperCase())));
        }

        return new SetWeatherEvent(time, weatherChanges);
    }
}