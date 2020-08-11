package com.mobiquel.udhampur.ui.splash;

import com.mobiquel.udhampur.base.BaseModel;
import com.mobiquel.udhampur.data.preferences.PrefKeys;

public class SplashModel extends BaseModel<SplashModelListener> {

    public SplashModel(SplashModelListener listener) {
        super(listener);
    }

    @Override
    public void init() {

    }

    boolean isUserLoggedIn() {

        return getDataManager().getBooleanFromPreference(PrefKeys.IS_LOGGED_IN);
    }
    boolean isFirstTimeLaunch() {
        return getDataManager().getBooleanFromPreference(PrefKeys.IS_LAUNCH_FIRST_TIME);
    }
    String userType() {
        return getDataManager().getStringFromPreference(PrefKeys.USER_TYPE);
    }

}
