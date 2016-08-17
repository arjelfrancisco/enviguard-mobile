package capstone.com.cybertracker.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arjel on 7/18/2016.
 */

public enum SpeciesEnum {

    BIRD("Birds"),
    MAMMAL("Mammals"),
    REPTILES("Reptiles"),
    FLORA("Flora");

    SpeciesEnum(String label) {
        this.label = label;
    }

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static List<SpeciesEnum> getDirectObsSpecies() {
        List<SpeciesEnum> species = new ArrayList<>();
        species.add(BIRD);
        species.add(MAMMAL);
        species.add(REPTILES);
        species.add(FLORA);
        return species;
    }

    public static List<SpeciesEnum> getIndirectObsSpecies() {
        List<SpeciesEnum> species = new ArrayList<>();
        species.add(BIRD);
        species.add(MAMMAL);
        species.add(REPTILES);
        return species;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
