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
        private List<CategoryModel> categories;
        private AdvantageModel advantage;
        private ShiftModel shift;
        private List<ItemModel> items;
        private List<DeliveryModel> dining;
        private List<OrderModel> draft_sales;
        private List<DiscountModel> discounts;
        private UserModel.Data profile;
        private List<CustomerModel> customers;
        private InvoiceSettings receipt;


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

        public List<CategoryModel> getCategories() {
            return categories;
        }

        public AdvantageModel getAdvantage() {
            return advantage;
        }

        public ShiftModel getShift() {
            return shift;
        }

        public List<ItemModel> getItems() {
            return items;
        }

        public List<DeliveryModel> getDining() {
            return dining;
        }

        public List<OrderModel> getDraft_sales() {
            return draft_sales;
        }

        public List<DiscountModel> getDiscounts() {
            return discounts;
        }

        public UserModel.Data getProfile() {
            return profile;
        }

        public List<CustomerModel> getCustomers() {
            return customers;
        }

        public InvoiceSettings getReceipt() {
            return receipt;
        }
    }
}
