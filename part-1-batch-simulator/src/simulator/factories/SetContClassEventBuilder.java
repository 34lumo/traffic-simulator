package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.Event;
import simulator.model.SetContClassEvent;
import simulator.misc.Pair;
import java.util.ArrayList;
import java.util.List;

public class SetContClassEventBuilder extends Builder<Event> {

    public SetContClassEventBuilder() {
        super("set_cont_class", "Cambia la clase de contaminación de vehículos.");
    }

    @Override
    protected void fill_in_data(JSONObject o) {
        o.put("info", "[vehículo, new contamination class]");
    }

    @Override
    protected Event create_instance(JSONObject data) {
        int time = data.getInt("time");
        List<Pair<String, Integer>> contaminationChanges = new ArrayList<>();

        JSONArray infoArray = data.getJSONArray("info");
        for (int i = 0; i < infoArray.length(); i++) {
            JSONObject pair = infoArray.getJSONObject(i);
            contaminationChanges.add(new Pair<>(pair.getString("vehicle"), pair.getInt("class")));
        }

        return new SetContClassEvent(time, contaminationChanges);
    }
}
