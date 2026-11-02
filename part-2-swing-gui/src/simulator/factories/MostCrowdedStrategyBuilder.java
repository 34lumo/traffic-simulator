package simulator.factories;

import org.json.JSONObject;
import simulator.model.LightSwitchingStrategy;
import simulator.model.MostCrowdedStrategy;

public class MostCrowdedStrategyBuilder extends Builder<LightSwitchingStrategy> {

    public MostCrowdedStrategyBuilder() {
        super("most_crowded_lss", "Estrategia que prioriza la carretera con más vehículos");
    }

    @Override
    protected void fill_in_data(JSONObject o) {
        o.put("timeslot", "Número de ticks antes de cambiar el semáforo (opcional, por defecto 1)");
    }

    @Override
    protected LightSwitchingStrategy create_instance(JSONObject data) {
        int timeslot = data.optInt("timeslot", 1);
        return new MostCrowdedStrategy(timeslot);
    }
}
