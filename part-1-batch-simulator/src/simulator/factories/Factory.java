package simulator.factories;

import java.util.List;

import org.json.JSONObject;

public interface Factory<T> {
	public T create_instance(JSONObject info); //Crea una instancia de un objeto T a partir de un JSON. Si la información es incorrecta, lanza una excepción.
	public List<JSONObject> get_info(); //Devuelve una lista de objetos JSON que describen los objetos que la factoría puede crear.
}

