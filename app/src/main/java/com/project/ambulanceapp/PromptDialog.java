package com.project.ambulanceapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PromptDialog {

    private Dialog dialog;
    private Activity activity;

    public PromptDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoading() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loader_dialog);
        dialog.show();
    }

    public void stopLoading(){
        dialog.dismiss();
    }

    public void showAlert(final Context ctx, String message) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setPositiveButton(ctx.getString(R.string.ok), null);
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertScale;
        dialog.show();
    }

    public void showToast(Context ctx, String msg){

        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

    }

    public static String convertToDate(String dateString){

        DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = (Date) parser.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());

        return formatter.format(date);
    }

    private SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean("logged_in", loggedIn);
        editor.apply();
    }

    public boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean("logged_in", false);
    }

//    public void saveUserLoginDetails(Context ctx, String surname, String other_names,
//                                     String email, String user_id) {
//        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_details", Context.MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
//        prefsEditor.putString("surname", surname);
//        prefsEditor.putString("other_names", other_names);
//        prefsEditor.putString("email", email);
//        prefsEditor.putString("user_id", user_id);
//        prefsEditor.apply();
//    }

    public void setLoginType(Context ctx, String type) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("type", type);
        prefsEditor.apply();
    }

    public void deleteUserLoginDetails(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }

    public String getUserLoginType(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user_details", 0);
        return pref.getString("type", "0");
    }

    public static String toCamelCase(String input) {
        input = input.toLowerCase();
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
//        return input;
    }


}
