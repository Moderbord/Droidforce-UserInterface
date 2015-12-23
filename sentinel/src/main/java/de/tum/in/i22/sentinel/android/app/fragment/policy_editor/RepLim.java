package de.tum.in.i22.sentinel.android.app.fragment.policy_editor;

import java.util.ArrayList;

/**
 * Created by laurentmeyer on 23/12/15.
 */
public class RepLim extends EventMatchConditionContainer{

    String amount, unit;
    int lowerLimit, upperLimit;

    private String amountKey = "amount";
    private String unitKey = "unit";
    private String lowerLimitKey = "lowerLimit";
    private String upperLimitKey = "upperLimit";

    RepLim(){
        super();
        elementXMLName = "within";
    }

    @Override
    String createAttributeString() {
        CustomStringBuilder sb = new CustomStringBuilder();
        sb.append(Utils.createAttributeString(amountKey, amount))
                .append(Utils.createAttributeString(unitKey, unit))
                .append(Utils.createAttributeString(lowerLimitKey, String.valueOf(lowerLimit)))
                .append(Utils.createAttributeString(upperLimitKey, String.valueOf(upperLimit)));
        return sb.toString();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }
}
