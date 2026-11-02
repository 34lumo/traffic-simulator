package simulator.factories;

import org.json.JSONObject;
import simulator.model.LightSwitchingStrategy;
import simulator.model.RoundRobinStrategy;

public class RoundRobinStrategyBuilder extends Builder<LightSwitchingStrategy> {

    public RoundRobinStrategyBuilder() {
        super("round_robin_lss", "Estrategia de cambio de semáforo deel tipo Round Robin");
    }

    @Override
    protected void fill_in_data(JSONObject o) {
        o.put("timeslot", "Número de ticks antes de cambiar el semáforo, estos son por defecto 1");
    }

    @Override
    protected LightSwitchingStrategy create_instance(JSONObject data) {
        int timeslot = data.optInt("timeslot", 1); //Esto lo que hace es que si se ha escrito timeslot, pondrá ese, si no, por defecto será 1.
        return new RoundRobinStrategy(timeslot);
    }
}
