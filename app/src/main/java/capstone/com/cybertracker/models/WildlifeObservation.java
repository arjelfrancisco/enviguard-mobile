package capstone.com.cybertracker.models;

import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;

/**
 * Created by Arjel on 7/17/2016.
 */

public class WildlifeObservation extends Observation {

    private WildlifeObservationTypeEnum wildlifeObservationType;
    private SpeciesEnum species;
    private String speciesType;

    public WildlifeObservationTypeEnum getWildlifeObservationType() {
        return wildlifeObservationType;
    }

    public void setWildlifeObservationType(WildlifeObservationTypeEnum wildlifeObservationType) {
        this.wildlifeObservationType = wildlifeObservationType;
    }

    public SpeciesEnum getSpecies() {
        return species;
    }

    public void setSpecies(SpeciesEnum species) {
        this.species = species;
    }

    public String getSpeciesType() {
        return speciesType;
    }

    public void setSpeciesType(String speciesType) {
        this.speciesType = speciesType;
    }
}


