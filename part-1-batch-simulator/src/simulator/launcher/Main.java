package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.apache.commons.cli.*;

import simulator.control.Controller;
import simulator.factories.*;
import simulator.model.*;

public class Main {

    private final static Integer _timeLimitDefaultValue = 20;
    private static String _inFile = null;
    private static String _outFile = null;
    private static Factory<Event> _eventsFactory = null;
    private static Integer ticks = _timeLimitDefaultValue;

    private static void parseArgs(String[] args) {
        Options cmdLineOptions = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parseHelpOption(line, cmdLineOptions);
            parseInFileOption(line);
            parseOutFileOption(line);
            parseTicks(line);
            
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
        cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg().desc("Ticks to the simulator's loop (default: 10)").build());
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
        if (_inFile == null) {
            throw new ParseException("An events file is missing");
        }
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

    private static void start(String[] args) throws IOException {
        initFactories();
        parseArgs(args);
        startBatchMode();
    }

    public static void main(String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}