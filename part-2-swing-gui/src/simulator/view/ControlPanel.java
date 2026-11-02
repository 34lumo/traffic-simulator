package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import simulator.control.Controller;
import simulator.misc.Pair;
import simulator.model.*;

public class ControlPanel extends JPanel implements TrafficSimObserver {

	private JToolBar barra;
	private JButton abrirFicheroBtn, cambiarCO2Btn, cambiarTiempoBtn, runBtn, stopBtn, salirBtn;
	private JLabel ticksLbl;
	private JSpinner ticksSpinner;
	private JFileChooser selector;
	private ChangeCO2ClassDialog dialogCO2;
	private ChangeWeatherDialog dialogTiempo;
	private Controller _ctrl;
	private RoadMap mapa;
	private int tiempo;
	private boolean parado;

	public ControlPanel(Controller c) {
		_ctrl = c;
		parado = true;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		barra = new JToolBar();
		barra.setFloatable(false);
		this.add(barra, BorderLayout.PAGE_START);

		selector = new JFileChooser();
		selector.setCurrentDirectory(new File("./resources/examples/"));
		selector.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

		actionBotonAbrir();
		actionBotonCO2();
		actionBotonTiempo();
		actionBotonRun();
		actionBotonStop();
		spinnerTicks();
		actionBotonExit();
	}

	private void actionBotonAbrir() {
		abrirFicheroBtn = new JButton();
		abrirFicheroBtn.setToolTipText("Carga los archivos de datos a la aplicación");
		abrirFicheroBtn.setIcon(new ImageIcon("./resources/icons/open.png"));

		selector = new JFileChooser();
		selector.setDialogTitle("Carga de fichero de datos");
		selector.setCurrentDirectory(new File("./resources/examples/"));
		selector.setMultiSelectionEnabled(false);
		selector.setFileFilter(new FileNameExtensionFilter("Archivos JSON", "json"));

		abrirFicheroBtn.addActionListener(e -> {
	        int seleccion = selector.showOpenDialog(this.getParent());
	        if (seleccion == JFileChooser.APPROVE_OPTION) {
	            try {
	            	InputStream in = new FileInputStream(selector.getSelectedFile());
	                _ctrl.reset();
	                _ctrl.loadEvents(in);
	            } catch (FileNotFoundException e1) {
	            	JOptionPane.showMessageDialog(this, "Error al cargar el archivo: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "Operación cancelada o error al abrir el archivo.");
	        }
	    });

	    barra.add(abrirFicheroBtn);
	}
	
void actionBotonCO2() {
		cambiarCO2Btn = new JButton(new ImageIcon("./resources/icons/co2class.png"));
		cambiarCO2Btn.setToolTipText("Cambiar clase de contaminación");
		cambiarCO2Btn.addActionListener(e -> abrirDialogCO2());
		barra.add(cambiarCO2Btn);
	}

	private void abrirDialogCO2() {
		dialogCO2 = new ChangeCO2ClassDialog((Frame) SwingUtilities.getWindowAncestor(this));
		if (dialogCO2.open(mapa) != 0) {
			List<Pair<String, Integer>> cambios = new ArrayList<>();
			cambios.add(new Pair<>(dialogCO2.getVehicle().getId(), dialogCO2.getCO2Class()));
			_ctrl.addEvent(new SetContClassEvent(tiempo + dialogCO2.getTicks(), cambios));
		}
	}

	private void actionBotonTiempo() {
		cambiarTiempoBtn = new JButton(new ImageIcon("./resources/icons/weather.png"));
		cambiarTiempoBtn.setToolTipText("Cambiar condiciones meteorológicas");
		cambiarTiempoBtn.addActionListener(e -> abrirDialogTiempo());
		barra.add(cambiarTiempoBtn);
	}

	private void abrirDialogTiempo() {
		dialogTiempo = new ChangeWeatherDialog((Frame) SwingUtilities.getWindowAncestor(this));
		if (dialogTiempo.open(mapa) != 0) {
			List<Pair<String, Weather>> cambios = new ArrayList<>();
			cambios.add(new Pair<>(dialogTiempo.getRoad().getId(), dialogTiempo.getWeather()));
			_ctrl.addEvent(new SetWeatherEvent(tiempo + dialogTiempo.getTicks(), cambios));
		}
	}

	private void actionBotonRun() {
		runBtn = new JButton(new ImageIcon("./resources/icons/run.png"));
		runBtn.setToolTipText("Iniciar simulación");
		runBtn.addActionListener(e -> {
			deshabilitarBarra(false);
			parado = false;
			run_sim((Integer) ticksSpinner.getValue());
		});
		barra.add(runBtn);
	}

	private void actionBotonStop() {
		stopBtn = new JButton(new ImageIcon("./resources/icons/stop.png"));
		stopBtn.setToolTipText("Parar simulación");
		stopBtn.addActionListener(e -> parado = true);
		barra.add(stopBtn);
	}

	private void spinnerTicks() {
		ticksLbl = new JLabel("Ticks:");
		ticksSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
		ticksSpinner.setPreferredSize(new Dimension(80, 30));
		barra.add(ticksLbl);
		barra.add(ticksSpinner);
	}

	private void actionBotonExit() {
		salirBtn = new JButton(new ImageIcon("./resources/icons/exit.png"));
		salirBtn.setToolTipText("Salir de la aplicación");
		salirBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "¿Cerrar simulador?", "Salir", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) System.exit(0);
		});
		barra.add(Box.createHorizontalGlue());
		barra.add(salirBtn);
	}

	private void run_sim(int n) {
		if (n > 0 && !parado) {
			try {
				_ctrl.run(1);
				SwingUtilities.invokeLater(() -> run_sim(n - 1));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error durante simulación", "Error de progr", JOptionPane.ERROR_MESSAGE);
				parado = true;
				deshabilitarBarra(true);
				return;
			}
		} else {
			parado = true;
			deshabilitarBarra(true);
		}
	}

	private void deshabilitarBarra(boolean estado) {
		abrirFicheroBtn.setEnabled(estado);
		cambiarCO2Btn.setEnabled(estado);
		cambiarTiempoBtn.setEnabled(estado);
		runBtn.setEnabled(estado);
		salirBtn.setEnabled(estado);
		ticksSpinner.setEnabled(estado);
	}


	@Override
	public void onAdvance(RoadMap map, Collection<Event> events, int time) {
		mapa = map;
		tiempo = time;	
	}

	@Override
	public void onEventAdded(RoadMap map, Collection<Event> events, Event e, int time) {
		mapa = map;
		tiempo = time;
	}

	@Override
	public void onReset(RoadMap map, Collection<Event> events, int time) {
		
		mapa = map;
		tiempo = time;
	}

	@Override
	public void onRegister(RoadMap map, Collection<Event> events, int time) {
		
		mapa = map;
		tiempo = time;
	}

}
