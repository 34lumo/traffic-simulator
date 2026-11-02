package simulator.model;

import java.util.List;

public class MoveAllStrategy implements DequeuingStrategy {
    @Override
    public List<Vehicle> dequeue(List<Vehicle> q) {
        // Devuelve una copia de la cola completa.
        return List.copyOf(q);
    }
}
