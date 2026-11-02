package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Junction;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;

public class MapByRoadComponent extends JComponent implements TrafficSimObserver {

	private int x1;
	private int x2;
	private int y;
	private int i;
	private int xCoche;

	private RoadMap _map;
	private Image _car;
	private Image _clima;
	private Image _cont;
	
	private static final int _JRADIUS = 10;
	private static final Color _BG_COLOR = Color.WHITE;
	private static final Color _JUNCTION_COLOR = Color.BLUE;
	private static final Color _JUNCTION_LABEL_COLOR = new Color(200, 100, 0);
	private static final Color _GREEN_LIGHT_COLOR = Color.GREEN;
	private static final Color _RED_LIGHT_COLOR = Color.RED;
		
	public MapByRoadComponent(Controller ctrl) {
		initGUI();
		setPreferredSize(new Dimension(300, 200));
		ctrl.addObserver(this);	
	}

	private void initGUI() {
		_car = loadImage("car.png");
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(_BG_COLOR);
		g.clearRect(0, 0, getWidth(), getHeight());

		if (_map == null || _map.getJunctions().size() == 0) {
			g.setColor(Color.red);
			g.drawString("No map by road yet!", getWidth() / 2 - 50, getHeight() / 2);
		} else {
			drawMap(g);
		}
	}

	private void drawMap(Graphics g) {
		i = 0;
		x1 = 50;
		x2 = getWidth() - 100;
		for (Road r : _map.getRoads()) {
			y = (i + 1) * 50;
			g.setColor(Color.BLACK);
			g.drawString(r.getId(), x1 - 30, y + _JRADIUS / 2);
			g.drawLine(x1, y, x2, y);

			g.setColor(_JUNCTION_COLOR);
			g.fillOval(x1 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);

			int idx = r.getDest().getGreenLightIndex();
			if (idx != -1 && r.equals(r.getDest().getIncomingRoads().get(idx))) {
				g.setColor(_GREEN_LIGHT_COLOR);
			} else {
				g.setColor(_RED_LIGHT_COLOR);
			}
			g.fillOval(x2 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);

			g.setColor(_JUNCTION_LABEL_COLOR);
			g.drawString(r.getSrc().toString(), x1, y - _JRADIUS);
			g.drawString(r.getDest().toString(), x2, y - _JRADIUS);

			switch (r.getWeather()) {
				case SUNNY:
					_clima = loadImage("sun.png");
					break;
				case CLOUDY:
					_clima = loadImage("cloud.png");
					break;
				case RAINY:
					_clima = loadImage("rain.png");
					break;
				case WINDY:
					_clima = loadImage("wind.png");
					break;
				case STORM:
					_clima = loadImage("storm.png");
					break;
			}
			g.drawImage(_clima, x2 + 15, y - _JRADIUS * 2, 32, 32, this);

			int c = (int) Math.floor(Math.min((double) r.getTotalCO2() / (1.0 + (double) r.getContLimit()), 1.0) / 0.19);
			c = Math.min(c, 6);
			_cont = loadImage("cont_" + c + ".png");
			g.drawImage(_cont, x2 + 55, y - _JRADIUS * 2, 32, 32, this);

			for (Vehicle v : r.getVehicles()) {
				xCoche = x1 + (int) ((x2 - x1) * ((double) v.getLocation() / (double) r.getLength()));
				g.setColor(_GREEN_LIGHT_COLOR);
				g.drawString(v.getId(), xCoche, y - _JRADIUS - 5);
				g.drawImage(_car, xCoche, y - _JRADIUS - 3, 16, 16, this);
			}
			i++;
		}
	}

	private Image loadImage(String img) {
		try {
			return ImageIO.read(new File("resources/icons/" + img));
		} catch (IOException e) {
			return null;
		}
	}

	public void update(RoadMap map) {
		_map = map;
		repaint();
	}
	
	@Override
	public void onAdvance(RoadMap map, Collection<Event> events, int time) {
		update(map);
	}

	@Override
	public void onEventAdded(RoadMap map, Collection<Event> events, Event e, int time) {
		update(map);
	}

	@Override
	public void onReset(RoadMap map, Collection<Event> events, int time) {
		update(map);
	}

	@Override
	public void onRegister(RoadMap map, Collection<Event> events, int time) {
		update(map);
	}

}
