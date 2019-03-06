/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.spleint.kida.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spleint.kida.R;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

public class Helpers {
    //String urlgooglelus = "https://plus.google.com/u/0/+Spleint";
    static String urlcommunity = "kidda@spleint.com";
    static String urltwitter = "https://twitter.com/spleint_inc";
    static String urlgithub = "https://github.com/SEzekiel/kidda";
    static String urlsource = "https://github.com/SEzekiel/kidda/issues";

    public static void showAbout(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new AboutDialog().show(ft, "dialog_about");
    }

    public static String getATEKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
    }

    public static class AboutDialog extends DialogFragment {
        public AboutDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout aboutBodyView = (LinearLayout) layoutInflater.inflate(R.layout.layout_about_dialog, null);

            TextView appversion = (TextView) aboutBodyView.findViewById(R.id.app_version_name);

            TextView googleplus = (TextView) aboutBodyView.findViewById(R.id.googleplus);
            TextView twitter = (TextView) aboutBodyView.findViewById(R.id.twitter);
            TextView github = (TextView) aboutBodyView.findViewById(R.id.github);
            TextView source = (TextView) aboutBodyView.findViewById(R.id.source);
            TextView community = (TextView) aboutBodyView.findViewById(R.id.feature_request);

            final TextView dismiss = (TextView) aboutBodyView.findViewById(R.id.dismiss_dialog);
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            googleplus.setPaintFlags(googleplus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            twitter.setPaintFlags(twitter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            github.setPaintFlags(github.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            googleplus.setVisibility(View.GONE);
//            googleplus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(urlgooglelus));
//                    startActivity(i);
//                }
//
//            });
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urltwitter));
                    startActivity(i);
                }

            });
            github.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlgithub));
                    startActivity(i);
                }

            });
            source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlsource));
                    startActivity(i);
                }
            });
            community.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    new Selection().show(getFragmentManager(),getTag());
                }
            });
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                String version = pInfo.versionName;
                int versionCode = pInfo.versionCode;
                appversion.setText("Kidda " + version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return new AlertDialog.Builder(getActivity())
                    .setView(aboutBodyView)
                    .create();
        }
    }

    public static class Selection extends DialogFragment{

        public Selection() {

        }
        public void sendReport(int reportID){
            try {
                String emailAddresses[] = {urlcommunity};
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, reportID==1?"Kidda - Feature Request":"Kidda - Bug Report");
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Kidda Team,\n\n");

                startActivity(emailIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout optionDialog = (LinearLayout) layoutInflater.inflate(R.layout.layout_feature_bug_dialog, null);

            LinearLayout featureRequest = optionDialog.findViewById(R.id.feature_request);
            LinearLayout bugReport = optionDialog.findViewById(R.id.bug_report);

            featureRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    sendReport(1);
                }
            });

            bugReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    sendReport(2);
                }
            });

            return new AlertDialog.Builder(getActivity())
                    .setView(optionDialog)
                    .create();
        }
    }
}
