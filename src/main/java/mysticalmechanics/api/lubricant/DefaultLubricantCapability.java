package mysticalmechanics.api.lubricant;

import java.util.Collection;
import java.util.Collections;

public class DefaultLubricantCapability implements ILubricantCapability {
    @Override
    public int lubricate(ILubricant lubricant, int amount, boolean simulate) {
        return amount;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public Collection<LubricantStack> getAppliedLubricant() {
        return Collections.emptyList();
    }
}
