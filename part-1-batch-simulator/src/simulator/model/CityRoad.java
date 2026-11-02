
package simulator.model;

public class CityRoad extends Road {
	final int REDUCTION_WINDY_STORM = 10; // Reducción mayor en clima de tormenta
	final int REDUCTION_DEFAULT = 2; // Reducción mínima en otros climas

	public CityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length,
			Weather weather) {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}

	@Override
	public void reduceTotalContamination() {
		boolean isWindyOrStormy = this.weather == Weather.WINDY || this.weather == Weather.STORM;
		int reduction;

		if (isWindyOrStormy)
			reduction = REDUCTION_WINDY_STORM;
		else{
			reduction = REDUCTION_DEFAULT;
		}
		this.totalCO2 = Math.max(0, this.totalCO2 - reduction);
	}

	@Override
	public void updateSpeedLimit() {
		this.currentSpeedLimit = this.getMaxSpeed();
		}

	@Override
	public int calculateVehicleSpeed(Vehicle v) {
		return ((11 - v.getContClass()) * this.currentSpeedLimit / 11);
	}

}
