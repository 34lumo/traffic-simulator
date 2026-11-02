package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Road extends SimulatedObject {
	// ATRIBUTOS
	// Circular por esa carretera es ir desde el cruce origen al cruce destino.
	protected Junction srcJunc; // Será el cruce de origen, protegido para acceso directo en subclases
	protected Junction destJunc; // Será el cruce destino, protegido para acceso directo en subclases
	protected int length; // Longitud de la carretera, protegido para modificación en subclases
	protected int maxSpeed; // Velocidad máxima permitida en la carretera
	protected int currentSpeedLimit; // Límite de velocidad actual, puede cambiar según la contaminación
	protected int contLimit; // Límite de contaminación antes de que se impongan restricciones
	protected Weather weather; // Condiciones meteorológicas actuales en la carretera
	protected int totalCO2; // Total de CO2 acumulado en la carretera, protegido para modificación en
							// subclases
	protected List<Vehicle> vehicles; // Lista de vehículos en la carretera, ordenada por ubicación

	// CONSTRUCTORA Y FUNCIONES PARA COSAS DE LA CARRETERA Y LOS COCHES
	// Preguntar al profesor que sifnifica todo esto de protected en los
	// constructores ********
	protected Road(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length,
			Weather weather) {
		super(id);
		if (srcJunc == null || destJunc == null || weather == null)
			throw new IllegalArgumentException("El tiempo que haga y las intersecciones no pueden ser nunca nulas.");
		if (maxSpeed <= 0 || length <= 0 || contLimit < 0)
			throw new IllegalArgumentException(
					"La velocidad y el tamaño deben ser positivas, La contaminacion debe ser mayor que 0.");

		this.srcJunc = srcJunc;
		this.destJunc = destJunc;
		this.length = length;
		this.maxSpeed = maxSpeed;
		this.currentSpeedLimit = maxSpeed; // Al principio que la velocidad current sea la maxima permitida.
		this.contLimit = contLimit;
		this.weather = weather;
		this.vehicles = new ArrayList<>();

		srcJunc.addOutgoingRoad(this);
		destJunc.addIncomingRoad(this);
	}

	void enter(Vehicle v) {

		if (v.getLocation() != 0)
			throw new IllegalArgumentException("La localizacion del vehiculo tiene que ser 0.");
		if (v.getSpeed() != 0)
			throw new IllegalArgumentException("La velocidad del vehiculo tiene que ser 0.");

		vehicles.add(v);
	}

	public void exit(Vehicle v) {
		vehicles.remove(v);
	}

	public void setWeather(Weather w) {
		if (w == null)
			throw new IllegalArgumentException("Weather cannot be null.");

		this.weather = w;
	}

	public void addContamination(int c) {
		if (c < 0)
			throw new IllegalArgumentException("Contamination cannot be negative.");

		this.totalCO2 += c;
	}

	@Override
	public void advance(int currTime) {
		reduceTotalContamination(); // Reduce la contaminación basada en el tipo específico de carretera.
		updateSpeedLimit(); // Actualiza el límite de velocidad basado en la contaminación actual.

		for (Vehicle v : vehicles) { //Este formato de bucles es igual que el tipico solo va pasando todos los vehicles uno en uno desde v hasta vehicles
			v.setSpeed(calculateVehicleSpeed(v)); // Ajusta la velocidad de cada vehículo basado en la carretera y condiciones actuales.
													 
			v.advance(currTime); // Avanza el estado de cada vehículo.
		}

		// Ordena los vehículos por su localización en orden descendente para mantener el orden correcto en la carretera.
		
		vehicles.sort((v1, v2) -> Integer.compare(v2.getLocation(), v1.getLocation()));
	}

	@Override
	public JSONObject report() {
		JSONObject jo = new JSONObject();
		jo.put("id", getId());
		jo.put("speedlimit", currentSpeedLimit);
		jo.put("weather", weather.toString());
		jo.put("co2", totalCO2);
		JSONArray vehiclesArray = new JSONArray();
		for (Vehicle v : vehicles) {
			vehiclesArray.put(v.getId());
		}
		jo.put("vehicles", vehiclesArray);
		return jo;
	}

	// FUNCIONES GETTERS ETC...
	public abstract void reduceTotalContamination();

	public abstract void updateSpeedLimit();

	public abstract int calculateVehicleSpeed(Vehicle v);

	public int getLength() {
		return length;
	}

	// getters de la clase-
	public Junction getDest() {
		return destJunc;
	}

	public Junction getSrc() {
		return srcJunc;
	}

	public Weather getWeather() {
		return weather;
	}

	public int getContLimit() {
		return contLimit;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public int getTotalCO2() {
		return totalCO2;
	}

	public int getSpeedLimit() {
		return currentSpeedLimit;
	}

	public List<Vehicle> getVehicles() {
		return Collections.unmodifiableList(vehicles);
	}

}
