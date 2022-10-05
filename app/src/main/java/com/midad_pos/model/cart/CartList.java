package com.midad_pos.model.cart;

import android.util.Log;

import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ModifierModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CartList implements Serializable {
    private double net_total;
    private double total;
    private double total_discount;
    private double total_tax;
    private List<DiscountModel> discounts;
    private List<ItemModel> items = new ArrayList<>();

    public double getNet_total() {
        return net_total;
    }

    public void setNet_total(double net_total) {
        this.net_total = net_total;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(double total_discount) {
        this.total_discount = total_discount;
    }

    public double getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(double total_tax) {
        this.total_tax = total_tax;
    }

    public List<DiscountModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountModel> discounts) {
        this.discounts = discounts;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }

    public void addItem(ItemModel model) {
        int itemIndex = getItemIndex(model);
        if (itemIndex == -1) {
            getItems().add(model);
        } else {
            if (model.getAmount() == 0) {
                removeItem(model);
            } else {

                ItemModel itemModel = getItems().get(itemIndex);
                if (model.getSelectedVariant()!=null){
                    if (model.getSelectedModifiers().size()>0){
                        if (itemModel.getSelectedModifiers().size()>0){
                            if (model.getSelectedModifiers().size()==itemModel.getSelectedModifiers().size())
                            {
                                for (int index = 0; index < model.getSelectedModifiers().size(); index++) {
                                    ModifierModel.Data modifierModel = model.getSelectedModifiers().get(index);
                                    ModifierModel.Data modifierData = itemModel.getSelectedModifiers().get(index);
                                    if (!modifierModel.getId().equals(modifierData.getId())) {
                                        getItems().add(model);
                                        return;
                                    }
                                }
                                int amount = itemModel.getAmount();
                                int newAmount = amount + model.getAmount();
                                model.setAmount(newAmount);
                                double netTotal = newAmount*Double.parseDouble(model.getSelectedVariant().getPrice());
                                model.setNetTotal(netTotal);
                                getItems().set(itemIndex, model);

                            }else {
                                getItems().add(model);
                            }
                        }else {
                            if (model.getSelectedVariant().getId().equals(itemModel.getSelectedVariant().getId())){
                                int amount = itemModel.getAmount();
                                int newAmount = model.getAmount()+amount;
                                model.setAmount(newAmount);
                                double netTotal = newAmount*Double.parseDouble(model.getSelectedVariant().getPrice());
                                model.setNetTotal(netTotal);
                                getItems().set(itemIndex,model);
                            }else {
                                getItems().add(model);
                            }
                        }
                    }else {
                        if (model.getSelectedVariant().getId().equals(itemModel.getSelectedVariant().getId())){
                            int amount = itemModel.getAmount();
                            int newAmount = model.getAmount()+amount;
                            model.setAmount(newAmount);
                            double netTotal = newAmount*Double.parseDouble(model.getSelectedVariant().getPrice());
                            model.setNetTotal(netTotal);
                            getItems().set(itemIndex,model);
                        }else {
                            getItems().add(model);
                        }
                    }
                }else {

                    if (model.getSelectedModifiers().size()>0){
                        if (itemModel.getSelectedModifiers().size()>0){
                            if (model.getSelectedModifiers().size()==itemModel.getSelectedModifiers().size())
                            {
                                Log.e("ttt","fff");
                                for (int index = 0; index < model.getSelectedModifiers().size(); index++) {
                                    ModifierModel.Data modifierModel = model.getSelectedModifiers().get(index);
                                    if (!extraDataFound(itemModel,modifierModel)){
                                        getItems().add(model);
                                        return;
                                    }
                                }
                                int amount = itemModel.getAmount();
                                int newAmount = amount + model.getAmount();
                                model.setAmount(newAmount);
                                getItems().set(itemIndex, model);

                            }else {
                                getItems().add(model);
                            }
                        }else {
                            if (model.getSelectedVariant().getId().equals(itemModel.getSelectedVariant().getId())){
                                int amount = itemModel.getAmount();
                                int newAmount = model.getAmount()+amount;
                                model.setAmount(newAmount);
                                getItems().set(itemIndex,model);
                            }else {
                                getItems().add(model);
                            }
                        }
                    }else {

                        if (itemModel.getId().equals(model.getId())){
                            int amount = itemModel.getAmount();
                            amount++;
                            model.setAmount(amount);
                            double netTotal = amount*Double.parseDouble(model.getPrice());
                            model.setNetTotal(netTotal);
                            getItems().set(itemIndex,model);
                        }else {
                            getItems().add(model);

                        }
                    }
                }


            }
        }
    }

    public int getItemCounts() {
        return getItems().size();
    }

    public void removeItem(ItemModel model) {
        int itemIndex = getItemIndex(model);
        if (itemIndex != -1) {
            getItems().remove(itemIndex);
        }
    }

    public double getNetTotalPrice() {
        double total = 0.0;
        double price = 0.0;

        for (ItemModel itemModel : getItems()) {
            double totalExtraPrice = 0.0;

            if (itemModel.getSelectedVariant() != null) {
                price = Double.parseDouble(itemModel.getSelectedVariant().getPrice());
            } else {
                price = Double.parseDouble(itemModel.getPrice());
            }

            for (ModifierModel.Data data : itemModel.getSelectedModifiers()) {
                totalExtraPrice += Double.parseDouble(data.getCost()) * itemModel.getAmount();
            }

            total += (price * itemModel.getAmount()) + totalExtraPrice;
        }

        return total;
    }

    public double getTotalPrice() {
        double total = 0.0;
        double price = 0.0;

        for (ItemModel itemModel : getItems()) {
            double totalExtraPrice = 0.0;
            double itemTotalPrice = 0.0;
            Log.e("data", itemModel.getName() + "__");

            if (itemModel.getSelectedVariant() != null) {
                price = Double.parseDouble(itemModel.getSelectedVariant().getPrice());
            } else {
                price = Double.parseDouble(itemModel.getPrice());
            }
            Log.e("price", price + "");

            for (ModifierModel.Data data : itemModel.getSelectedModifiers()) {
                totalExtraPrice += Double.parseDouble(data.getCost()) * itemModel.getAmount();
            }

            itemTotalPrice = (price * itemModel.getAmount()) + totalExtraPrice;
            Log.e("totPrice1_", itemTotalPrice + "");
            for (DiscountModel discountModel : itemModel.getDiscounts()) {
                double discount = 0.0;
                if (discountModel.getType().equals("val")) {
                    discount = Double.parseDouble(discountModel.getValue());
                } else {
                    discount = (Double.parseDouble(discountModel.getValue()) / 100.0) * itemTotalPrice;
                }
                Log.e("disc", discount + "_");
                itemTotalPrice -= discount;
            }

            Log.e("totPrice2_", itemTotalPrice + "");

            if (itemModel.getTax() != null) {
                double tax = (Double.parseDouble(itemModel.getTax().getRate()) / 100.0) * itemTotalPrice;

                itemTotalPrice = itemTotalPrice + tax;
                Log.e("tax", tax + "");

                Log.e("totPrice3_", itemTotalPrice + "");

            }

            total += itemTotalPrice;

            Log.e("finTot", total + "");

        }

        return total;
    }

    public double getTotalDiscountValue() {
        double totalDiscount = 0.0;
        double price = 0.0;

        for (ItemModel itemModel : getItems()) {
            double totalExtraPrice = 0.0;
            double itemTotalPrice = 0.0;

            if (itemModel.getSelectedVariant() != null) {
                price = Double.parseDouble(itemModel.getSelectedVariant().getPrice());
            } else {
                price = Double.parseDouble(itemModel.getPrice());
            }

            for (ModifierModel.Data data : itemModel.getSelectedModifiers()) {
                totalExtraPrice += Double.parseDouble(data.getCost()) * itemModel.getAmount();
            }

            itemTotalPrice = (price * itemModel.getAmount()) + totalExtraPrice;

            for (DiscountModel discountModel : itemModel.getDiscounts()) {
                double discount = 0.0;
                if (discountModel.getType().equals("val")) {
                    discount = Double.parseDouble(discountModel.getValue());
                } else {
                    discount = (Double.parseDouble(discountModel.getValue()) / 100.0) * itemTotalPrice;
                }
                totalDiscount += discount;
            }

        }
        return totalDiscount;
    }

    public double getTotalTaxPrice() {
        double totalTaxPrice = 0.0;
        double price = 0.0;

        for (ItemModel itemModel : getItems()) {
            double totalExtraPrice = 0.0;
            double itemTotalPrice = 0.0;

            if (itemModel.getSelectedVariant() != null) {
                price = Double.parseDouble(itemModel.getSelectedVariant().getPrice());
            } else {
                price = Double.parseDouble(itemModel.getPrice());
            }

            for (ModifierModel.Data data : itemModel.getSelectedModifiers()) {
                totalExtraPrice += Double.parseDouble(data.getCost()) * itemModel.getAmount();
            }

            itemTotalPrice = (price * itemModel.getAmount()) + totalExtraPrice;

            for (DiscountModel discountModel : itemModel.getDiscounts()) {
                double discount = 0.0;
                if (discountModel.getType().equals("val")) {
                    discount = Double.parseDouble(discountModel.getValue());
                } else {
                    discount = (Double.parseDouble(discountModel.getValue()) / 100.0) * itemTotalPrice;
                }
                itemTotalPrice -= discount;
            }

            if (itemModel.getTax() != null) {
                double tax = (Double.parseDouble(itemModel.getTax().getRate()) / 100.0) * itemTotalPrice;
                totalTaxPrice += tax;
            }

        }

        return totalTaxPrice;
    }


    public int getItemIndex(ItemModel itemModel) {
        int index = 0;
        for (ItemModel model : getItems()) {
            if (model.getId().equals(itemModel.getId())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private boolean extraDataFound(ItemModel itemModel,ModifierModel.Data item){

        for (ModifierModel.Data data: itemModel.getSelectedModifiers()){
            if (item.getId().equals(data.getId())){
                return true;
            }
        }

        return false;
    }

    public void clear() {
        getItems().clear();
    }

}
