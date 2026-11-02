package simulator.factories;

import org.json.JSONObject;

public abstract class Builder<T> {
	private String _type_tag;
	private String _desc;

	public Builder(String typeTag, String desc) {
		if (typeTag == null || desc == null || typeTag.isBlank() || desc.isBlank()) //Nombre del tipo de objeto que crea este builder. (typetag) Descripción de lo que puede crear esta factoría (desc)
			throw new IllegalArgumentException("Invalid type/desc");

		_type_tag = typeTag; 
		_desc = desc;
	}

	public String get_type_tag() { //devuelve el tipo de objeto que crea este
		return _type_tag;
	}

	//e
	public JSONObject get_info() { //Devuelve un objeto JSON con la información
		JSONObject info = new JSONObject();
		info.put("type", _type_tag);
		info.put("desc", _desc);

		JSONObject data = new JSONObject(); 
		fill_in_data(data);
		info.put("data", data);
		return info;
	}

	protected void fill_in_data(JSONObject o) { //Método protegido para agregar datos adicionales en get_info(). Puede ser sobrescrito en subclases.
	}

	@Override
	public String toString() {
		return _desc;
	}

	protected abstract T create_instance(JSONObject data); //Método abstracto que las subclases deben implementar para construir objetos concretos.

}
