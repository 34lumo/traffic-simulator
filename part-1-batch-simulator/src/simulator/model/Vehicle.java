package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

public class Vehicle extends SimulatedObject {

    private int maxSpeed;
    private int currentSpeed;
    private int contaminationClass;
    private List<Junction> itinerary;
    private VehicleStatus status;
    private Road road;
    private int location;
    private int totalCO2;
    private int totalDistance;
    private int itineraryIndex;

    public Vehicle(String id, int maxSpeed, int contClass, List<Junction> itinerary) {
        super(id);
        if (maxSpeed <= 0) 
        	throw new IllegalArgumentException("Maximum speed must be positive.");
        
        if (contClass < 0 || contClass > 10)
        	throw new IllegalArgumentException("Contamination class must be between 0 and 10.");
        
        if (itinerary == null || itinerary.size() < 2) 
        	throw new IllegalArgumentException("Itinerary must have at least two junctions.");
        
        this.maxSpeed = maxSpeed;
        this.contaminationClass = contClass;
        this.itinerary = Collections.unmodifiableList(new ArrayList<>(itinerary));
        this.status = VehicleStatus.PENDING;
        this.currentSpeed = 0;
        this.totalCO2 = 0;
        this.totalDistance = 0;
        this.itineraryIndex = 0;
    }
    
    public void setSpeed(int s) {
	    if (s < 0)
	        throw new IllegalArgumentException("Speed cannot be negative.");
	    
	    if (this.status != VehicleStatus.TRAVELING) 
	        return; // No hacer nada si el vehículo no está viajando
	    
	    this.currentSpeed = Math.min(s, this.maxSpeed);
	}

    public void setContClass(int c) {
        if (c < 0 || c > 10)
        	throw new IllegalArgumentException("Contamination class must be between 0 and 10.");
        
        
        this.contaminationClass = c;
    }

    public void advance(int currTime) { 
        if (this.status == VehicleStatus.TRAVELING) 
        {
            int prevLocation = this.location;
            this.location = Math.min(this.location + this.currentSpeed, road.getLength());  
            int distanceTraveled = this.location - prevLocation;
            int contamination = distanceTraveled * this.contaminationClass; 
            this.totalCO2 += contamination;
            road.addContamination(contamination); 
            this.totalDistance += distanceTraveled;

            if (this.location >= road.getLength()) {
                this.status = VehicleStatus.WAITING;
                this.currentSpeed = 0;
                road.getDest().enter(this); 
            }
        }
    }

    	public void moveToNextRoad() {
    	    if (this.status != VehicleStatus.PENDING && this.status != VehicleStatus.WAITING) {
    	        throw new UnsupportedOperationException(
    	                "moveToNextRoad for vehicle '" + getId() + "' failed, the vehicle's status must be PENDING or WAITING."); //el + hace que se pegue el texto
    	    }
    	    
    	    //Si el vehículo está en una carretera, salir de ella
    	    if (road != null) {
    	        road.exit(this);
    	    }
    	 
    	    //Comprobar si el vehículo ha llegado al final de su itinerario
    	    if (itineraryIndex == itinerary.size() - 1) {
    	        status = VehicleStatus.ARRIVED;
    	        road = null;
    	        location = 0;
    	    } else {
    	        status = VehicleStatus.TRAVELING;
    	        road = itinerary.get(itineraryIndex).roadTo(itinerary.get(itineraryIndex + 1));
    	        location = 0;
    	        road.enter(this);
    	        itineraryIndex++;
    	    }
    	}

    public JSONObject report() {
        JSONObject jo = new JSONObject();
        jo.put("id", getId());
        jo.put("speed", currentSpeed);
        jo.put("distance", totalDistance);
        jo.put("co2", totalCO2);
        jo.put("class", contaminationClass);
        jo.put("status", status.toString());
        if (status != VehicleStatus.PENDING && status != VehicleStatus.ARRIVED) {
            jo.put("road", road.getId());
            jo.put("location", location);
        }
        return jo;
    }

    // Getters
    public int getLocation() {
        return location;
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getContClass() {
        return contaminationClass;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public int getTotalCO2() {
        return totalCO2;
    }

    public List<Junction> getItinerary() {
        return itinerary;
    }

    public Road getRoad() {
        return road;
    }
}
