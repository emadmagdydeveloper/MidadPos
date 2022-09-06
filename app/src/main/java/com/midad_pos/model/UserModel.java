package com.midad_pos.model;

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
    }


}
