package com.example.bricola.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNewTransactionActivity extends AppCompatActivity {

    private String groupName = null;
    private static XMLManipulator groupXMLManipulator;
    private static ArrayList<String> listOfMember = new ArrayList<String>();
    private EditText transactionNameEditText = null;
    private Spinner transactionOwnerSpinner = null;
    private EditText transactionValueEditText = null;
    private Button addNewTransactionButton = null;
    private DatePicker transactionDateDatePicker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");
        this.setTitle(groupName);

        transactionNameEditText = (EditText) findViewById(R.id.transactionName_editText);
        transactionOwnerSpinner = (Spinner) findViewById(R.id.transactionOwner_spinner);
        transactionValueEditText = (EditText) findViewById(R.id.transactionValue_editText);
        transactionDateDatePicker = (DatePicker) findViewById(R.id.transactionDate_datePicker);
        addNewTransactionButton = (Button) findViewById(R.id.addNewTransaction_button);

        groupXMLManipulator = new XMLManipulator(this.getApplicationContext());
        try {
            listOfMember = groupXMLManipulator.getListMemberOfGroup(groupName);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfMember);
        transactionOwnerSpinner.setAdapter(adapter);

        addNewTransactionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Verification du contenu des EditText qui ne doivent pas être vides
                //Verification du spinner à faire
                RelativeLayout mRlayout = (RelativeLayout) findViewById(R.id.contentAddNewMember);
                for (int i = 0; i < mRlayout.getChildCount(); i++)
                    if (mRlayout.getChildAt(i) instanceof EditText) {
                        EditText myEditText = (EditText) mRlayout.getChildAt(i);
                        if (myEditText.getText().toString().matches("")) {
                            Toast.makeText(getApplication(), "Vous avez mal completer une zone de texte", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                Transaction newTransaction = null;
                String transactionName = transactionNameEditText.getText().toString();
                String transactionOwner = transactionOwnerSpinner.getSelectedItem().toString();
                Double transactionValue = Double.parseDouble(transactionValueEditText.getText().toString());

                Integer day = transactionDateDatePicker.getDayOfMonth();
                Integer month = transactionDateDatePicker.getMonth() + 1;
                Integer year =  transactionDateDatePicker.getYear();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date transactionDate = null;
                try {
                    transactionDate = df.parse(new String(checkDigit(day).toString() + "/" + checkDigit(month).toString() + "/" + year.toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                newTransaction = new Transaction(transactionName, transactionOwner, transactionValue, transactionDate);
                groupXMLManipulator = new XMLManipulator(getApplicationContext());
                groupXMLManipulator.addNewTransaction(groupName, newTransaction);

                //Retour a la fenetre du group en envoyant le nom du nouveau membrer en extra
                Intent intent = new Intent(AddNewTransactionActivity.this, GroupActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        });
    }

    public String checkDigit(int number)
    {
        return number<=9?"0"+number:String.valueOf(number);
    }

}
