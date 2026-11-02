package simulator.view;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import simulator.model.*;

public class ChangeWeatherDialog extends JDialog {

	private JPanel panelPrincipal;
	private JLabel descripcion;
	private JLabel etiquetaCarretera;
	private JComboBox<Road> selectorCarretera;
	private JLabel etiquetaTiempo;
	private JComboBox<Weather> selectorTiempo;
	private JLabel etiquetaTicks;
	private JSpinner spinnerTicks;

	private JPanel panelCampos;
	private JPanel panelBotones;
	private JButton botonAceptar;
	private JButton botonCancelar;

	private int resultado = 0;
	private DefaultComboBoxModel<Road> modeloCarreteras;
	private DefaultComboBoxModel<Weather> modeloTiempo;

	public ChangeWeatherDialog(Frame padre) {
		super(padre, true);
		initGUI();
	}

	private void initGUI() {
		setTitle("Cambiar el tiempo de una carretera");

		panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
		setContentPane(panelPrincipal);

		descripcion = new JLabel("<html>Programar un cambio de clima en una carretera tras un número de ticks.</html>");
		descripcion.setAlignmentX(CENTER_ALIGNMENT);
		panelPrincipal.add(descripcion);
		panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

		panelCampos = new JPanel();
		panelCampos.setAlignmentX(CENTER_ALIGNMENT);
		panelPrincipal.add(panelCampos);

		etiquetaCarretera = new JLabel("Carretera: ");
		modeloCarreteras = new DefaultComboBoxModel<>();
		selectorCarretera = new JComboBox<>(modeloCarreteras);
		panelCampos.add(etiquetaCarretera);
		panelCampos.add(selectorCarretera);

		etiquetaTiempo = new JLabel("Tiempo: ");
		modeloTiempo = new DefaultComboBoxModel<>();
		selectorTiempo = new JComboBox<>(modeloTiempo);
		panelCampos.add(etiquetaTiempo);
		panelCampos.add(selectorTiempo);

		etiquetaTicks = new JLabel("Ticks: ");
		spinnerTicks = new JSpinner(new SpinnerNumberModel(10, 1, 300, 1));
		spinnerTicks.setPreferredSize(new Dimension(80, 30));
		panelCampos.add(etiquetaTicks);
		panelCampos.add(spinnerTicks);

		panelBotones = new JPanel();
		panelBotones.setAlignmentX(CENTER_ALIGNMENT);
		panelPrincipal.add(panelBotones);

		botonCancelar = new JButton("Cancelar");
		botonCancelar.addActionListener((ActionEvent e) -> {
			resultado = 0;
			setVisible(false);
		});
		panelBotones.add(botonCancelar);

		botonAceptar = new JButton("Aceptar");
		botonAceptar.addActionListener((ActionEvent e) -> {
			if (selectorCarretera.getSelectedItem() != null && selectorTiempo.getSelectedItem() != null) {
				resultado = 1;
				setVisible(false);
			}
		});
		panelBotones.add(botonAceptar);

		setPreferredSize(new Dimension(500, 200));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public int open(RoadMap mapa) {
		modeloCarreteras.removeAllElements();
		for (Road r : mapa.getRoads()) {
			modeloCarreteras.addElement(r);
		}
		modeloTiempo.removeAllElements();
		for (Weather w : Weather.values()) {
			modeloTiempo.addElement(w);
		}
		setLocation(getParent().getLocation().x + 10, getParent().getLocation().y + 10);
		setVisible(true);
		return resultado;
	}

	public int getTicks() {
		return (Integer) spinnerTicks.getValue();
	}

	public Weather getWeather() {
		return (Weather) selectorTiempo.getSelectedItem();
	}

	public Road getRoad() {
		return (Road) selectorCarretera.getSelectedItem();
	}
}
