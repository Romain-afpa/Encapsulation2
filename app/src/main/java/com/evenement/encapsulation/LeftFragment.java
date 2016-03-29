package com.evenement.encapsulation;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class LeftFragment extends Fragment implements View.OnClickListener{

    private Button loginBtn;
    private EditText user;
    private EditText pass;
    private EditText server;
    private DrawerI interfaceDrawer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_left, null);

        loginBtn = (Button) view.findViewById(R.id.btn_login);
        user = (EditText) view.findViewById(R.id.input_email);
        pass = (EditText) view.findViewById(R.id.input_password);
        server = (EditText) view.findViewById(R.id.input_server);

        loginBtn.setOnClickListener(this);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String username = user.getText().toString();
        String password = pass.getText().toString();
        String server = this.server.getText().toString();

        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("server", server);

        editor.commit();

        try {
            interfaceDrawer.closeDrawer();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        WebView webView = (WebView) getActivity().findViewById(R.id.webview);
       new LoginTask(webView, getActivity(),server, username, password ).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            interfaceDrawer = (DrawerI) context;
        } catch (ClassCastException e) {
           e.printStackTrace();
        }
    }
}
