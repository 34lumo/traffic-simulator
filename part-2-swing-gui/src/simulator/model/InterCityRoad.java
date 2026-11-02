package simulator.model;

public class InterCityRoad extends Road {
	public InterCityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length,
			Weather weather) {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}

	@Override
	public void reduceTotalContamination() {
		int actualContamination = switch (this.getWeather()) { //depende del weather se escribe un valor u otro
		case SUNNY -> 2;
		case CLOUDY -> 3;
		case RAINY -> 10;
		case WINDY -> 15;
		case STORM -> 20;
		};
		
		int currentCO2 = getTotalCO2();
		double reduction = ((100.0 - actualContamination) / 100.0) * currentCO2;
		totalCO2 = (int) reduction;
	}

	@Override
	public void updateSpeedLimit() {
		if (getTotalCO2() > getContLimit())
			currentSpeedLimit = (int) (getMaxSpeed() * 0.5);
		else
			currentSpeedLimit = getMaxSpeed();

	}

	@Override
	public int calculateVehicleSpeed(Vehicle v) {
		int calculatedSpeed;
		if (this.weather == Weather.STORM) {
			calculatedSpeed = (this.currentSpeedLimit * 8) / 10;
		} else {
			calculatedSpeed = this.currentSpeedLimit;
		}

		return calculatedSpeed;
	}

}
