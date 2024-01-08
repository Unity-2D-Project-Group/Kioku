package pt.iade.memoriescompanionapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pt.iade.memoriescompanionapp.classes.APIUser;
import pt.iade.memoriescompanionapp.data.LoginRepository;
import pt.iade.memoriescompanionapp.data.Result;
import pt.iade.memoriescompanionapp.data.model.LoggedInUser;
import pt.iade.memoriescompanionapp.R;
import pt.iade.memoriescompanionapp.utilities.WebRequest;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<Integer> future = executorService.submit(() -> {
            try{
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/users/auth"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                String result = webRequest.performGetRequest(params);
                try{
                    Gson gson = new Gson();

                    JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

                    // Define the Java class you want to convert the JSON data into
                    APIUser person = gson.fromJson(jsonObject, APIUser.class);
                    return person.user_id;
                }catch(Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return 0;
        });

        try {
            Integer result = future.get();
            Result<LoggedInUser> logResult = loginRepository.login(username, password, result);
            if (logResult instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) logResult).getData();
                loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
            } else {
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 0;
    }
}

