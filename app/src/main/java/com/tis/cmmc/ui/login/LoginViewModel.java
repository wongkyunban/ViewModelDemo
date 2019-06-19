package com.tis.cmmc.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Patterns;

import com.tis.cmmc.data.LoginRepository;
import com.tis.cmmc.data.Result;
import com.tis.cmmc.data.model.LoggedInUser;
import com.tis.cmmc.R;

public class LoginViewModel extends ViewModel {

    // 持有一个可观察的数据LoginFormState的LiveData类
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    // 持有一个可观察的数据LoginResult的LiveData类
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    // 缓存类，所有需要共享的方法数据都在里面
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    // 返回一个可观察的数据持有者类LiveData，它持有LoginFormState数据
    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    // 返回一个可观察的数据持有者类LiveData，它持有LoginResult数据
    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    // 处理用户登录的
    public void login(String username, String password) {
        // 处理用户登录
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {// 成功登录
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));// 设置新数据，并通知观察者
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));// 设置新数据，并通知观察者
        }
    }

    // 处理表单数据变化的
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {// 验证用户名的有效性
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {// 验证密码的有效性
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {// 用户名和密码都有效
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) { // 邮件
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();// 去两边空格
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5; // 密码至少6位
    }
}
