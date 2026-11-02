package simulator.model;

import java.util.Collections;
import java.util.List;


public class MoveFirstStrategy implements DequeuingStrategy {

    @Override
    public List<Vehicle> dequeue(List<Vehicle> q) {
        if (q.isEmpty()) 
            return Collections.emptyList(); // Si esta vacia pues devuelve la lista vacia
        else 
           return Collections.singletonList(q.get(0)); //devuelve una lista que incluye el primer vehı́culo de q.
        
    }
}
