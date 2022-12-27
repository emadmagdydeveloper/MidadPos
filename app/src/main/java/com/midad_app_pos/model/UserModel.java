package com.midad_app_pos.model;

import java.io.Serializable;
import java.util.List;

public class UserModel extends StatusResponse implements Serializable {
    private Data data;
    public Data getData() {
        return data;
    }

    public static class Data implements Serializable {
        private User user;
        private List<User> anotherUsers;
        private User selectedUser;
        private WereHouse selectedWereHouse;
        private POSModel selectedPos;
        private InvoiceSettings invoiceSettings;


        public User getUser() {
            return user;
        }

        public List<User> getAnotherUsers() {
            return anotherUsers;
        }

        public User getSelectedUser() {
            return selectedUser;
        }

        public void setSelectedUser(User selectedUser) {
            this.selectedUser = selectedUser;
        }

        public WereHouse getSelectedWereHouse() {
            return selectedWereHouse;
        }

        public void setSelectedWereHouse(WereHouse selectedWereHouse) {
            this.selectedWereHouse = selectedWereHouse;
        }

        public POSModel getSelectedPos() {
            return selectedPos;
        }

        public void setSelectedPos(POSModel selectedPos) {
            this.selectedPos = selectedPos;
        }

        public InvoiceSettings getInvoiceSettings() {
            return invoiceSettings;
        }

        public void setInvoiceSettings(InvoiceSettings invoiceSettings) {
            this.invoiceSettings = invoiceSettings;
        }
    }


}
