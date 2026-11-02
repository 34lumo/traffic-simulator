package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Junction extends SimulatedObject {
    private List<Road> incomingRoads;
    private Map<Junction, Road> outgoingRoads;
    private List<List<Vehicle>> queues;
    private Map<Road, List<Vehicle>> roadToQueueMap; //carretera cola
    private int greenLightIndex; //Representa el índice de la carretera entrante que actualmente tiene el semáforo en verde en ese cruce
    private int lastSwitchingTime;
    private LightSwitchingStrategy lsStrategy; //estrategia para cambiar de color los semáforos.
    private DequeuingStrategy dqStrategy; //una estrategia para eliminar vehı́culos de las colas.
    private int xCoor;
    private int yCoor;
   

    public Junction(String id, LightSwitchingStrategy lsStrategy, DequeuingStrategy dqStrategy, int xCoor, int yCoor) {
        super(id);
        if (lsStrategy == null || dqStrategy == null)
            throw new NullPointerException("Strategies cannot be null.");
        if (xCoor < 0 || yCoor < 0)
            throw new IllegalArgumentException("Coordinates cannot be negative.");

        this.lsStrategy = lsStrategy;
        this.dqStrategy = dqStrategy;
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        this.incomingRoads = new ArrayList<>();
        this.outgoingRoads = new HashMap<>(); 
        this.queues = new ArrayList<>();
        this.roadToQueueMap = new HashMap<>();
        this.greenLightIndex = -1; //De inicio todos los semaforos estan en rojo 
        this.lastSwitchingTime = 0;
    }
    
    
   
    public void addIncomingRoad(Road r) {
        if (r.getDest() != this)
            throw new IllegalArgumentException("El destino de la carretera entrante debe ser este cruce.");
        incomingRoads.add(r); //añade esa carretera al final de la lista de carreteras entrantes
        List<Vehicle> queue = new LinkedList<>(); //lista vacia de vehiculos
        queues.add(queue); //añadimos esa lista vacia de vehiculos, a la lista de colas
        roadToQueueMap.put(r, queue);
    }

    
    public void addOutgoingRoad(Road r) {
        if (outgoingRoads.containsKey(r.getDest()))
            throw new IllegalArgumentException("Ya existe una carretera saliente hacia el cruce destino."); //para que no haya dos carreteras que salgan del msimo cruce a otro igual (serian la misma carretera practicamente)
        if (r.getSrc() != this)
            throw new IllegalArgumentException("El origen de la carretera saliente debe ser este cruce.");
        outgoingRoads.put(r.getDest(), r);
    }

    public void enter(Vehicle v) {
        Road r = v.getRoad(); //nos da la carretera en la que esta este vehiculo
        List<Vehicle> queue = roadToQueueMap.get(r); //encuentra la cola a la carretera r, que este esperando a ese cruce.
        if (queue == null)
            throw new IllegalArgumentException("La carretera del vehículo no es una carretera entrante a este cruce."); //Si por alguna razón un vehículo intenta entrar a una carretera que no tiene una cola asociada en ese cruce 
        queue.add(v); //añade el vehiculo a la cola
    }

    public Road roadTo(Junction j) { //está diseñado para devolver la carretera específica que conecta el cruce actual (this) con otro cruce específico j que se pasa como parámetro.
        return outgoingRoads.get(j);
    }

    
    @Override
    public void advance(int currTime) {
        // Usa la estrategia de extracción de la cola para calcular la lista de vehículos que deben avanzar
        if (greenLightIndex > -1) {
            List<Vehicle> queue = queues.get(greenLightIndex); //devuelve la lista con semaforo en verde
            List<Vehicle> vehiclesToMove = dqStrategy.dequeue(queue); //dqStrategy decide la estrategia para hacer dequing, y dequeue da la lista de los vehiuclos que avanzaran en ese verde
            for (Vehicle v : vehiclesToMove) {
                v.moveToNextRoad();
                queue.remove(v);
            }
        }  

        // Usa la estrategia de cambio de semáforo para calcular el índice de la siguiente carretera a poner en verde
        int nextGreen = lsStrategy.chooseNextGreen(incomingRoads, queues, greenLightIndex, lastSwitchingTime, currTime);
        if (nextGreen != greenLightIndex) {
            greenLightIndex = nextGreen;
            lastSwitchingTime = currTime;
        }
    }

    @Override
    public JSONObject report() {
        JSONObject jo = new JSONObject();
        jo.put("id", getId());
        
        jo.put("green", (greenLightIndex == -1) ? "none" : incomingRoads.get(greenLightIndex).getId());
        
        JSONArray jsonQueues = new JSONArray();
        jo.put("queues", jsonQueues);
        
        for (Road road : incomingRoads) {
            JSONObject jsonRoad = new JSONObject();
            jsonQueues.put(jsonRoad);
            jsonRoad.put("road", road.getId());
            
            JSONArray jsonVehicles = new JSONArray();
            jsonRoad.put("vehicles", jsonVehicles);
            
            for (Vehicle v : roadToQueueMap.get(road)) {
                jsonVehicles.put(v.getId());
            }
        }
        
        return jo;
    }
}
