package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class HomeIndexModel extends StatusResponse implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private List<UnitModel> units;
        private List<ModifierModel> modifiers;
        private List<TaxModel> taxes;

        public List<UnitModel> getUnits() {
            return units;
        }

        public List<ModifierModel> getModifiers() {
            return modifiers;
        }

        public List<TaxModel> getTaxes() {
            return taxes;
        }

        public void setUnits(List<UnitModel> units) {
            this.units = units;
        }

        public void setModifiers(List<ModifierModel> modifiers) {
            this.modifiers = modifiers;
        }

        public void setTaxes(List<TaxModel> taxes) {
            this.taxes = taxes;
        }
    }
}
