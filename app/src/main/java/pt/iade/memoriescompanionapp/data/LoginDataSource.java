package pt.iade.memoriescompanionapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import pt.iade.memoriescompanionapp.data.model.Consts;
import pt.iade.memoriescompanionapp.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password, Integer id) {
        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser User =
                    new LoggedInUser(
                            id.toString(),
                            username);

            Consts.currentUser = User;
            return new Result.Success<>(User);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        Consts.currentUser = null;
    }
}