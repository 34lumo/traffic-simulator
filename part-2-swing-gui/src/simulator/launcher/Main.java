package simulator.launcher;

import java.io.*;
import java.util.*;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.*;

import simulator.control.Controller;
import simulator.factories.*;
import simulator.model.*;
import simulator.view.MainWindow;

public class Main {

    private final static Integer _timeLimitDefaultValue = 20;
    private static String _inFile = null;
    private static String _outFile = null;
    private static String _mode = "gui";
    private static Factory<Event> _eventsFactory = null;
    private static Integer ticks = _timeLimitDefaultValue;
	private static boolean gui = true;


    private static void parseArgs(String[] args) {
        Options cmdLineOptions = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parseHelpOption(line, cmdLineOptions);
            parseInFileOption(line);
            parseOutFileOption(line);
            parseTicks(line);
            parseModeOption(line);

            String[] remaining = line.getArgs();
            if (remaining.length > 0) {
                throw new ParseException("Illegal arguments: " + String.join(" ", remaining));
            }
        } catch (ParseException e) {
            System.err.println("Error: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static Options buildOptions() {
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Events input file").build());
        cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where reports are written.").build());
        cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message").build());
        cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg().desc("Ticks to the simulator's loop (default: 20)").build());
        cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution mode: gui or console (default: gui)").build());
        return cmdLineOptions;
    }

    private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("h")) {
            new HelpFormatter().printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
            System.exit(0);
        }
    }

    private static void parseInFileOption(CommandLine line) throws ParseException {
        _inFile = line.getOptionValue("i");
    }

    private static void parseOutFileOption(CommandLine line) {
        _outFile = line.getOptionValue("o");
    }

    private static void parseTicks(CommandLine line) {
        if (line.hasOption("t")) {
            try {
                ticks = Integer.parseInt(line.getOptionValue("t"));
            } catch (NumberFormatException e) {
                System.err.println("Error: The value for -t must be an integer.");
                System.exit(1);
            }
        }
    }

    private static void parseModeOption(CommandLine line) {
        if (line.hasOption("m")) {
            _mode = line.getOptionValue("m");
            if (!_mode.equals("gui") && !_mode.equals("console")) {
                System.err.println("Error: invalid mode '" + _mode + "'. Valid modes are 'gui' and 'console'.");
                System.exit(1);
            }
        } else {
            _mode = "console"; // <-- CAMBIA el valor por defecto aquí
        }
    }

    private static void initFactories() {
        List<Builder<LightSwitchingStrategy>> lsbs = new ArrayList<>();
        lsbs.add(new RoundRobinStrategyBuilder());
        lsbs.add(new MostCrowdedStrategyBuilder());
        Factory<LightSwitchingStrategy> lssFactory = new BuilderBasedFactory<>(lsbs);

        List<Builder<DequeuingStrategy>> dqbs = new ArrayList<>();
        dqbs.add(new MoveFirstStrategyBuilder());
        dqbs.add(new MoveAllStrategyBuilder());
        Factory<DequeuingStrategy> dqsFactory = new BuilderBasedFactory<>(dqbs);

        List<Builder<Event>> ebs = new ArrayList<>();
        ebs.add(new NewJunctionEventBuilder(lssFactory, dqsFactory));
        ebs.add(new NewCityRoadEventBuilder());
        ebs.add(new NewInterCityRoadEventBuilder());
        ebs.add(new NewVehicleEventBuilder());
        ebs.add(new SetWeatherEventBuilder());
        ebs.add(new SetContClassEventBuilder());

        _eventsFactory = new BuilderBasedFactory<>(ebs);
    }

    private static void startBatchMode() throws IOException {
        try (InputStream in = new FileInputStream(new File(_inFile));
             OutputStream out = (_outFile == null) ? System.out : new FileOutputStream(new File(_outFile))) {
            TrafficSimulator sim = new TrafficSimulator();
            Controller controller = new Controller(sim, _eventsFactory);
            controller.loadEvents(in);
            controller.run(ticks, out);
        }
    }

    private static void startGUIMode() throws IOException {
        TrafficSimulator sim = new TrafficSimulator();
        Controller ctrl = new Controller(sim, _eventsFactory);

        if (_inFile != null) {
            try (InputStream in = new FileInputStream(new File(_inFile))) {
                ctrl.loadEvents(in);
            }
        }

        SwingUtilities.invokeLater(() -> new MainWindow(ctrl));
    }

    private static void start(String[] args) throws IOException {
        initFactories();
        parseArgs(args);
        if (_mode.equals("console")) {
            if (_inFile == null) {
                System.err.println("Error: input file is required in console mode.");
                System.exit(1);
            }
            startBatchMode();
        } else {
            startGUIMode();
        }
    }

    public static void main(String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
