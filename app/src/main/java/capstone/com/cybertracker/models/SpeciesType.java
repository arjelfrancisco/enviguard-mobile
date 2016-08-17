package capstone.com.cybertracker.models;

import capstone.com.cybertracker.enums.SpeciesEnum;

/**
 * Created by Arjel on 7/20/2016.
 */

public class SpeciesType {

    private Long id;
    private String name;
    private SpeciesEnum species;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpeciesEnum getSpecies() {
        return species;
    }

    public void setSpecies(SpeciesEnum species) {
        this.species = species;
    }

    @Override
    public String toString() {
        return name;
    }
}
