package com.midad_pos.model.cart;

import android.util.Log;

import com.midad_pos.model.CustomerModel;
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
    private String delivery_id;
    private String delivery_name;
    private double remaining = 0.0;
    private double paidAmount = 0.0;
    private List<DiscountModel> discounts = new ArrayList<>();
    private List<ItemModel> items = new ArrayList<>();
    private CustomerModel customerModel;

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

    public String getDelivery_name() {
        return delivery_name;
    }

    public void setDelivery_name(String delivery_name) {
        this.delivery_name = delivery_name;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }

    public void addItem(ItemModel model,int isAddOrUpdate) {
        int itemIndex = getItemIndex(model);
        if (itemIndex == -1) {
            getItems().add(model);
        } else {
            if (model.getAmount() == 0) {
                removeItem(model);
            } else {

                ItemModel itemModel = getItems().get(itemIndex);
                if (model.getSelectedVariant() != null) {
                    if (model.getSelectedModifiers().size() > 0) {
                        if (itemModel.getSelectedModifiers().size() > 0) {
                            if (model.getSelectedModifiers().size() == itemModel.getSelectedModifiers().size()) {
                                for (int index = 0; index < model.getSelectedModifiers().size(); index++) {
                                    ModifierModel.Data modifierModel = model.getSelectedModifiers().get(index);
                                    ModifierModel.Data modifierData = itemModel.getSelectedModifiers().get(index);
                                    if (!modifierModel.getId().equals(modifierData.getId())) {
                                        getItems().add(model);
                                        return;
                                    }
                                }
                                int amount;
                                if (isAddOrUpdate==1){
                                    amount = itemModel.getAmount();
                                    int newAmount = amount + model.getAmount();
                                    model.setAmount(newAmount);
                                    double netTotal = newAmount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }else {
                                    amount = model.getAmount();
                                    model.setAmount(amount);
                                    double netTotal = amount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }
                                getItems().set(itemIndex, model);


                            } else {
                                getItems().add(model);
                            }
                        } else {
                            if (model.getSelectedVariant().getId().equals(itemModel.getSelectedVariant().getId())) {

                                int amount;
                                if (isAddOrUpdate==1){
                                    amount = itemModel.getAmount();
                                    int newAmount = amount + model.getAmount();
                                    model.setAmount(newAmount);
                                    double netTotal = newAmount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }else {
                                    amount = model.getAmount();
                                    model.setAmount(amount);
                                    double netTotal = amount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }
                                getItems().set(itemIndex, model);



                            } else {
                                getItems().add(model);
                            }
                        }
                    } else {
                        if (model.getSelectedVariant().getId().equals(itemModel.getSelectedVariant().getId())) {
                            int amount;
                            if (isAddOrUpdate==1){
                                amount = itemModel.getAmount();
                                int newAmount = amount + model.getAmount();
                                model.setAmount(newAmount);
                                double netTotal = newAmount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                model.setNetTotal(netTotal);
                            }else {
                                amount = model.getAmount();
                                model.setAmount(amount);
                                double netTotal = amount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                model.setNetTotal(netTotal);
                            }
                            getItems().set(itemIndex, model);
                        } else {
                            getItems().add(model);
                        }
                    }
                } else {

                    if (model.getSelectedModifiers().size() > 0) {
                        if (itemModel.getSelectedModifiers().size() > 0) {
                            if (model.getSelectedModifiers().size() == itemModel.getSelectedModifiers().size()) {
                                for (int index = 0; index < model.getSelectedModifiers().size(); index++) {
                                    ModifierModel.Data modifierModel = model.getSelectedModifiers().get(index);
                                    if (!extraDataFound(itemModel, modifierModel)) {
                                        getItems().add(model);
                                        return;
                                    }
                                }
                                int amount;
                                if (isAddOrUpdate==1){
                                    amount = itemModel.getAmount();
                                    int newAmount = amount + model.getAmount();
                                    model.setAmount(newAmount);
                                    double netTotal = newAmount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }else {
                                    amount = model.getAmount();
                                    model.setAmount(amount);
                                    double netTotal = amount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }
                                getItems().set(itemIndex, model);

                            } else {
                                getItems().add(model);
                            }
                        } else {
                            if (model.getSelectedVariant().getId().equals(itemModel.getSelectedVariant().getId())) {
                                int amount;
                                if (isAddOrUpdate==1){
                                    amount = itemModel.getAmount();
                                    int newAmount = amount + model.getAmount();
                                    model.setAmount(newAmount);
                                    double netTotal = newAmount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }else {
                                    amount = model.getAmount();
                                    model.setAmount(amount);
                                    double netTotal = amount * Double.parseDouble(model.getSelectedVariant().getPrice());
                                    model.setNetTotal(netTotal);
                                }
                                getItems().set(itemIndex, model);
                            } else {
                                getItems().add(model);
                            }
                        }
                    } else {

                        if (itemModel.getId().equals(model.getId())) {
                            int amount;
                            if (isAddOrUpdate==1){
                                amount = itemModel.getAmount();
                                amount++;
                                model.setAmount(amount);
                                double netTotal = amount * Double.parseDouble(model.getPrice());
                                model.setNetTotal(netTotal);
                            }else {
                                amount = model.getAmount();
                                model.setAmount(amount);
                                double netTotal = amount * Double.parseDouble(model.getPrice());
                                model.setNetTotal(netTotal);
                            }
                            getItems().set(itemIndex, model);


                        } else {
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

            for (DiscountModel discountModel:getDiscounts()){
                if (discountModel.getType().equals("val")){
                    itemTotalPrice -=Double.parseDouble(discountModel.getValue());
                }else {
                    itemTotalPrice -=(Double.parseDouble(discountModel.getValue())/100.0)*itemTotalPrice;
                }
            }




            if (itemModel.getTax() != null) {
                if (itemModel.getTax().isSingleChecked()){
                    double tax = (Double.parseDouble(itemModel.getTax().getRate()) / 100.0) * itemTotalPrice;
                    itemTotalPrice += tax;
                }

            }

            total += itemTotalPrice;


        }


        return total;
    }

    public double getTotalPriceAfterItemDiscount() {
        double total = 0.0;
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

            total += itemTotalPrice;


        }

        return total;
    }


    public double getTotalDiscountValue()
    {
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
        for (DiscountModel discountModel:getDiscounts()){
            if (discountModel.getType().equals("val")){
                totalDiscount +=getTotalPriceAfterItemDiscount()+Double.parseDouble(discountModel.getValue());
            }else {
                totalDiscount += (Double.parseDouble(discountModel.getValue())/100.0)*getTotalPriceAfterItemDiscount();
            }
        }

        return totalDiscount;
    }
    public double getTotalTaxPrice()
    {
        double price = 0.0;
        double taxValue = 0.0;


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

            for (DiscountModel discountModel:getDiscounts()){
                if (discountModel.getType().equals("val")){
                    itemTotalPrice -=Double.parseDouble(discountModel.getValue());
                }else {
                    itemTotalPrice -=(Double.parseDouble(discountModel.getValue())/100.0)*itemTotalPrice;
                }
            }




            if (itemModel.getTax() != null) {
                if (itemModel.getTax().isSingleChecked()){
                    double tax = (Double.parseDouble(itemModel.getTax().getRate()) / 100.0) * itemTotalPrice;
                    taxValue +=tax;
                }

            }



        }







        return taxValue;
    }
    public void addDiscountForAll(DiscountModel discountModel)
    {
        int index = isDiscountFound(discountModel);
        if (index == -1) {
            getDiscounts().add(discountModel);

        } else {
            getDiscounts().remove(index);
        }

        getTotalPrice();
        getTotalDiscountValue();

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

    private boolean extraDataFound(ItemModel itemModel, ModifierModel.Data item) {

        for (ModifierModel.Data data : itemModel.getSelectedModifiers()) {
            if (item.getId().equals(data.getId())) {
                return true;
            }
        }

        return false;
    }

    private int isDiscountFound(DiscountModel discountModel) {
        int index = 0;
        for (DiscountModel disc : getDiscounts()) {
            if (discountModel.getId().equals(disc.getId())) {
                return index;
            }
            index++;
        }

        return -1;
    }

    public void removeItem(int pos){
        getItems().remove(pos);
        setItems(getItems());
        getNetTotalPrice();
        getTotalPrice();
        getTotalDiscountValue();
        getTotalTaxPrice();
    }

    public void removeGeneralDiscount(List<DiscountModel> deletedDiscounts){
        List<DiscountModel> list = new ArrayList<>();
        for (DiscountModel model: getDiscounts()){
            if (!isDiscFounded(model,deletedDiscounts)){
                list.add(model);
            }
        }
        setDiscounts(list);
        getNetTotalPrice();
        getTotalPrice();
        getTotalDiscountValue();
        getTotalTaxPrice();

    }

    private boolean isDiscFounded(DiscountModel model,List<DiscountModel> discounts){

        for (DiscountModel discountModel:discounts){
            if (discountModel.getId().equals(model.getId())){
                return true;
            }
        }
        return false;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public void clear() {
        getItems().clear();
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public CustomerModel getCustomerModel() {
        return customerModel;
    }

    public void setCustomerModel(CustomerModel customerModel) {
        this.customerModel = customerModel;
    }
}
