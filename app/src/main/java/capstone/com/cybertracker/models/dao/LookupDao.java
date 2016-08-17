package capstone.com.cybertracker.models.dao;

import java.util.List;

import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.models.SpeciesType;
import capstone.com.cybertracker.models.ThreatType;

/**
 * Created by Arjel on 7/23/2016.
 */

public interface LookupDao {

    public List<SpeciesType> getSpeciesTypeBySpecies(SpeciesEnum speciesEnum);
    public List<ThreatType> getThreatTypes();
    public void clearSpeciesTypes();
    public void addSpeciesTypes(List<SpeciesType> speciesTypes);
    public void clearThreatTypes();
    public  void addThreatTypes(List<ThreatType> threatTypes);

}
