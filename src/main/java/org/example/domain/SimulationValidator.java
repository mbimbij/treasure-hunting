package org.example.domain;

import com.speedment.common.mapstream.MapStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

class SimulationValidator {

    void validate(Simulation simulation) {
        validateSimulationSize(simulation.getSize());
        validateNoDuplicatePlayerName(simulation);
        validateObjectsCoordinates(simulation);
    }

    private void validateObjectsCoordinates(Simulation simulation) {
        List<Coordinates> objectsCoordinates = this.getAllObjectsCoordinates(simulation);
        validateNoOverlappingObjects(objectsCoordinates);
        validateNoObjectOutOfBound(simulation, objectsCoordinates);
    }

    private void validateSimulationSize(Simulation.Size simulationSize) {
        if (simulationSize.width() <= 0 || simulationSize.height() <= 0) {
            String message = Simulation.INVALID_SIMULATION_SIZE_ERROR_MESSAGE_FORMAT.formatted(simulationSize.width(), simulationSize.height());
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * TODO validate with PO: an player cannot have its initial coordinates equal to one of the treasure.
     * TODO explicit what features overlap to help with debugging (mountain, treasure, and later what line in the input file)
     *
     * @param allObjectsCoordinates
     */
    private void validateNoOverlappingObjects(List<Coordinates> allObjectsCoordinates) {
        Map<Coordinates, Long> featuresCountByCoordinate = allObjectsCoordinates.stream()
                .collect(groupingBy(identity(),
                        counting()));

        Map<Coordinates, Long> overlapsCountByCoordinate = MapStream.of(featuresCountByCoordinate)
                .filterValue(count -> count > 1)
                .toMap();

        if (!overlapsCountByCoordinate.isEmpty()) {
            String overlapsString = overlapsCountByCoordinate.keySet().toString();
            String message = Simulation.OVERLAPPING_FEATURES_ERROR_MESSAGE_FORMAT.formatted(overlapsString);
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNoObjectOutOfBound(Simulation simulation, List<Coordinates> allObjectsCoordinates) {
        Map<Coordinates, Long> outOfBoundFeatureCoordinates = allObjectsCoordinates.stream()
                .filter(simulation::isOutOfBound)
                .collect(groupingBy(identity(), counting()));
        if (!outOfBoundFeatureCoordinates.isEmpty()) {
            String message = Simulation.FEATURES_COORDINATES_OUT_OF_BOUND_ERROR_MESSAGE
                    .formatted(outOfBoundFeatureCoordinates.keySet());
            throw new IllegalArgumentException(message);
        }
    }


    List<Coordinates> getAllObjectsCoordinates(Simulation simulation) {
        List<Coordinates> treasuresCoordinates = simulation.getTreasures().stream().map(Treasure::getCoordinates).toList();
        List<Coordinates> mountainsCoordinates = simulation.getMountains().stream().map(Mountain::getCoordinates).toList();
        List<Coordinates> playersCoordinates = simulation.getPlayers().stream().map(Player::getCoordinates).toList();
        List<Coordinates> allObjectsCoordinates = new ArrayList<>();
        allObjectsCoordinates.addAll(treasuresCoordinates);
        allObjectsCoordinates.addAll(mountainsCoordinates);
        allObjectsCoordinates.addAll(playersCoordinates);
        return allObjectsCoordinates;
    }

    private void validateNoDuplicatePlayerName(Simulation simulation) {
        Map<String, Long> playersCountByName = simulation.getPlayers().stream()
                .collect(groupingBy(Player::getName, counting()));

        Map<String, Long> duplicatePlayersNames = MapStream.of(playersCountByName)
                .filterValue(count -> count > 1)
                .toMap();

        if (!duplicatePlayersNames.isEmpty()) {
            String duplicatePlayersNamesString = duplicatePlayersNames.keySet().toString();
            String message = Simulation.DUPLICATE_PLAYERS_NAMES_ERROR_MESSAGE_FORMAT.formatted(duplicatePlayersNamesString);
            throw new IllegalArgumentException(message);
        }
    }
}