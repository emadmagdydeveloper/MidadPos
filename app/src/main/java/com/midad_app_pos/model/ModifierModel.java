package com.midad_app_pos.model;

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

    public void setModifiers_data(List<Data> modifiers_data) {
        this.modifiers_data = modifiers_data;
    }

    public static class Data implements Serializable {
        private String id;
        private String title;
        private String sort;
        private String cost;
        private String modifier_id;
        private boolean isSelected;


        public Data() {
        }

        public Data(String id, String title, String sort, String cost, String modifier_id, boolean isSelected) {
            this.id = id;
            this.title = title;
            this.sort = sort;
            this.cost = cost;
            this.modifier_id = modifier_id;
            this.isSelected = isSelected;
        }

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

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }



    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
