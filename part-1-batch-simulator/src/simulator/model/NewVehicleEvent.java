package simulator.model;
import java.util.ArrayList;
import java.util.List;

public class NewVehicleEvent extends Event {
    private String id;
    private int maxSpeed, contClass;
    private List<String> itinerary;

    public NewVehicleEvent(int time, String id, int maxSpeed, int contClass, List<String> itinerary) {
        super(time);
        this.id = id;
        this.maxSpeed = maxSpeed;
        this.contClass = contClass;
        this.itinerary = itinerary; //es una lista de identificadores (String) de los cruces que forman el recorrido del vehículo.
    }

    @Override
    void execute(RoadMap map) {
        List<Junction> junctionList = new ArrayList<>(); //Se crea una lista vacía de cruces (Junction) llamada junctionList.
        //Se recorre la lista itinerary y, para cada identificador jId, se obtiene el cruce correspondiente desde el RoadMap usando map.getJunction(jId).
        for (String jId : itinerary) 
            junctionList.add(map.getJunction(jId)); //Se van añadiendo los cruces (ya que itinerary tiene los nombres esscritos) a junctionList.
        
        Vehicle createdVehicle = new Vehicle(id, maxSpeed, contClass, junctionList); //Se crea un nuevo vehículo con los datos obtenidos y se añade al RoadMap.
        map.addVehicle(createdVehicle);
        createdVehicle.moveToNextRoad();
    }
}
