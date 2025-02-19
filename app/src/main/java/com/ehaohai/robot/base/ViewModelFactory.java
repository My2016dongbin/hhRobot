package com.ehaohai.robot.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile ViewModelFactory INSTANCE;

    public static ViewModelFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory();
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (modelClass.isAssignableFrom(SignContractViewModel.class)) {
//            return (T) new SignContractViewModel();
//        } else if (modelClass.isAssignableFrom(AuthorizeViewModel.class)) {
//            return (T) new AuthorizeViewModel();
//        } else if (modelClass.isAssignableFrom(IDCardRecViewModel.class)) {
//            return (T) new IDCardRecViewModel();
//        } else if (modelClass.isAssignableFrom(ChangePhoneViewModel.class)) {
//            return (T) new ChangePhoneViewModel();
//        } else if (modelClass.isAssignableFrom(FaceRecognizeViewModel.class)) {
//            return (T) new FaceRecognizeViewModel();
//        } else if (modelClass.isAssignableFrom(ContractViewModel.class)) {
//            return (T) new ContractViewModel();
//        }
        return super.create(modelClass);
    }
}
