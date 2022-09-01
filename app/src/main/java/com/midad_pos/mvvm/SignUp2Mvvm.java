package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.model.BusinessTypeModel;
import com.midad_pos.model.SignUpModel;

import java.util.ArrayList;
import java.util.List;

public class SignUp2Mvvm extends AndroidViewModel {
    private SignUpModel signUpModel;
    private MutableLiveData<SignUpModel> signUpModelMutableLiveData;
    private MutableLiveData<List<BusinessTypeModel>> businessType;
    private int oldPos = 0;

    public SignUp2Mvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<SignUpModel> getSignUpModel(){
        if (signUpModelMutableLiveData==null){
            signUpModelMutableLiveData = new MutableLiveData<>();
        }
        return signUpModelMutableLiveData;
    }

    public MutableLiveData<List<BusinessTypeModel>> getBusinessType(){
        if (businessType==null){
            businessType = new MutableLiveData<>();
            List<BusinessTypeModel> list = new ArrayList<>();
            list.add(new BusinessTypeModel("نوع العمل","Business type"));
            list.add(new BusinessTypeModel("مقهى","Coffee shop/cafe"));
            list.add(new BusinessTypeModel("مطعم","Restaurant"));
            list.add(new BusinessTypeModel("حانة","Bar"));
            list.add(new BusinessTypeModel("شاحنة الغذاء","Food truck"));
            list.add(new BusinessTypeModel("خضروات","Grocery"));
            list.add(new BusinessTypeModel("الجمال والعناية الشخصية","Beauty and Personal care"));
            list.add(new BusinessTypeModel("الملابس","Clothing"));
            list.add(new BusinessTypeModel("إلكترونيات","Electronics"));
            list.add(new BusinessTypeModel("مجوهرات و إكسسوارات","Jewelry and Accessories"));
            list.add(new BusinessTypeModel("راحة وترفية","Leisure and Entertainment"));
            list.add(new BusinessTypeModel("طب و صيدلة","Medicine and Pharmacy"));
            list.add(new BusinessTypeModel("حيوانات أليفة","Pets"));
            list.add(new BusinessTypeModel("منتجات رياضية","Sporing goods"));
            list.add(new BusinessTypeModel("وسائل النقل","Transportation"));
            list.add(new BusinessTypeModel("تدخين","Vape and Smoke"));
            list.add(new BusinessTypeModel("نبيذ والمشروبات الروحية","Wine and Spirits"));
            list.add(new BusinessTypeModel("اخرى","Other"));
            businessType.setValue(list);
        }
        return businessType;
    }

    public void updateList(int pos){
        if (pos==0){
            oldPos = 0;
        }else {
            if (oldPos!=0){
                businessType.getValue().get(oldPos).setSelected(false);
            }
            businessType.getValue().get(pos).setSelected(true);
            oldPos = pos;
        }

        businessType.setValue(businessType.getValue());

    }
    public void setSignUpModel(SignUpModel signUpModel){
        this.signUpModel = signUpModel;
        getSignUpModel().setValue(signUpModel);
    }
}
