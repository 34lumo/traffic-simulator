package simulator.factories;

import org.json.JSONObject;
import java.util.*;

public class BuilderBasedFactory<T> implements Factory<T> {
    private Map<String, Builder<T>> _builders; // Mapa que asocia un tipo con su Builder
    private List<JSONObject> _buildersInfo; // Lista con información de los Builders

    public BuilderBasedFactory() {
        _builders = new HashMap<>();
        _buildersInfo = new LinkedList<>();
    }

    public BuilderBasedFactory(List<Builder<T>> builders) {
        this(); // Llama al constructor vacío para inicializar los atributos

        for (Builder<T> b : builders) {
            add_builder(b);
        }
    }

    public void add_builder(Builder<T> b) {
        _builders.put(b.get_type_tag(), b); // Asocia el tipo del Builder con su instancia
        _buildersInfo.add(b.get_info()); // Almacena la información de qué puede crear este Builder
    }

    @Override
    public T create_instance(JSONObject info) { //preguntar a eugenio lo de las excepciones
        if (info == null) {
            throw new IllegalArgumentException("‘info’ cannot be null");
        }

        String type = info.getString("type");
        Builder<T> builder = _builders.get(type);

        if (builder != null) {
            return builder.create_instance(info.has("data") ? info.getJSONObject("data") : new JSONObject());
        }

        throw new IllegalArgumentException("Unrecognized ‘info’: " + info.toString());
    }

    @Override
    public List<JSONObject> get_info() {
        return Collections.unmodifiableList(_buildersInfo);
    }
}
