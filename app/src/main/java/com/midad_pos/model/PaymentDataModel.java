package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class PaymentDataModel extends StatusResponse implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private PaymentModel cash;
        private List<PaymentModel> all;

        public PaymentModel getCash() {
            return cash;
        }

        public List<PaymentModel> getAll() {
            return all;
        }

        public void setAll(List<PaymentModel> all) {
            this.all = all;
        }
    }
}
