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
    private Map<Road, List<Vehicle>> roadToQueueMap;
    private int greenLightIndex;
    private int lastSwitchingTime;
    private LightSwitchingStrategy lsStrategy;
    private DequeuingStrategy dqStrategy;
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
        this.greenLightIndex = -1;
        this.lastSwitchingTime = 0;
    }

    public void addIncomingRoad(Road r) {
        if (r.getDest() != this)
            throw new IllegalArgumentException("El destino de la carretera entrante debe ser este cruce.");
        incomingRoads.add(r);
        List<Vehicle> queue = new LinkedList<>();
        queues.add(queue);
        roadToQueueMap.put(r, queue);
    }

    public void addOutgoingRoad(Road r) {
        if (outgoingRoads.containsKey(r.getDest()))
            throw new IllegalArgumentException("Ya existe una carretera saliente hacia el cruce destino.");
        if (r.getSrc() != this)
            throw new IllegalArgumentException("El origen de la carretera saliente debe ser este cruce.");
        outgoingRoads.put(r.getDest(), r);
    }

    public void enter(Vehicle v) {
        Road r = v.getRoad();
        List<Vehicle> queue = roadToQueueMap.get(r);
        if (queue == null)
            throw new IllegalArgumentException("La carretera del vehículo no es una carretera entrante a este cruce.");
        queue.add(v);
    }

    public Road roadTo(Junction j) {
        return outgoingRoads.get(j);
    }

    @Override
    public void advance(int currTime) {
        if (greenLightIndex > -1) {
            List<Vehicle> queue = queues.get(greenLightIndex);
            List<Vehicle> vehiclesToMove = dqStrategy.dequeue(queue);
            for (Vehicle v : vehiclesToMove) {
                v.moveToNextRoad();
                queue.remove(v);
            }
        }

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

    // Getters añadidos
    public List<Road> getIncomingRoads() {
        return incomingRoads;
    }

    public Map<Junction, Road> getOutgoingRoads() {
        return outgoingRoads;
    }

    public List<List<Vehicle>> getQueues() {
        return queues;
    }

    public Map<Road, List<Vehicle>> getRoadToQueueMap() {
        return roadToQueueMap;
    }

    public int getGreenLightIndex() {
        return greenLightIndex;
    }

    public int getLastSwitchingTime() {
        return lastSwitchingTime;
    }

    public LightSwitchingStrategy getLightSwitchingStrategy() {
        return lsStrategy;
    }

    public DequeuingStrategy getDequeuingStrategy() {
        return dqStrategy;
    }

    public int getXCoor() {
        return xCoor;
    }

    public int getYCoor() {
        return yCoor;
    }

    public int getX() {
        return xCoor;
    }

    public int getY() {
        return yCoor;
    }
}
