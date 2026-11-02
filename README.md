# Traffic Simulator

A small event-driven traffic simulator written in Java: junctions with traffic lights, roads, vehicles with routes and CO2 classes, and a Swing window to watch it run.

## What it is

Assignment for Programming Technology 2 (Tecnología de la Programación 2) at UCM, second year, spring 2025. It came in two parts, so the repo has two folders. Part 1 is the simulation engine plus a command-line launcher that reads a JSON file of events and writes a JSON report of every tick. Part 2 is the same engine (copied forward, not shared) with an observer layer added on top and a Swing GUI: tables of events, vehicles, roads and junctions, two map views, a control panel and a status bar.

Each part is a standalone Eclipse-style project with its own `src/`, `tests/`, `lib/` and `resources/`.

## How it works

- A `TrafficSimulator` holds a `RoadMap` (junctions, roads, vehicles) and a queue of `Event`s sorted by time. Each tick it fires the events due, advances every junction and road, then builds a JSON snapshot of the state.
- Events come from JSON through a `BuilderBasedFactory`: one `Builder` per event type (new junction, city road, intercity road, vehicle, set weather, set CO2 class). The same factory pattern picks the junction strategies.
- Junctions combine a light-switching strategy (`RoundRobin` or `MostCrowded`) with a dequeuing strategy (`MoveFirst` or `MoveAll`).
- Roads come in two flavours, `CityRoad` and `InterCityRoad`, with different rules for how weather and pollution change the speed limit.
- Vehicles move along a route of junctions, emit CO2 based on their class and speed, and queue at junctions until the light is green.
- Part 2 makes the simulator `Observable`; every Swing component implements `TrafficSimObserver` and repaints itself on each event (register, reset, event added, advance).
- `resources/examples/` has input files with their expected output; `extra.testing.TestExamples` runs them all and diffs the results. The JUnit tests in `tests/` cover the model, the factories and the launcher.

## Stack

Java 21 (any recent JDK should do), Swing for the GUI, Apache Commons CLI 1.4 and org.json (both in `lib/`), JUnit 5 for the tests.

## Run it

No JDK was available on the machine where this cleanup was done, so these commands were not executed here. They follow the source layout and the classpath the project used in Eclipse.

Part 1, batch mode (ticks default to 20):

```
cd part-1-batch-simulator
mkdir -p bin
javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" simulator.launcher.Main -i resources/examples/ex1.json -o out.json -t 10
```

Part 2, GUI (the launcher runs in console mode unless you pass `-m gui`):

```
cd part-2-swing-gui
mkdir -p bin
javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" simulator.launcher.Main -m gui -i resources/examples/ex1.json
```

`-h` prints the options. Run from inside the part folder: icons and example files are loaded by relative path (`resources/...`). On Windows replace `:` with `;` in the classpath.

To run the examples checker: `java -cp "bin:lib/*" extra.testing.TestExamples`. The JUnit tests need `junit-platform-console-standalone` on the classpath or an IDE; they write their output to `resources/tmp/`.

## Project layout

```
part-1-batch-simulator/   engine + command-line launcher
  src/simulator/          control, factories, launcher, misc, model
  src/extra/              json usage example, examples checker
  tests/                  JUnit 5 tests
  lib/                    commons-cli-1.4.jar, json.jar
  resources/examples/     inputs with expected outputs (db.json lists the ticks)
part-2-swing-gui/         same engine, observer layer, Swing view
  src/simulator/view/     MainWindow, ControlPanel, table models, map components, dialogs
  src/extra/              Swing samples from class (JTable, JDialog)
  resources/icons/        PNGs used by the GUI
```

## Team

- Luis Molina Salvador (34lumo)
- David Arranz (Tuligambas, davidarranz)
