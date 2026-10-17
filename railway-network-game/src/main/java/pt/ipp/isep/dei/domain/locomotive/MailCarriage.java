package pt.ipp.isep.dei.domain.locomotive;

import java.util.List;

public class MailCarriage extends Carriage {
    private String securityLevel;
    private boolean sortingCapability;

    public MailCarriage(String id, double weight, double capacity, double purchasePrice,
                        String securityLevel, boolean sortingCapability) {
        super(id, weight, capacity, purchasePrice);
    }

    @Override
    public String getCargoType() {
        return null;
    }

    @Override
    public double getLoadingTime() {
        return 0.0;
    }

    @Override
    public List<String> getSpecialRequirements() {
        return null;
    }

    @Override
    public double getRevenuePotential() {
        return 0.0;
    }

    public String getSecurityLevel() {
        return null;
    }

    public boolean hasSortingCapability() {
        return false;
    }
}
