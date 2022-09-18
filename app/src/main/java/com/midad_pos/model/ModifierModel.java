package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class ModifierModel implements Serializable {
    private String id;
    private String title;
    private List<Data> modifiers_data;
    private boolean isChecked = false;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Data> getModifiers_data() {
        return modifiers_data;
    }

    public static class Data implements Serializable {
        private String id;
        private String title;
        private String sort;
        private String cost;
        private String modifier_id;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getSort() {
            return sort;
        }

        public String getCost() {
            return cost;
        }

        public String getModifier_id() {
            return modifier_id;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
