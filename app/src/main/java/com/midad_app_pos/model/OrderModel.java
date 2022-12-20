package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {
    private String date;
    private List<Sale> sales;

    public String getDate() {
        return date;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public static class Sale implements Serializable {
        private String id;
        private String user_id;
        private String name;
        private String warehouse_id;
        private String biller_id;
        private String customer_id;
        private String date;
        private String total_qty;
        private String total_discount;
        private String total_tax;
        private String total_price;
        private String item;
        private String order_tax;
        private String grand_total;
        private DeliveryModel dining;
        private String coupon_discount;
        private String sale_status;
        private String coupon_id;
        private String paid_amount;
        private String sale_note;
        private String staff_note;
        private String order_discount_type;
        private String order_discount_value;
        private String order_discount;
        private String order_tax_rate;
        private String shipping_cost;
        private String payment_status;
        private String created_at;
        private String reference_no;
        private User user;
        private WereHouse warehouse;
        private POSModel pos;
        private CustomerModel customer;
        private List<Detail> details;
        private List<Payment> payments;
        private List<OrderDiscount> discounts;
        private boolean isSelected;
        private boolean show;
        private String order_date;

        public String getId() {
            return id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getWarehouse_id() {
            return warehouse_id;
        }

        public String getBiller_id() {
            return biller_id;
        }

        public String getName() {
            return name;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public String getTotal_qty() {
            return total_qty;
        }

        public String getDate() {
            return date;
        }

        public DeliveryModel getDining() {
            return dining;
        }

        public String getTotal_discount() {
            return total_discount;
        }

        public String getTotal_tax() {
            return total_tax;
        }

        public String getTotal_price() {
            return total_price;
        }

        public String getItem() {
            return item;
        }

        public String getOrder_tax() {
            return order_tax;
        }

        public String getGrand_total() {
            return grand_total;
        }

        public String getCoupon_discount() {
            return coupon_discount;
        }

        public String getSale_status() {
            return sale_status;
        }

        public String getCoupon_id() {
            return coupon_id;
        }

        public String getPaid_amount() {
            return paid_amount;
        }

        public String getSale_note() {
            return sale_note;
        }

        public String getStaff_note() {
            return staff_note;
        }

        public String getOrder_discount_type() {
            return order_discount_type;
        }

        public String getOrder_discount_value() {
            return order_discount_value;
        }

        public String getOrder_discount() {
            return order_discount;
        }

        public String getOrder_tax_rate() {
            return order_tax_rate;
        }

        public String getOrder_date() {
            return order_date;
        }

        public void setOrder_date(String order_date) {
            this.order_date = order_date;
        }

        public String getShipping_cost() {
            return shipping_cost;
        }

        public String getPayment_status() {
            return payment_status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getReference_no() {
            return reference_no;
        }

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public User getUser() {
            return user;
        }

        public WereHouse getWarehouse() {
            return warehouse;
        }

        public POSModel getPos() {
            return pos;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public List<Payment> getPayments() {
            return payments;
        }

        public List<OrderDiscount> getDiscounts() {
            return discounts;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public CustomerModel getCustomer() {
            return customer;
        }
    }



    public static class Detail implements Serializable {
        private String id;
        private String sale_id;
        private String product_id;
        private String product_batch_id;
        private String variant_id;
        private String imei_number;
        private String qty;
        private String sale_unit_id;
        private String net_unit_price;
        private String discount;
        private String tax_rate;
        private String tax;
        private String total;
        private String grand_total;
        private String comment;
        private String created_at;
        private String updated_at;
        private String category_id;
        private String total_cost;
        private List<SaleModifierData> sale_modifiers;
        private List<OrderDiscount> discounts;
        private VariantModel variant;
        private ItemModel product;

        public String getId() {
            return id;
        }

        public String getSale_id() {
            return sale_id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public String getProduct_batch_id() {
            return product_batch_id;
        }

        public String getVariant_id() {
            return variant_id;
        }

        public String getImei_number() {
            return imei_number;
        }

        public String getQty() {
            return qty;
        }

        public String getGrand_total() {
            return grand_total;
        }

        public String getSale_unit_id() {
            return sale_unit_id;
        }

        public String getNet_unit_price() {
            return net_unit_price;
        }

        public String getDiscount() {
            return discount;
        }

        public String getTax_rate() {
            return tax_rate;
        }

        public String getTax() {
            return tax;
        }

        public String getTotal() {
            return total;
        }

        public String getComment() {
            return comment;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getCategory_id() {
            return category_id;
        }

        public String getTotal_cost() {
            return total_cost;
        }

        public List<SaleModifierData> getSale_modifiers() {
            return sale_modifiers;
        }

        public List<OrderDiscount> getDiscounts() {
            return discounts;
        }

        public ItemModel getProduct() {
            return product;
        }

        public VariantModel getVariant() {
            return variant;
        }
    }


    public static class Modifier implements Serializable {
        private String id;
        private String title;
        private String user_id;
        private String is_active;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getIs_active() {
            return is_active;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }


    public static class SaleModifier implements Serializable {
        private String id;
        private String sale_modifier_id;
        private String modifier_id;
        private String product_id;
        private String sale_id;
        private String product_sale_id;
        private String modifier_data_id;
        private String net_cost;
        private String qty;
        private String total_cost;
        private String created_at;
        private String updated_at;
        private ModifierModel.Data modifier_data;

        public String getId() {
            return id;
        }

        public String getSale_modifier_id() {
            return sale_modifier_id;
        }

        public String getModifier_id() {
            return modifier_id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public String getSale_id() {
            return sale_id;
        }

        public String getProduct_sale_id() {
            return product_sale_id;
        }

        public String getModifier_data_id() {
            return modifier_data_id;
        }

        public String getNet_cost() {
            return net_cost;
        }

        public String getQty() {
            return qty;
        }

        public String getTotal_cost() {
            return total_cost;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public ModifierModel.Data getModifier_data() {
            return modifier_data;
        }
    }

    public static class Payment implements Serializable {
        private String id;
        private String sale_id;
        private String total;
        private String paid;
        private String remaining;
        private String pay_id;
        private String created_at;
        private String updated_at;
        private PaymentModel payment;

        public String getId() {
            return id;
        }

        public String getSale_id() {
            return sale_id;
        }

        public String getTotal() {
            return total;
        }

        public String getPaid() {
            return paid;
        }

        public String getRemaining() {
            return remaining;
        }

        public String getPay_id() {
            return pay_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public PaymentModel getPayment() {
            return payment;
        }
    }

    public static class OrderDiscount implements Serializable {
        private String id;
        private String sale_id;
        private String discount_id;
        private String product_sale_id;
        private String product_id;
        private String type;
        private String value;
        private String grand_total;
        private String created_at;
        private String updated_at;
        private DiscountModel discount;

        public String getId() {
            return id;
        }

        public String getSale_id() {
            return sale_id;
        }

        public String getDiscount_id() {
            return discount_id;
        }

        public String getProduct_sale_id() {
            return product_sale_id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getGrand_total() {
            return grand_total;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public DiscountModel getDiscount() {
            return discount;
        }
    }

    public static class SaleModifierData implements Serializable {
        private String id;
        private String product_id;
        private String sale_id;
        private String product_sale_id;
        private String modifier_id;
        private String total;
        private String created_at;
        private String updated_at;
        private Modifier modifier;
        private List<SaleModifier> sale_modifier_data;

        public String getId() {
            return id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public String getSale_id() {
            return sale_id;
        }

        public String getProduct_sale_id() {
            return product_sale_id;
        }

        public String getModifier_id() {
            return modifier_id;
        }

        public String getTotal() {
            return total;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public Modifier getModifier() {
            return modifier;
        }

        public List<SaleModifier> getSale_modifier_data() {
            return sale_modifier_data;
        }
    }


}
